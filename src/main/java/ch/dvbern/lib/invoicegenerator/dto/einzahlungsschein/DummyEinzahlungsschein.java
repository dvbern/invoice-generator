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
import java.util.Arrays;

import javax.annotation.Nonnull;

public class DummyEinzahlungsschein extends OrangerEinzahlungsschein {

	private static final String DUMMY_ESR_PLACEHOLDER = "XXXXXXX";

	public DummyEinzahlungsschein() {
		super(
			Arrays.asList(DUMMY_ESR_PLACEHOLDER, DUMMY_ESR_PLACEHOLDER),
			BigInteger.ZERO,
			BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP),
			DUMMY_ESR_PLACEHOLDER,
			DUMMY_ESR_PLACEHOLDER,
			Arrays.asList(DUMMY_ESR_PLACEHOLDER, DUMMY_ESR_PLACEHOLDER)
		);
	}

	@Nonnull
	@Override
	public String getBetragInCHFAsText() {
		return "";
	}

	@Nonnull
	@Override
	public String getBetragInRpAsText() {
		return "";
	}

	@Nonnull
	@Override
	public String getReferenzNrAsText() {
		return DUMMY_ESR_PLACEHOLDER;
	}

	@Nonnull
	@Override
	public String getReferenzNrAsTextFuerEmpfangsschein() {
		return DUMMY_ESR_PLACEHOLDER;
	}

	@Nonnull
	@Override
	public String getReferenzNrForPruefzifferAsText() {
		return DUMMY_ESR_PLACEHOLDER;
	}

	@Nonnull
	@Override
	public String getKodierzeile() {
		return DUMMY_ESR_PLACEHOLDER;
	}
}
