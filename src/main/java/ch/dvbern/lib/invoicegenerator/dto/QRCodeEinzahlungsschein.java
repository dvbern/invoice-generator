/*
 * Copyright © 2020 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.lib.invoicegenerator.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
