/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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
package ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.StringJoiner;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentRenderer;
import ch.dvbern.lib.invoicegenerator.dto.component.OrangerEinzahlungsscheinComponent;
import ch.dvbern.lib.invoicegenerator.dto.component.SimpleConfiguration;
import ch.dvbern.lib.invoicegenerator.errors.IllegalKontoException;
import org.jspecify.annotations.NonNull;

import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.KONTO_PARTS;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.MAX_LENGTH_OF_ORDNUNGSNUMMER;
import static java.util.Objects.requireNonNull;

/**
 * Aus den Daten des OrangerEinzahlungsscheins erstellt der InvoiceGenerator den ESR. Dies ist die Implementierung
 * für einen Einzahlungsschein eines Postkontos.
 * Siehe {@link OrangerEinzahlungsscheinBank} für Bank-Implementierung.
 *
 * @author Xaver Weibel
 * @see OrangerEinzahlungsscheinBank
 */
public class OrangerEinzahlungsschein extends Einzahlungsschein {

	public static final int CHECKSUMME_MODULO = 10;

	private static final String ESR_IN_CHF_BELEGARTCODE = "01";
	private static final int[] CHECKSUMME_FAKTOREN = { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 };

	public static int calcPruefziffer(@NonNull final String referenzNrOhnePruefziffer) {
		// Prüfzifferberechnung Modulo 10, rekursiv
		int uebertrag = referenzNrOhnePruefziffer.chars()
			.mapToObj(i -> (char) i)
			.mapToInt(Character::getNumericValue)
			.reduce(0, (currentUebertrag, currentDigit) ->
				CHECKSUMME_FAKTOREN[(currentUebertrag + currentDigit) % CHECKSUMME_MODULO]);

		return (CHECKSUMME_MODULO - uebertrag) % CHECKSUMME_MODULO;
	}

	@NonNull
	private final List<String> einzahlungFuer;
	@NonNull
	private final String kontoForKodierzeile;
	@NonNull
	private final List<String> einbezahltVon;

	/**
	 * Erstellt eine neue OrangerEinzahlungsschein-Instanz.
	 *
	 * @param einzahlungFuer Das Feld "Einzahlung für" auf dem ESR
	 * @param referenzNrMitPruefziffer Die Referenznummer mit Prüfziffer (letzte Ziffer).
	 * Die Prüfziffer wird berechnet.
	 * @param betrag Der Betrag
	 * @param konto Das Konto im Format VV-XXX-P (VV = ESR-Code, XXX = Ordnungsnummer, P = Prüfziffer).
	 * Hier muss die Prüfziffer übergeben werden. Das Format wird entsprechend validiert.
	 * @param einbezahltVon Das Feld "Einbezahlt von" auf dem ESR
	 * @throws IllegalKontoException Falls das Konto nicht dem Format VV-XXX-P (VV = ESR-Code, XXX = Ordnungsnummer,
	 *                               P = Prüfziffer) entspricht
	 */
	public OrangerEinzahlungsschein(
		@NonNull List<String> einzahlungFuer,
		@NonNull BigInteger referenzNrMitPruefziffer,
		@NonNull BigDecimal betrag,
		@NonNull String konto,
		@NonNull List<String> einbezahltVon) throws IllegalKontoException {
		super(EinzahlungType.ORANGE_EINZAHLUNGSCHEIN, referenzNrMitPruefziffer, betrag, konto);

		requireNonNull(einzahlungFuer);
		requireNonNull(einbezahltVon);

		this.kontoForKodierzeile = parseKontoForKodierzeile(konto);
		this.einzahlungFuer = einzahlungFuer;
		this.einbezahltVon = einbezahltVon;
	}

	protected OrangerEinzahlungsschein(
		@NonNull List<String> einzahlungFuer,
		@NonNull BigInteger referenzNrMitPruefziffer,
		@NonNull BigDecimal betrag,
		@NonNull String konto,
		@NonNull String kontoForKodierzeile,
		@NonNull List<String> einbezahltVon) {
		super(EinzahlungType.ORANGE_EINZAHLUNGSCHEIN, referenzNrMitPruefziffer, betrag, konto);

		this.einzahlungFuer = einzahlungFuer;
		this.einbezahltVon = einbezahltVon;
		this.kontoForKodierzeile = kontoForKodierzeile;
	}

	@NonNull
	public static String parseKontoForKodierzeile(@NonNull String konto) throws IllegalKontoException {
		final String[] parts = konto.split("-");
		if (parts.length != KONTO_PARTS) {
			throw new IllegalKontoException("The konto must be in the format VV-XXX-P (VV = ESR-Code, XXX = "
				+ "Ordnungsnummer, P = Prüfziffer)");
		}
		if (!parts[0].equals(ESR_IN_CHF_BELEGARTCODE)) {
			throw new IllegalKontoException("Only 01 is supported as ESR-Code");
		}
		if (parts[1].length() > MAX_LENGTH_OF_ORDNUNGSNUMMER) {
			throw new IllegalKontoException("The max length for the Ordnungsnummer is 6");
		}
		try {
			final int ordnungsnummer = Integer.parseInt(parts[1]);
			final String ordnungsmummerString = String.format("%06d", ordnungsnummer);
			final int pruefziffer = calcPruefziffer(parts[0] + ordnungsmummerString);
			if (pruefziffer != Integer.parseInt(parts[2])) {
				throw new IllegalKontoException("Invalid Prüfziffer");
			}
			return parts[0] + ordnungsmummerString + pruefziffer;
		} catch (NumberFormatException numberFormatException) {
			throw new IllegalKontoException(
				"The Ordnungsnummer and the Prüfziffer must be a number",
				numberFormatException);
		}
	}

	@NonNull
	@Override
	public ComponentRenderer<SimpleConfiguration, ? extends Einzahlungsschein> componentRenderer(
		@NonNull EinzahlungsscheinConfiguration configuration,
		@NonNull OnPage onPage) {
		return new OrangerEinzahlungsscheinComponent(configuration, this, onPage);
	}

	@NonNull
	public List<String> getEinzahlungFuer() {
		return einzahlungFuer;
	}

	public int getBetragInChf() {
		return getBetrag().intValue();
	}

	@NonNull
	public String getBetragInCHFAsText() {
		return String.format("%08d", this.getBetragInChf());
	}

	public int getBetragInRp() {
		return getBetrag().remainder(BigDecimal.ONE).movePointRight(2).intValue();
	}

	@NonNull
	public String getBetragInRpAsText() {
		return String.format("%02d", this.getBetragInRp());
	}

	@NonNull
	public String getReferenzNrAsTextFuerEmpfangsschein() {
		return getReferenzNr().toString();
	}

	@NonNull
	public String getReferenzNrForPruefzifferAsText() {
		return String.format("%027d", getReferenzNr());
	}

	@NonNull
	public List<String> getEinbezahltVon() {
		return einbezahltVon;
	}

	@NonNull
	public String getKodierzeile() {
		final String belegartCodeAndBetrag = ESR_IN_CHF_BELEGARTCODE + getBetragInCHFAsText() + getBetragInRpAsText();

		int pruefziffer = calcPruefziffer(belegartCodeAndBetrag);
		return belegartCodeAndBetrag + pruefziffer + '>' + getReferenzNrForPruefzifferAsText() + "+ " +
			kontoForKodierzeile + '>';
	}

	@Override
	@NonNull
	public String toString() {
		return new StringJoiner(", ", OrangerEinzahlungsschein.class.getSimpleName() + '[', "]")
			.add("einzahlungFuer=" + einzahlungFuer)
			.add("kontoForKodierzeile='" + kontoForKodierzeile + '\'')
			.add("einbezahltVon=" + einbezahltVon)
			.toString();
	}
}
