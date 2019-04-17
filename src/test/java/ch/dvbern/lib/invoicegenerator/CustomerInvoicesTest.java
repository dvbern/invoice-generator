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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.Invoice;
import ch.dvbern.lib.invoicegenerator.dto.InvoiceGeneratorConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.component.Logo;
import ch.dvbern.lib.invoicegenerator.dto.component.PhraseRenderer;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPosition;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities;
import org.junit.Assert;
import org.junit.Test;

import static ch.dvbern.lib.invoicegenerator.InvoiceGeneratorTest.readURL;
import static ch.dvbern.lib.invoicegenerator.dto.component.AddressComponent.RECHTE_ADRESSE_LEFT_MARGIN_MM;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_MULTIPLIED_H2_LEADING;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.createFontWithSize;

// CHECKSTYLE:OFF
public class CustomerInvoicesTest {

	@Test
	public void dvbernInvoiceTest_1() throws InvoiceGeneratorException, IOException {
		final Logo logo = new Logo(readURL(CustomerInvoicesTest.class.getResource("dvbern.png")),
			11, 9, 42);
		final List<String> footerLines = Arrays.asList(
			"DV Bern AG • Nussbaumstrasse 21 • CH-3000 Bern 22",
			"031 378 24 24 • hallo@dvbern.ch • www.dvbern.ch");
		final PhraseRenderer footer = new PhraseRenderer(
			footerLines,
			42,
			280,
			80,
			10,
			OnPage.NOT_LAST,
			createFontWithSize(PdfUtilities.DEFAULT_FONT, 8),
			Alignment.LEFT,
			DEFAULT_MULTIPLIED_H2_LEADING);
		final List<String> zahlungskonto = Arrays.asList("Zahlungsverbindung:", "Berner Kantonalbank AG",
			"IBAN: CH44 0079 0016 2683 1167");
		final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.RIGHT);
		configuration.setLogo(logo);
		configuration.setFooter(footer);
		configuration.setMargins(33, 32, 100, 15);
		configuration.setLeftAddressMarginInMM(33);
		configuration.setTopAddressMarginInMM(56);
		configuration.setZahlungsKonditionen(zahlungskonto);
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = Invoice.createDemoInvoiceForBank(Arrays.asList("Berner Kantonalbank AG", "3001 Bern"),
			Arrays.asList("DV Bern", "Abteilung X", "Nussbaumstrasse 21", "CH-3000 22"), "01-123456-9");
		invoiceGenerator.generateInvoice(new FileOutputStream("target/dvbernInvoice1.pdf"), invoice);
		Assert.assertTrue(new File("target/dvbernInvoice1.pdf").isFile());
		replacePositionenWith30SimplePositionen(invoice);
		invoiceGenerator.generateInvoice(new FileOutputStream("target/dvbernInvoiceWith25Positionen1.pdf"), invoice);
		Assert.assertTrue(new File("target/dvbernInvoiceWith25Positionen1.pdf").isFile());
	}

	@Test
	public void dvbernInvoiceTest_2() throws InvoiceGeneratorException, IOException {
		final Logo logo = new Logo(readURL(CustomerInvoicesTest.class.getResource("dvbern.png")),
			PageConfiguration.LEFT_PAGE_DEFAULT_MARGIN_MM, 10, 38);
		final List<String> headerLines = Arrays.asList("DV Bern AG", "Nussbaumstrasse 21", "CH-3000 Bern 22",
			"Tel.: 031 378 24 24", "hallo@dvbern.ch", "www.dvbern.ch");
		final PhraseRenderer header = new PhraseRenderer(headerLines, RECHTE_ADRESSE_LEFT_MARGIN_MM, 18, 80, 40);
		final List<String> zahlungskonto = Arrays.asList("Zahlungsverbindung:", "Postfinance",
			"Konto-Nr.: 30-12345-0", "IBAN: CH79 0000 0000 0000 0000 0", "SWIFT-Code/BIC: POFICHBEXXX");
		final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.RIGHT);
		configuration.setLogo(logo);
		configuration.setHeader(header);
		configuration.setZahlungsKonditionen(zahlungskonto);
		configuration.setTopAddressMarginInMM(60);
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = Invoice.createDemoInvoice(Arrays.asList("DV Bern AG", "Nussbaumstrasse 21",
			"CH-3000 Bern 22"), "01-123456-9");
		invoiceGenerator.generateInvoice(new FileOutputStream("target/dvbernInvoice2.pdf"), invoice);
		Assert.assertTrue(new File("target/dvbernInvoice2.pdf").isFile());
		replacePositionenWith30SimplePositionen(invoice);
		invoiceGenerator.generateInvoice(new FileOutputStream("target/dvbernInvoiceWith25Positionen2.pdf"), invoice);
		Assert.assertTrue(new File("target/dvbernInvoiceWith25Positionen2.pdf").isFile());
	}

	@Test
	public void dvbernInvoiceTest_3() throws InvoiceGeneratorException, IOException {
		final Logo logo = new Logo(readURL(CustomerInvoicesTest.class.getResource("dvbern.png")),
			PageConfiguration.LEFT_PAGE_DEFAULT_MARGIN_MM, 10, 20);
		final List<String> headerLines = Arrays.asList("DV Bern AG", "Nussbaumstrasse 21", "CH-3000 Bern 22",
			"http://www.dvbern.ch");
		final PhraseRenderer header = new PhraseRenderer(headerLines, RECHTE_ADRESSE_LEFT_MARGIN_MM, 10, 80, 40);
		final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.RIGHT);
		configuration.setLogo(logo);
		configuration.setHeader(header);
		configuration.setPp("P.P. 3000 Bern 1 POST CH AG");
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = Invoice.createDemoInvoiceForBank(Arrays.asList("Berner Kantonalbank AG", "3001 Bern"),
			Arrays.asList("DV Bern AG", "Nussbaumstrasse 21", "3000 Bern 22"), "01-123456-9");
		invoiceGenerator.generateInvoice(new FileOutputStream("target/dvbernInvoice3.pdf"), invoice);
		Assert.assertTrue(new File("target/dvbernInvoice3.pdf").isFile());
		replacePositionenWith30SimplePositionen(invoice);
		invoiceGenerator.generateInvoice(new FileOutputStream("target/dvbernInvoiceWith25Positionen3.pdf"), invoice);
		Assert.assertTrue(new File("target/dvbernInvoiceWith25Positionen3.pdf").isFile());
	}

	private void replacePositionenWith30SimplePositionen(Invoice invoice) {
		invoice.getPositionen().clear();
		for (int i = 0; i < 30; i++) {
			invoice.getPositionen().add(new RechnungsPosition("Testposition", "1", "1.00", "1.00"));
		}
	}
}
