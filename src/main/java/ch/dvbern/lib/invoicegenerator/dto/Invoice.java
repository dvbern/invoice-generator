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
package ch.dvbern.lib.invoicegenerator.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.Einzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.OrangerEinzahlungsscheinBank;
import ch.dvbern.lib.invoicegenerator.dto.position.H1Position;
import ch.dvbern.lib.invoicegenerator.dto.position.H2Position;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPosition;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPositionColumnTitle;
import ch.dvbern.lib.invoicegenerator.errors.IllegalKontoException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Der InvoiceGenerator erstellt aus den Daten eines Invoice-Objekts eine Rechnung.
 *
 * @author Xaver Weibel
 */
public class Invoice {

	@NonNull
	private final RechnungsPositionColumnTitle rechnungsPositionColumnTitle;
	@NonNull
	private final String title;
	@NonNull
	private final List<SummaryEntry> summary;
	@Nullable
	private final List<String> einleitung;
	@NonNull
	private final List<String> adresse;
	@Nullable
	private final Einzahlungsschein einzahlungsschein;
	@NonNull
	private final List<Position> positionen;
	@NonNull
	private final List<SummaryEntry> total;
	@Nullable
	private final List<String> konditionen;

	/**
	 * Erstellt eine neue Invoice.
	 *
	 * @param rechnungsPositionColumnTitle Die Spaltentitel der Positionen
	 * @param title Der Titel der Rechnung
	 * @param summary Eine Liste von SummaryEntrys als Zusammenfassung. Diese Zusammenfassung wird auf jeder Rechnng
	 * dargestellt
	 * @param einleitung Nach dem Titel kann eine einleitung angezeigt werden
	 * @param adresse Die Empfänger-Adresse
	 * @param einzahlungsschein Die Daten des Einzahlungsscheins
	 * @param positionen Sämtliche Positionen der Rechnung
	 * @param total Eine Liste von SummaryEntrys als Total
	 */
	public Invoice(
		@NonNull RechnungsPositionColumnTitle rechnungsPositionColumnTitle,
		@NonNull String title,
		@NonNull List<SummaryEntry> summary,
		@Nullable List<String> einleitung,
		@NonNull List<String> adresse,
		@Nullable Einzahlungsschein einzahlungsschein,
		@NonNull List<Position> positionen,
		@NonNull List<SummaryEntry> total) {

		this(rechnungsPositionColumnTitle, title, summary, einleitung, adresse, einzahlungsschein, positionen,
			total, null);
	}

	/**
	 * Erstellt eine neue Invoice.
	 *
	 * @param rechnungsPositionColumnTitle Die Spaltentitel der Positionen
	 * @param title Der Titel der Rechnung
	 * @param summary Eine Liste von SummaryEntrys als Zusammenfassung. Diese Zusammenfassung wird auf jeder Rechnng
	 * dargestellt
	 * @param einleitung Nach dem Titel kann eine einleitung angezeigt werden
	 * @param adresse Die Empfänger-Adresse
	 * @param einzahlungsschein Die Daten des Einzahlungsscheins
	 * @param positionen Sämtliche Positionen der Rechnung
	 * @param total Eine Liste von SummaryEntrys als Total
	 * @param konditionen Nach dem Total können Konditionen angezeigt werden
	 */
	public Invoice(
		@NonNull RechnungsPositionColumnTitle rechnungsPositionColumnTitle,
		@NonNull String title,
		@NonNull List<SummaryEntry> summary,
		@Nullable List<String> einleitung,
		@NonNull List<String> adresse,
		@Nullable Einzahlungsschein einzahlungsschein,
		@NonNull List<Position> positionen,
		@NonNull List<SummaryEntry> total,
		@Nullable List<String> konditionen) {
		requireNonNull(rechnungsPositionColumnTitle);
		requireNonNull(title);
		requireNonNull(summary);
		requireNonNull(adresse);
		requireNonNull(positionen);
		requireNonNull(total);

		this.rechnungsPositionColumnTitle = rechnungsPositionColumnTitle;
		this.title = title;
		this.summary = summary;
		this.adresse = adresse;
		this.einzahlungsschein = einzahlungsschein;
		this.einleitung = einleitung;
		this.positionen = positionen;
		this.total = total;
		this.konditionen = konditionen;
	}

	/**
	 * Erstellt eine neue Invoice mit Demo-Positionen und einem Einzahlungsschein für die Bank
	 *
	 * @param einzahlungFuer Einzahlung für welche Bank
	 * @param zugunstenVon Einzahlung zugunsten von wem
	 * @param konto account information
	 * @return an Invoice DTO - can be used with InvoiceGenerator to generate a PDF file
	 * @throws IllegalKontoException when invalid account number given
	 */
	public static Invoice createDemoInvoiceForBank(
		@NonNull List<String> einzahlungFuer,
		@NonNull List<String> zugunstenVon,
		@NonNull String konto) throws IllegalKontoException {
		requireNonNull(einzahlungFuer);
		requireNonNull(zugunstenVon);
		requireNonNull(konto);

		List<String> adresse = Arrays.asList("Sandra Muster", "Thomas Muster", "Musterstrasse 21", "3000 Bern");
		OrangerEinzahlungsscheinBank orangerEinzahlungsscheinBank = new OrangerEinzahlungsscheinBank(einzahlungFuer,
			zugunstenVon, new BigInteger("83144100000000000000006015"), new BigDecimal("224.00"), konto, adresse);

		return createDemoInvoice(orangerEinzahlungsscheinBank);
	}

	/**
	 * Erstellt eine neue Invoice mit Demo-Positionen und einem Einzahlungsschein für die Post
	 *
	 * @param einzahlungFuer Einzahlung für
	 * @param konto Das Konto
	 * @return an Invoice DTO - can be used with InvoiceGenerator to generate a PDF file
	 * @throws IllegalKontoException when invalid account number given
	 */
	@NonNull
	public static Invoice createDemoInvoice(@NonNull List<String> einzahlungFuer, @NonNull String konto)
		throws IllegalKontoException {
		requireNonNull(einzahlungFuer);
		requireNonNull(konto);

		List<String> adresse = Arrays.asList("Sandra Muster", "Thomas Muster", "Musterstrasse 21", "3000 Bern");
		Einzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("83144100000000000000006015"), new BigDecimal("224.00"), konto, adresse);

		return createDemoInvoice(einzahlungsschein);
	}

	@NonNull
	public static Invoice createDemoInvoice(@NonNull Einzahlungsschein einzahlungsschein) {
		requireNonNull(einzahlungsschein);

		RechnungsPositionColumnTitle rechnungsPositionColumnTitle = new RechnungsPositionColumnTitle(
			"Dienstleistung", "Menge", "Preis", "Total");

		List<String> adresse = Arrays.asList("Sandra Muster", "Thomas Muster", "Musterstrasse 21", "3000 Bern");

		List<Position> positionen = new ArrayList<>();
		positionen.add(new H1Position("Sarah Muster"));
		positionen.add(new H2Position("40% Private Betreuung: 01.04.2017 - 30.04.2017"));
		positionen.add(new RechnungsPosition("Betreuungsgebühr", "1", "1040.00", "1'040.00"));
		positionen.add(new RechnungsPosition("Verpflegung", "8", "9.00", "72.00"));
		positionen.add(new H1Position("David Muster"));
		positionen.add(new H2Position("40% Private Betreuung: 01.04.2017 - 30.04.2017"));
		positionen.add(new RechnungsPosition("Betreuungsgebühr", "1", "1040.00", "1'040.00"));
		positionen.add(new RechnungsPosition("Verpflegung", "8", "9.00", "72.00"));

		List<SummaryEntry> summary = new ArrayList<>();
		summary.add(new SummaryEntry("Rechnungs-Nr.", "6015"));
		summary.add(new SummaryEntry("Kunden-Nr.", "1231"));
		summary.add(new SummaryEntry("Datum", "21.04.2017"));
		summary.add(new SummaryEntry("Fällig am", "21.05.2017"));
		summary.add(new SummaryEntry("Total", "CHF 2'224.00", true, false));
		summary.add(new SummaryEntry("Ausstehend", "CHF 224.00", true, true));

		List<SummaryEntry> total = new ArrayList<>();
		total.add(new SummaryEntry("Subtotal", "CHF 2'224.00", false, true));
		total.add(new SummaryEntry("Rundungsdifferenz", "CHF 0.01", false, false));
		total.add(new SummaryEntry("Total", "CHF 2'224.00", true, true));
		total.add(new SummaryEntry("Zahlung 30.03.18", "CHF 2'000.00"));
		total.add(new SummaryEntry("Ausstehend", "CHF 224.00", true, true));

		List<String> einleitung = Arrays.asList("Kita Muster", "April 2017", "Referenznr. 83 14410 00000 00000 "
			+ "00000 60154");

		List<String> konditionen = Collections.singletonList("Zahlbar innerhalb 30 Tagen.");

		return new Invoice(rechnungsPositionColumnTitle, "Rechnung", summary, einleitung, adresse,
			einzahlungsschein, positionen, total, konditionen);
	}

	@NonNull
	public RechnungsPositionColumnTitle getRechnungsPositionColumnTitle() {
		return rechnungsPositionColumnTitle;
	}

	@NonNull
	public List<String> getAdresse() {
		return adresse;
	}

	@Nullable
	public List<String> getEinleitung() {
		return einleitung;
	}

	@Nullable
	public Einzahlungsschein getEinzahlungsschein() {
		return einzahlungsschein;
	}

	@NonNull
	public String getTitle() {
		return title;
	}

	@NonNull
	public List<Position> getPositionen() {
		return positionen;
	}

	@NonNull
	public List<SummaryEntry> getTotal() {
		return total;
	}

	@NonNull
	public List<SummaryEntry> getSummary() {
		return summary;
	}

	@Nullable
	public List<String> getKonditionen() {
		return konditionen;
	}
}
