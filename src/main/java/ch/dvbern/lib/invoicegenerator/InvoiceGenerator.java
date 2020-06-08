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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.Invoice;
import ch.dvbern.lib.invoicegenerator.dto.InvoiceGeneratorConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.SummaryEntry;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentRenderer;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.Einzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.EinzahlungsscheinConfiguration;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorException;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfGenerator;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;

import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.ESR_HEIGHT_WITH_MARGIN;
import static com.lowagie.text.PageSize.A4;
import static com.lowagie.text.Utilities.millimetersToPoints;

/**
 * Mit dem InvoiceGenerator können PDF-Rechnungen mit oder ohne ESR erstellt werden. Der Generator wurde optimiert,
 * um von einem Empfänger innerhalb eines Rechnungslaufs
 * möglichst schnell Rechnungen zu generieren.
 *
 * @author Xaver Weibel
 */
public class InvoiceGenerator extends BaseGenerator<InvoiceGeneratorConfiguration> {

	/**
	 * Erstellt einen neuen InvoiceGenerator für die ensprechende Konfiguration.
	 *
	 * @param configuration Die Konfiguration beinhaltet das Layout der Rechnungen (Header, Footer, Zahlungskonto, ...)
	 * sowie statische Elemente, die bei sämtlichen Rechnungen gleich bleiben, z.B. Zahlungskonditionen
	 */
	public InvoiceGenerator(@Nonnull final InvoiceGeneratorConfiguration configuration) {
		super(configuration);
	}

	/**
	 * Erstellt aus dem Invoice-Objekt ein PDF.
	 *
	 * @param invoice Das Invoice-Objekt aus welchem die Rechnung erstellt wird
	 * @return ein {@link ByteArrayOutputStream} der das PDF enthält
	 * @throws InvoiceGeneratorException Wird bei bei technischen Problemen geworfen, z.B. falls das Logo nicht
	 *                                   geladen werden kann
	 */
	@Nonnull
	public ByteArrayOutputStream generateInvoice(@Nonnull Invoice invoice) throws InvoiceGeneratorException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		generateInvoice(out, invoice);

		return out;
	}

	/**
	 * Erstellt aus dem Invoice-Objekt ein PDF.
	 *
	 * @param outputStream Das PDF wird in diesen Stream geschrieben
	 * @param invoice Das Invoice-Objekt aus welchem die Rechnung erstellt wird
	 * @throws InvoiceGeneratorException Wird bei bei technischen Problemen geworfen, z.B. falls das Logo nicht
	 *                                   geladen werden kann
	 */
	public void generateInvoice(@Nonnull OutputStream outputStream, @Nonnull Invoice invoice)
		throws InvoiceGeneratorException {

		InvoiceGeneratorConfiguration configuration = getConfiguration();

		List<ComponentRenderer<? extends ComponentConfiguration, ?>> componentRenderers =
			getComponentRenderers(invoice.getAdresse());

		Einzahlungsschein einzahlungsschein = invoice.getEinzahlungsschein();
		if (einzahlungsschein != null) {
			EinzahlungsscheinConfiguration einzahlungsscheinConfig = configuration.getEinzahlungsscheinConfiguration();
			componentRenderers.add(einzahlungsschein.componentRenderer(einzahlungsscheinConfig, OnPage.LAST));
		}

		OnPageHandler onPageHandler = new OnPageHandler(
			getPdfElementGenerator(),
			componentRenderers,
			summaryTableGenerator(invoice.getSummary()));

		generate(outputStream, onPageHandler, pdfGenerator -> {
			Document document = pdfGenerator.getDocument();

			document.add(getPdfElementGenerator().createTitle(invoice.getTitle()));
			createIntroIfExist(document, invoice.getEinleitung());

			document.add(createRechnungspositionsTabelle(invoice));

			addTotalTable(invoice, document, configuration.getSpaceBefore());

			createKonditionenIfExist(document, invoice.getKonditionen());

			if (isNewPageRequired(pdfGenerator, invoice)) {
				document.newPage();
			}
		});
	}

	public void addTotalTable(
		@Nonnull Invoice invoice,
		@Nonnull Document document,
		float spaceBefore) throws DocumentException {

		Paragraph paragraph = getPdfElementGenerator().createParagraph(getConfiguration().getZahlungsKonditionen());

		PdfPTable totalTable = getPdfElementGenerator()
			.createSummaryTable(invoice.getTotal(), Alignment.RIGHT, paragraph, Element.ALIGN_RIGHT);

		totalTable.setSpacingBefore(spaceBefore);

		document.add(totalTable);
	}

	@Nonnull
	public Consumer<PdfContentByte> summaryTableGenerator(@Nonnull List<SummaryEntry> summary) {
		return directContent -> {
			try {
				Alignment summaryTablePosition = getConfiguration().getSummaryTablePosition();

				PdfPTable summaryTable = getPdfElementGenerator()
					.createSummaryTable(summary, summaryTablePosition, null, Element.ALIGN_LEFT);

				summaryTable.setTotalWidth(A4.getWidth() -
					getConfiguration().getLeftPageMarginInPoints() -
					getConfiguration().getRightPageMarginInPoints());

				addSummaryTable(directContent, summaryTable);
			} catch (DocumentException e) {
				throw new InvoiceGeneratorRuntimeException("Summary Table creation failed", e);
			}
		};
	}

	public void addSummaryTable(@Nonnull PdfContentByte directContent, @Nonnull PdfPTable summaryTable) {
		final float leftX = getConfiguration().getLeftPageMarginInPoints();
		final float pageHeight = getConfiguration().getPageSize().getHeight();
		final float topLeftYPosition = pageHeight - millimetersToPoints(getConfiguration().getTopAddressMarginInMM());

		summaryTable.writeSelectedRows(0, -1, leftX, topLeftYPosition, directContent);
	}

	public boolean isNewPageRequired(@Nonnull PdfGenerator pdfGenerator, @Nonnull Invoice invoice) {
		if (invoice.getEinzahlungsschein() == null) {
			return false;
		}

		EinzahlungsscheinConfiguration config = getConfiguration().getEinzahlungsscheinConfiguration();

		// this works for qr bill as well, there is only 1 mm difference in the specified height
		if (pdfGenerator.getVerticalPosition() < ESR_HEIGHT_WITH_MARGIN + config.getYOffset()) {
			return true;
		}

		return config.isEinzahlungsscheinNotOnPageOne()
			&& pdfGenerator.getDocument().getPageNumber() == 0;
	}

	public void createKonditionenIfExist(@Nonnull Document document, @Nullable List<String> konditionen)
		throws DocumentException {

		if (konditionen != null) {
			Paragraph paragraph = getPdfElementGenerator().createParagraph(konditionen);
			paragraph.setSpacingBefore(getConfiguration().getSpaceBefore());

			addNonnullElement(document, paragraph);
		}
	}

	public void createIntroIfExist(@Nonnull Document document, @Nullable List<String> einleitung)
		throws DocumentException {

		if (einleitung != null) {
			Paragraph paragraph = getPdfElementGenerator().createParagraph(einleitung);

			addNonnullElement(document, paragraph);
		}
	}

	public void addNonnullElement(@Nonnull Document document, @Nullable Element element) throws DocumentException {
		if (element != null) {
			document.add(element);
		}
	}

	@Nonnull
	public PdfPTable createRechnungspositionsTabelle(@Nonnull Invoice invoice) throws DocumentException {
		return getPdfElementGenerator()
			.createRechnungspositionsTabelle(invoice.getRechnungsPositionColumnTitle(), invoice.getPositionen());
	}
}
