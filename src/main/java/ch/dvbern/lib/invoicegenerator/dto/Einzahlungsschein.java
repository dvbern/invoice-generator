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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.annotation.Nonnull;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Einzahlungsschein {
	@Nonnull
	private final BigInteger referenzNr;
	@Nonnull
	private final BigDecimal betrag;
	@Nonnull
	private final String konto;
	@Nonnull
	private final EinzahlungType dtype;

	protected Einzahlungsschein(
		@Nonnull EinzahlungType dtype,
		@Nonnull BigInteger referenzNr,
		@Nonnull BigDecimal betrag,
		@Nonnull String konto) {
		checkNotNull(betrag);
		checkNotNull(konto);
		checkNotNull(referenzNr);
		checkArgument(betrag.scale() == 2,
			"betrag.scale() was %s but expected 2", betrag.scale());
		this.dtype = dtype;

		this.referenzNr = referenzNr;
		this.betrag = betrag;
		this.konto = konto;
	}

	@Nonnull
	public String getReferenzNrAsText() {
		DecimalFormat decimalFormat = new DecimalFormat("#,#####");
		DecimalFormatSymbols formatSymbols = decimalFormat.getDecimalFormatSymbols();
		formatSymbols.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(formatSymbols);

		return decimalFormat.format(this.referenzNr);
	}

	@Nonnull
	public BigInteger getReferenzNr() {
		return referenzNr;
	}

	@Nonnull
	public BigDecimal getBetrag() {
		return betrag;
	}

	@Nonnull
	public String getKonto() {
		return konto;
	}

	@Nonnull
	public EinzahlungType getDtype() {
		return dtype;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("referenzNr", referenzNr)
			.add("betrag", betrag)
			.add("konto", konto)
			.toString();
	}
}
