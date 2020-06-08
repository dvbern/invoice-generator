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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentRenderer;
import ch.dvbern.lib.invoicegenerator.dto.component.QRCodeComponent;
import ch.dvbern.lib.invoicegenerator.dto.component.SimpleConfiguration;
import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Language;

public class QRCodeEinzahlungsschein extends Einzahlungsschein {

	@Nonnull
	private final Address einzahlungFuer;

	@Nonnull
	private final Address einzahlungVon;

	@Nonnull
	private Language language = Language.DE;

	@Nullable
	private final String additionalText;

	/**
	 * Erstellt eine neue QRCodeEinzahlungsschein-Instanz.
	 *
	 * @param einzahlungFuer Das Feld "Einzahlung für" auf dem QR Code
	 * @param referenzNr Die Referenznummer mit Prüfziffer (letzte Ziffer).
	 * Die Prüfziffer wird berechnet.
	 * @param betrag Der Betrag
	 * @param konto Das Konto im Format QR-IBAN (ISO 13616). Das Zahlverfahren mit
	 * Referenz wird über eine spezielle Identifikation des Finanzinstituts (QR-IID) erkannt.
	 * Für die QR-IID sind exklusiv Werte im Bereich 30000 – 31999 reserviert.
	 * @param einbezahltVon Das Feld "Einbezahlt von" auf dem ESR
	 */
	public QRCodeEinzahlungsschein(
		@Nonnull Address einzahlungFuer,
		@Nonnull BigInteger referenzNr,
		@Nonnull BigDecimal betrag,
		@Nonnull String konto,
		@Nonnull Address einbezahltVon,
		@Nullable String additionalText,
		@Nullable Language language) {
		super(EinzahlungType.QR_CODE, referenzNr, betrag, konto);

		this.einzahlungFuer = einzahlungFuer;
		this.einzahlungVon = einbezahltVon;
		this.additionalText = additionalText;
		if (language != null) {
			this.language = language;
		}
	}

	@Nonnull
	@Override
	public ComponentRenderer<SimpleConfiguration, ? extends Einzahlungsschein> componentRenderer(
		@Nonnull EinzahlungsscheinConfiguration configuration,
		@Nonnull OnPage onPage) {
		return new QRCodeComponent(configuration, this, onPage);
	}

	@Nonnull
	public Language getLanguage() {
		return language;
	}

	@Nullable
	public String getAdditionalText() {
		return additionalText;
	}

	@Nonnull
	public Address getEinzahlungFuer() {
		return einzahlungFuer;
	}

	@Nonnull
	public Address getEinzahlungVon() {
		return einzahlungVon;
	}
}
