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
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.errors.IllegalKontoException;
import com.google.common.base.MoreObjects;

import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.KONTO_PARTS;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.MAX_LENGTH_OF_ORDNUNGSNUMMER;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Aus den Daten des OrangerEinzahlungsscheins erstellt der InvoiceGenerator den ESR. Dies ist die Implementierung
 * für einen Einzahlungsschein eines Postkontos.
 * Siehe {@link OrangerEinzahlungsscheinBank} für Bank-Implementierung.
 *
 * @author Xaver Weibel
 * @see OrangerEinzahlungsscheinBank
 */
public class OrangerEinzahlungsschein extends Einzahlungsschein{

	public static final int CHECKSUMME_MODULO = 10;

	private static final String ESR_IN_CHF_BELEGARTCODE = "01";
	private static final int[] CHECKSUMME_FAKTOREN = { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 };

	public static int calcPruefziffer(@Nonnull final String referenzNrOhnePruefziffer) {
		// Prüfzifferberechnung Modulo 10, rekursiv
		int uebertrag = referenzNrOhnePruefziffer.chars()
			.mapToObj(i -> (char) i)
			.mapToInt(Character::getNumericValue)
			.reduce(0, (currentUebertrag, currentDigit) ->
				CHECKSUMME_FAKTOREN[(currentUebertrag + currentDigit) % CHECKSUMME_MODULO]);

		return (CHECKSUMME_MODULO - uebertrag) % CHECKSUMME_MODULO;
	}

	@Nonnull
	private final List<String> einzahlungFuer;
	@Nonnull
	private final String kontoForKodierzeile;
	@Nonnull
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
		@Nonnull List<String> einzahlungFuer,
		@Nonnull BigInteger referenzNrMitPruefziffer,
		@Nonnull BigDecimal betrag,
		@Nonnull String konto,
		@Nonnull List<String> einbezahltVon) throws IllegalKontoException {
		super(EinzahlungType.ORANGE_EINZAHLUNGSCHEIN, referenzNrMitPruefziffer, betrag, konto);

		checkNotNull(einzahlungFuer);
		checkNotNull(einbezahltVon);
		checkArgument(betrag.scale() == 2,
			"betrag.scale() was %s but expected 2", betrag.scale());

		this.kontoForKodierzeile = parseKontoForKodierzeile(konto);
		this.einzahlungFuer = einzahlungFuer;
		this.einbezahltVon = einbezahltVon;
	}

	protected OrangerEinzahlungsschein(
		@Nonnull List<String> einzahlungFuer,
		@Nonnull BigInteger referenzNrMitPruefziffer,
		@Nonnull BigDecimal betrag,
		@Nonnull String konto,
		@Nonnull String kontoForKodierzeile,
		@Nonnull List<String> einbezahltVon) {
		super(EinzahlungType.ORANGE_EINZAHLUNGSCHEIN, referenzNrMitPruefziffer, betrag, konto);

		this.einzahlungFuer = einzahlungFuer;
		this.einbezahltVon = einbezahltVon;
		this.kontoForKodierzeile = kontoForKodierzeile;
	}

	@Nonnull
	public static String parseKontoForKodierzeile(@Nonnull String konto) throws IllegalKontoException {
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
			if (pruefziffer != Integer.valueOf(parts[2])) {
				throw new IllegalKontoException("Invalid Prüfziffer");
			}
			return parts[0] + ordnungsmummerString + pruefziffer;
		} catch (NumberFormatException numberFormatException) {
			throw new IllegalKontoException(
				"The Ordnungsnummer and the Prüfziffer must be a number",
				numberFormatException);
		}
	}

	@Nonnull
	public List<String> getEinzahlungFuer() {
		return einzahlungFuer;
	}

	public int getBetragInChf() {
		return getBetrag().intValue();
	}

	@Nonnull
	public String getBetragInCHFpAsText() {
		return String.format("%08d", this.getBetragInChf());
	}

	public int getBetragInRp() {
		return getBetrag().remainder(BigDecimal.ONE).movePointRight(2).intValue();
	}

	@Nonnull
	public String getBetragInRpAsText() {
		return String.format("%02d", this.getBetragInRp());
	}

	@Nonnull
	public String getReferenzNrAsTextFuerEmpfangsschein() {
		return getReferenzNr().toString();
	}

	@Nonnull
	public String getReferenzNrForPruefzifferAsText() {
		return String.format("%027d", getReferenzNr());
	}

	@Nonnull
	public List<String> getEinbezahltVon() {
		return einbezahltVon;
	}

	@Nonnull
	public String getKodierzeile() {
		final String belegartCodeAndBetrag = ESR_IN_CHF_BELEGARTCODE + getBetragInCHFpAsText() + getBetragInRpAsText();

		int pruefziffer = calcPruefziffer(belegartCodeAndBetrag);
		return belegartCodeAndBetrag + pruefziffer + '>' + getReferenzNrForPruefzifferAsText() + "+ " +
			kontoForKodierzeile + '>';
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("einzahlungFuer", einzahlungFuer)
			.add("kontoForKodierzeile", kontoForKodierzeile)
			.add("einbezahltVon", einbezahltVon)
			.toString();
	}
}
