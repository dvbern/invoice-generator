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

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.errors.IllegalKontoException;

import static java.util.Objects.requireNonNull;

/**
 * Aus den Daten des OrangerEinzahlungsscheinBank erstellt der InvoiceGenerator den ESR. Dies ist die Implementierung
 * für einen Einzahlungsschein eines Bankkontos. Bei der Bank muss neben dem "Einzahlung für" auch das
 * "Zugunsten von" definiert werden.
 *
 * @author Xaver Weibel
 * @see OrangerEinzahlungsschein
 */
public class OrangerEinzahlungsscheinBank extends OrangerEinzahlungsschein {

	@Nonnull
	private final List<String> zugunstenVon;

	/**
	 * @param einzahlungFuer Das Feld "Einzahlung für" auf dem ESR
	 * @param zugunstenVon Das Feld "Zugunsten von" auf dem ESR
	 * @param referenzNrOhnePruefziffer Die Referenznummer ohne Prüfziffer (letzte Ziffer).
	 * Die Prüfziffer wird berechnet.
	 * @param betrag Der Betrag
	 * @param konto Das Konto im Format VV-XXX-P (VV = ESR-Code, XXX = Ordnungsnummer, P = Prüfziffer).
	 * Hier muss die Prüfziffer übergeben werden. Das Format wird entsprechend validiert.
	 * @param einbezahltVon Das Feld "Einbezahlt von" auf dem ESR
	 * @throws IllegalKontoException Falls das Konto nicht dem Format VV-XXX-P (VV = ESR-Code, XXX = Ordnungsnummer,
	 *                               P = Prüfziffer) entspricht
	 */
	public OrangerEinzahlungsscheinBank(
		@Nonnull List<String> einzahlungFuer,
		@Nonnull List<String> zugunstenVon,
		@Nonnull BigInteger referenzNrOhnePruefziffer,
		@Nonnull BigDecimal betrag,
		@Nonnull String konto,
		@Nonnull List<String> einbezahltVon) throws IllegalKontoException {
		super(einzahlungFuer, referenzNrOhnePruefziffer, betrag, konto, einbezahltVon);

		requireNonNull(zugunstenVon);
		if (einzahlungFuer.size() > 2) {
			throw new IllegalKontoException("EinzahlungFuer cannot have more than 2 lines");
		}

		this.zugunstenVon = zugunstenVon;
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", OrangerEinzahlungsscheinBank.class.getSimpleName() + '[', "]")
			.add("zugunstenVon=" + zugunstenVon)
			.toString();
	}

	@Nonnull
	public List<String> getZugunstenVon() {
		return zugunstenVon;
	}
}
