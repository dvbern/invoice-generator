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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentRenderer;
import ch.dvbern.lib.invoicegenerator.dto.component.SimpleConfiguration;
import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Einzahlungsschein {
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
	public abstract ComponentRenderer<SimpleConfiguration, ? extends Einzahlungsschein> componentRenderer(
		@Nonnull EinzahlungsscheinConfiguration configuration,
		@Nonnull OnPage onPage);

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
