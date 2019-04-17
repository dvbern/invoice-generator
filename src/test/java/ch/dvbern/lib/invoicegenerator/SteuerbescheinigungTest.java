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

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.Invoice;
import ch.dvbern.lib.invoicegenerator.dto.InvoiceGeneratorConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.SummaryEntry;
import ch.dvbern.lib.invoicegenerator.dto.component.PhraseRenderer;
import ch.dvbern.lib.invoicegenerator.dto.position.H1Position;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPosition;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPositionColumnTitle;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static ch.dvbern.lib.invoicegenerator.TestUtil.createFile;
import static ch.dvbern.lib.invoicegenerator.dto.component.AddressComponent.RECHTE_ADRESSE_LEFT_MARGIN_MM;

public class SteuerbescheinigungTest {

	private final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.RIGHT);

	private final List<String> absenderAdresse = Arrays.asList(
		"Kita Kinderland",
		"Nussbaumstrasse 1",
		"3006 Bern"
	);

	private final List<String> empfaengerAdresse = Arrays.asList(
		"Lord Byron",
		"Nussbaumstrasse 2",
		"3006 Bern"
	);

	private final PhraseRenderer header =
		new PhraseRenderer(absenderAdresse, RECHTE_ADRESSE_LEFT_MARGIN_MM, 11, 80, 30);

	private final List<SummaryEntry> summary = Collections.singletonList(
		new SummaryEntry("", "Datum: 13.12.2017", false, false)
	);

	private static final String TITLE = "Steuerbescheinigung für Lord Byron";

	private final List<String> einleitung = Collections.singletonList("Für den Zeitraum 01.01.2016 bis 31.12.2016 bei "
		+ "Kita Kinderland");

	private final RechnungsPositionColumnTitle columnTitle = new RechnungsPositionColumnTitle(
		"Dienstleistung",
		"",
		"",
		"Total"
	);

	private final List<Position> positionen = Arrays.asList(
		new H1Position("Lovelace Allegra"),
		new RechnungsPosition("Januar 2016", "", "", "146.40"),
		new RechnungsPosition("Februar 2016", "", "", "146.40"),
		new RechnungsPosition("März 2016", "", "", "146.40"),
		new RechnungsPosition("April 2016", "", "", "146.40"),
		new RechnungsPosition("Mai 2016", "", "", "146.40"),
		new RechnungsPosition("Juni 2016", "", "", "146.40"),
		new RechnungsPosition("Juli 2016", "", "", "146.40"),
		new RechnungsPosition("August 2016", "", "", "146.40"),
		new RechnungsPosition("September 2016", "", "", "146.40"),
		new RechnungsPosition("Oktober 2016", "", "", "146.40"),
		new RechnungsPosition("November 2016", "", "", "146.40"),
		new RechnungsPosition("Dezember 2016", "", "", "146.40")
	);

	private final List<SummaryEntry> total = Collections.singletonList(
		new SummaryEntry("Total", "8'468.00", true, true)
	);

	@Before
	public void init() {
		configuration.setHeader(header);
	}

	@Test
	public void testCreation() throws FileNotFoundException, InvoiceGeneratorException {
		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
		final Invoice invoice = new Invoice(columnTitle, TITLE, summary, einleitung, empfaengerAdresse,
			null, positionen, total, null);

		Assert.assertTrue(createFile(invoiceGenerator, invoice, "target/Steuerbescheinigung.pdf").isFile());
	}
}
