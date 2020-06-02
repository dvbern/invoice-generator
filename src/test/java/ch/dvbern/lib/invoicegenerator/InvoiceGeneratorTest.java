/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.dvbern.lib.invoicegenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.Invoice;
import ch.dvbern.lib.invoicegenerator.dto.InvoiceGeneratorConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.QRCodeEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.SummaryEntry;
import ch.dvbern.lib.invoicegenerator.dto.component.Logo;
import ch.dvbern.lib.invoicegenerator.dto.component.PhraseRenderer;
import ch.dvbern.lib.invoicegenerator.dto.position.H1Position;
import ch.dvbern.lib.invoicegenerator.dto.position.H2Position;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPosition;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPositionColumnTitle;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities;
import com.lowagie.text.Font;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.lib.invoicegenerator.TestUtil.containsFonts;
import static ch.dvbern.lib.invoicegenerator.TestUtil.createFile;
import static ch.dvbern.lib.invoicegenerator.TestUtil.withPages;
import static ch.dvbern.lib.invoicegenerator.dto.PageConfiguration.TOP_PAGE_DEFAULT_MARGIN_MM;
import static ch.dvbern.lib.invoicegenerator.dto.component.AddressComponent.RECHTE_ADRESSE_LEFT_MARGIN_MM;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.createFontWithSize;
import static com.lowagie.text.Utilities.millimetersToPoints;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class InvoiceGeneratorTest {

	private static final Logger LOG = LoggerFactory.getLogger(InvoiceGeneratorTest.class);
	private static final String TITEL = "Rechnung";
	private static final String LONG_STRING = "Keine gelungene Landung in 21 Weltcup-Bewerben. Kein Top-Ten-Platz! "
		+ "Simon Ammann (35) klassiert sich im Gesamtweltcup auf Platz 29 – zu wenig für die Nationalmannschaft. "
		+ "Berni Schödler, Chef Skisprung Swiss-Ski, erklärt: «Simon hätte sich in den Top 25 klassieren müssen, "
		+ "das hat er nicht geschafft. Deshalb wird er ins A-Kader zu Gregor Deschwanden und Kilian Peier "
		+ "zurückversetzt.»";
	private static final String PP_ADRESS_ZUSATZ = "P.P. CH-3030 Bern Post CH AG";
	private static final int NUMBER_OF_INVOICES = 100;
	private static final int TEST_NR_OF_DIFFEREN_CONTENT = 60;
	private static final int MAX_MAIN_MEMORY_BYTES = 100000000;

	private final List<SummaryEntry> total = Arrays.asList(
		new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
		new SummaryEntry("Total", "CHF 3'488.00", true, true),
		new SummaryEntry("Zahlung 30.03.18", "CHF 45.55"),
		new SummaryEntry("Zahlung 17.03.17", "CHF 2'500.00"),
		new SummaryEntry("Ausstehend", "CHF 892.45", true, true));

	private final List<SummaryEntry> summary = Arrays.asList(
		new SummaryEntry("Rechnungs-Nr.", "PDVB711"),
		new SummaryEntry("Kunden-Nr.", "PDV343242332"),
		new SummaryEntry("Datum", "17.03.2017"),
		new SummaryEntry("Fällig am", "17.04.2017"),
		new SummaryEntry("Total", "CHF 3'488.00", true, false),
		new SummaryEntry("Ausstehend", "CHF 892.45", true, true));

	private final List<String> einleitung = Arrays.asList(
		"Kita Kinderland", "Januar 2018", "Referenznr. " + TestDataUtil.ESR_REFERENCE);

	private final List<String> zahlungskonto = Arrays.asList("Geschäftskonto:", "Postfinance");
	private final List<String> konditionen = Collections.singletonList("Zahlbar innerhalb 30 Tagen.");
	private final List<String> looongStrings = Arrays.asList(LONG_STRING, LONG_STRING, LONG_STRING);
	private final Logo logo = TestDataUtil.defaultLogo();
	private final RechnungsPositionColumnTitle columnTitle = new RechnungsPositionColumnTitle(
		"Dienstleistung", "Menge", "Preis", "Total");
	private final List<String> headerLines = Arrays.asList(
		"DV Bern AG", "Nussbaumstrasse 21", "3006 Bern", "hello@kitadmin.ch");
	private final PhraseRenderer header = new PhraseRenderer(headerLines, RECHTE_ADRESSE_LEFT_MARGIN_MM, 11, 80, 30);
	private final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.LEFT);
	private final List<String> adresse = TestDataUtil.toLines(TestDataUtil.DEBTOR);

	private final List<Position> positionen = Arrays.asList(
		new H1Position("Lovelace Ada"),
		new H2Position("20% private Betreuung: 01.01.2018 - 31.01.2018"),
		new RechnungsPosition("Betreuungsgebühr", "1", "1'728.00", "1'728.00"),
		new RechnungsPosition("Verpflegung", "4", "10.00", "40.00"),
		new H2Position("Gutschrift, 05.01.2018"),
		new RechnungsPosition("Vereinsbeitrag 2018", "1", "-48.00", "-48.00"),
		new H1Position("Lovelace Allegra"),
		new H2Position("20% private Betreuung: 01.01.2018 - 31.01.2018"),
		new RechnungsPosition("Betreuungsgebühr", "1", "1'728.00", "1'728.00"),
		new RechnungsPosition("Verpflegung", "4", "10.00", "40.00"));

	private final OrangerEinzahlungsschein orangerEinzahlungsschein = TestDataUtil.ORANGER_EINZAHLUNGSSCHEIN;
	private final QRCodeEinzahlungsschein qrCodeEinzahlungsschein = TestDataUtil.QR_CODE_EINZAHLUNGSSCHEIN;

	@BeforeEach
	public void init() {
		initConfiguration(configuration);
	}

	private void initConfiguration(@Nonnull InvoiceGeneratorConfiguration config) {
		config.setZahlungsKonditionen(zahlungskonto);
		config.setPp(PP_ADRESS_ZUSATZ);
		config.setLogo(logo);
		config.setHeader(header);
		config.setEinzahlungsscheinNotOnPageOne(false);
		config.setTopMarginInPoints(millimetersToPoints(TOP_PAGE_DEFAULT_MARGIN_MM));
		config.setAddEsrBackgroundImage(true);
	}

	@Test
	public void testTheCreationOfASampleInvoice() throws InvoiceGeneratorException, IOException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse, orangerEinzahlungsschein,
			positionen, total, konditionen);

		assertThat(createFile(invoiceGenerator, invoice, "target/Invoice.pdf"), anExistingFile());
	}

	@Test
	public void testTheCreationOfASampleInvoiceWithShortReferenzeNummer() throws Exception {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);

		OrangerEinzahlungsschein einzahlungsschein =
			new OrangerEinzahlungsschein(
				TestDataUtil.toLines(TestDataUtil.CREDITOR),
				new BigInteger("1236"),
				TestDataUtil.AMOUNT,
				TestDataUtil.ESR_ACOUNT,
				TestDataUtil.toLines(TestDataUtil.DEBTOR));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse, einzahlungsschein,
			positionen, total, konditionen);

		assertThat(createFile(invoiceGenerator, invoice, "target/InvoiceShortESR.pdf"), anExistingFile());
	}

	@Test
	public void testTheCreationOfASampleInvoiceOnOnePageWithAllConfigurations()
		throws InvoiceGeneratorException, IOException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);

		List<SummaryEntry> totalEntires = Arrays.asList(
			new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
			new SummaryEntry("Total", "CHF 3'488.00", true, true),
			new SummaryEntry("Ausstehend", "CHF 3'488.00", true, true));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, null,
			adresse, orangerEinzahlungsschein, positionen.subList(0, 2), totalEntires, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceOnePage.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(1)
		));
	}

	@Test
	public void testTheCreationOfASampleInvoiceOnOnePageWithEinzahlungsscheinOnPage2()
		throws InvoiceGeneratorException, IOException {

		configuration.setEinzahlungsscheinNotOnPageOne(true);

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);

		List<SummaryEntry> totalEntires = Arrays.asList(
			new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
			new SummaryEntry("Total", "CHF 3'488.00", true, true),
			new SummaryEntry("Ausstehend", "CHF 3'488.00", true, true));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, null,
			adresse, orangerEinzahlungsschein, positionen.subList(0, 3), totalEntires, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceOnePageEinzahlunggscheinPage2.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(2)
		));
	}

	@Test
	public void testTheCreationOfASampleQRCode() throws Exception {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);

		List<SummaryEntry> totalEntires = Arrays.asList(
			new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
			new SummaryEntry("Total", "CHF 3'488.00", true, true),
			new SummaryEntry("Ausstehend", "CHF 3'488.00", true, true));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, null,
			adresse, qrCodeEinzahlungsschein, positionen.subList(0, 3), totalEntires, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceQRCode.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(1)
		));
	}

	@Test
	public void testTheCreationOfASampleInvoiceOnOnePageWithQRCodeOnPage2()
		throws InvoiceGeneratorException, IOException {

		configuration.setEinzahlungsscheinNotOnPageOne(true);

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);

		List<SummaryEntry> totalEntires = Arrays.asList(
			new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
			new SummaryEntry("Total", "CHF 3'488.00", true, true),
			new SummaryEntry("Ausstehend", "CHF 3'488.00", true, true));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, null,
			adresse, qrCodeEinzahlungsschein, positionen.subList(0, 3), totalEntires, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceOnePageQRCodePage2.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(2)
		));
	}

	@Test
	public void testBlankNewPageIsPrinted()
		throws InvoiceGeneratorException, IOException {
		configuration.setZahlungsKonditionen(null);
		// The combination of margins and page content is vital. To reproduce we adjust the top margin to make the
		// issue reproducable
		configuration.setTopMarginInPoints(configuration.getTopMarginInPoints() - 12);
		configuration.addDummyESR();
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);

		List<SummaryEntry> totalEntires = Arrays.asList(
			new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
			new SummaryEntry("Total", "CHF 3'488.00", true, true),
			new SummaryEntry("Ausstehend", "CHF 3'488.00", true, true));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, Collections.singletonList("Mai 2017"),
			adresse, orangerEinzahlungsschein, positionen.subList(0, 4), totalEntires, null);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceAddsNewPage.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(2)
		));
	}

	@Test
	public void testTheCreationOfASampleInvoiceOnOnePageWithoutEinzahlungsschein()
		throws InvoiceGeneratorException, IOException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);

		List<SummaryEntry> totalEntires = Arrays.asList(
			new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
			new SummaryEntry("Total", "CHF 3'488.00", true, true),
			new SummaryEntry("Ausstehend", "CHF 3'488.00", true, true));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, Collections.singletonList("Mai 2017"),
			adresse, null, positionen.subList(0, 10), totalEntires, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceOnePageWithoutEZ.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(1)
		));
	}

	@Test
	public void testTheCreationOfASampleInvoiceWithRightAddress()
		throws InvoiceGeneratorException, IOException {
		final InvoiceGeneratorConfiguration configurationRight = new InvoiceGeneratorConfiguration(Alignment.RIGHT);
		configurationRight.setPp(PP_ADRESS_ZUSATZ);
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configurationRight);
		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse, orangerEinzahlungsschein,
			positionen, total, konditionen);

		assertThat(createFile(invoiceGenerator, invoice, "target/InvoiceRight.pdf"), anExistingFile());
	}

	@Test
	public void testTheCreationOfASampleInvoiceWithLotOfLinesInTheOutro()
		throws InvoiceGeneratorException, IOException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse, orangerEinzahlungsschein,
			positionen, total, konditionen);

		assertThat(createFile(invoiceGenerator, invoice, "target/InvoiceOutro.pdf"), anExistingFile());
	}

	@Test
	public void testTheCreationOfAInvoiceWithLooooongContent()
		throws InvoiceGeneratorException, IOException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = new Invoice(columnTitle, LONG_STRING, summary, looongStrings, adresse,
			orangerEinzahlungsschein, positionen, total, konditionen);

		assertThat(createFile(invoiceGenerator, invoice, "target/InvoiceXL.pdf"), anExistingFile());
	}

	@Test
	public void testTheCreationOfAInvoiceWithSpecifcKonditionen()
		throws InvoiceGeneratorException, IOException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		List<String> specKonditionen = Arrays.asList(
			"Bei der nächsten Mahnung wird eine zusätzliche Bearbeitungsgebühr von 30.- fällig.",
			"Zahlbar innert 10 Tagen.");
		final Invoice invoice = new Invoice(columnTitle, LONG_STRING, summary, looongStrings, adresse,
			orangerEinzahlungsschein, positionen, total, specKonditionen);

		assertThat(createFile(invoiceGenerator, invoice, "target/InvoiceKonditionen.pdf"), anExistingFile());
	}

	@Test
	public void testTheCreationOf100Invoices() throws InvoiceGeneratorException, IOException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse, orangerEinzahlungsschein,
			positionen, total, konditionen);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_INVOICES; i++) {
			//noinspection StringConcatenationMissingWhitespace
			String filename = "target/zBulkInvoice" + (i + 1) + ".pdf";
			assertThat(createFile(invoiceGenerator, invoice, filename), anExistingFile());
		}
		long time = System.currentTimeMillis() - startTime;
		LOG.info(String.format("%1$d invoices createt in %2$dms (%3$dms/s)", NUMBER_OF_INVOICES, time, (time /
			NUMBER_OF_INVOICES)));
	}

	@Test
	public void testThePageBreakingWithCreatingDocumentsWithDifferentContent()
		throws InvoiceGeneratorException, IOException {
		final List<Position> growingPositions = new ArrayList<>();
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		PDFMergerUtility merger = new PDFMergerUtility();
		for (int i = 0; i < TEST_NR_OF_DIFFEREN_CONTENT; i++) {
			growingPositions.add(new RechnungsPosition("Betreuungsgebühr", "1", "1'728.00", "1'728.00"));
			final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse,
				orangerEinzahlungsschein, growingPositions, total, konditionen);
			ByteArrayOutputStream byteArrayOutputStream = invoiceGenerator.generateInvoice(invoice);
			ByteArrayInputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			merger.addSource(stream);
		}
		String mergedFileName = "target/InvoicesWithDifferentContent.pdf";
		merger.setDestinationFileName(mergedFileName);
		merger.mergeDocuments(MemoryUsageSetting.setupMixed(MAX_MAIN_MEMORY_BYTES));

		assertThat(new File(mergedFileName), anExistingFile());
	}

	@Test
	public void testByteArrayOutputStream() throws Exception {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse,
			orangerEinzahlungsschein, positionen, total, konditionen);
		ByteArrayOutputStream outputStream = invoiceGenerator.generateInvoice(invoice);

		String path = "target/StreamedInvoice.pdf";
		FileOutputStream fileOutputStream = new FileOutputStream(path);
		outputStream.writeTo(fileOutputStream);

		assertThat(new File(path), anExistingFile());
	}

	@Test
	public void testDummyESROnAllPages() throws Exception {
		InvoiceGeneratorConfiguration config = new InvoiceGeneratorConfiguration(Alignment.RIGHT);
		initConfiguration(config);
		config.addDummyESR();
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(config);

		Invoice invoice = new Invoice(
			columnTitle,
			TITEL,
			summary,
			einleitung,
			adresse,
			orangerEinzahlungsschein,
			positionen,
			total,
			null);

		File file = createFile(invoiceGenerator, invoice, "target/DummyESR.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(2)
		));
	}

	@Test
	public void testTheCreationOfASampleInvoiceWithEinzahlungsscheinOnPage2()
		throws InvoiceGeneratorException, IOException {

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		configuration.setEsrTopOffsetInMm(-10);
		configuration.setEsrLeftOffsetInMm(-3);

		List<SummaryEntry> totalEntires = Arrays.asList(
			new SummaryEntry("Subtotal", "CHF 3'488.00", false, true),
			new SummaryEntry("Total", "CHF 3'488.00", true, true),
			new SummaryEntry("Ausstehend", "CHF 3'488.00", true, true));

		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, null,
			adresse, orangerEinzahlungsschein, positionen.subList(0, 5), totalEntires, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceEinzahlunggscheinPage2.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			withPages(2)
		));
	}

	@Test
	public void testFontsAreConfigurable() throws Exception {
		InvoiceGeneratorConfiguration config = new InvoiceGeneratorConfiguration(Alignment.LEFT);
		initConfiguration(config);

		Font baseFont = new Font(Font.HELVETICA, 8);
		config.setFont(baseFont);

		Font fontBold = new Font(baseFont);
		fontBold.setStyle(Font.BOLDITALIC);

		Font fontItalic = new Font(baseFont);
		fontItalic.setStyle(Font.ITALIC);

		final PhraseRenderer footer = new PhraseRenderer(
			Collections.singletonList("Footer"),
			42,
			280,
			80,
			10,
			OnPage.NOT_LAST,
			createFontWithSize(fontItalic, 5),
			Alignment.LEFT,
			5);
		config.setFooter(footer);

		config.setFontBold(fontBold);
		config.setFontTitle(PdfUtilities.createFontWithSize(fontBold, 12));
		config.setFontH1(PdfUtilities.createFontWithSize(baseFont, 12));
		config.setFontH2(PdfUtilities.createFontWithSize(fontBold, 8));

		config.getPositionStrategyMap().get(RechnungsPosition.class)
			.setFont(fontItalic);

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(config);
		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse,
			orangerEinzahlungsschein, positionen, total, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceFonts.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			containsFonts(
				equalTo("Helvetica"),
				equalTo("Helvetica-BoldOblique"),
				equalTo("Helvetica-Oblique"),
				containsString("ArialMT"))
		));
	}

	@Test
	public void testMultipliedLeading() throws Exception {
		InvoiceGeneratorConfiguration config = new InvoiceGeneratorConfiguration(Alignment.LEFT);
		initConfiguration(config);

		config.setMultipliedLeadingH1(5);
		config.setMultipliedLeadingH2(4);
		config.setMultipliedTitleLeading(3);
		config.setMultipliedLeadingDefault(2);
		config.getPositionStrategyMap().get(RechnungsPosition.class)
			.setMultipliedLeading(1.0f);

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(config);
		final Invoice invoice = new Invoice(columnTitle, TITEL, summary, einleitung, adresse,
			orangerEinzahlungsschein, positionen, total, konditionen);

		File file = createFile(invoiceGenerator, invoice, "target/InvoiceMultipliedLeading.pdf");

		assertThat(file, anExistingFile());
	}
}
