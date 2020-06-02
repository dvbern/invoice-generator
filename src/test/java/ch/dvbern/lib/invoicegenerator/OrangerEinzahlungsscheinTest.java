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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import ch.dvbern.lib.invoicegenerator.dto.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.errors.IllegalKontoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class OrangerEinzahlungsscheinTest {

	public static final int EXPECTED_VALUE_IN_RP = 75;
	public static final int EXPECTED_VALUE_INC_CHF = 3949;
	private final List<String> einbezahltVon = Arrays.asList("Rutschmann Pia", "Marktgasse 28", "94900 Rorschach");
	private final List<String> einzahlungFuer = Arrays.asList(
		"Robert Schneider SA", "Grands magasins", "Case postale", "2501 Biel/Bienne");
	private BigDecimal betrag = new BigDecimal("3949.75");

	@BeforeEach
	public void init() {
		betrag = betrag.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	@Test
	public void getBetragInRpShouldReturn75IfTheBetragIs3949Dot75() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("930454000006040744040244035"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals(EXPECTED_VALUE_IN_RP, einzahlungsschein.getBetragInRp());
	}

	@Test
	public void getBetragInRpAsTextShouldReturn75IfTheBetragIs3949Dot75() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("930454000006040744040244035"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals("75", einzahlungsschein.getBetragInRpAsText());
	}

	@Test
	public void getBetragInChfShouldReturn3949IfTheBetragIs3949Dot75() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("930454000006040744040244035"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals(EXPECTED_VALUE_INC_CHF, einzahlungsschein.getBetragInChf());
	}

	@Test
	public void getReferenzNrAsTextShoutReturnGroupsOfFife() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("120000000000234478943216899"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals("12 00000 00000 23447 89432 16899", einzahlungsschein.getReferenzNrAsText());
	}

	@Test
	public void getReferenzNrAsTextShoutReturnGroupsOfFifeWithZeroPadding() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("1236"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals("1236", einzahlungsschein.getReferenzNrAsText());
	}

	@Test
	public void getReferenzNrAsTextFuerEmpfangsscheinShoutReturnAHugeNumber() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("120000000000234478943216899"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals(
			"120000000000234478943216899",
			einzahlungsschein.getReferenzNrAsTextFuerEmpfangsschein());
	}

	@Test
	public void getReferenzNrForPruefzifferShouldAlwysReturnANumberWith27Digits() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("1236"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals("000000000000000000000001236", einzahlungsschein.getReferenzNrForPruefzifferAsText());
	}

	@Test
	public void getBetragInChfAsTextShouldReturn00003949IfTheBetragIs3949() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("93045400000604076404024403"), betrag, "01-200027-2", einbezahltVon);
		Assertions.assertEquals("00003949", einzahlungsschein.getBetragInCHFpAsText());
	}

	@Test
	public void createAnEinzahlungsscheinWithAnInvalidKontoFormatShouldNotBeAllowed() {
		Assertions.assertThrows(IllegalKontoException.class, () -> new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("120000000000234478943216899"), betrag, "162-8", einbezahltVon));
	}

	@Test
	public void createAnEinzahlungsscheinWithAnInvalidEsrCodeInTheKontoShouldNotBeAllowed() {
		Assertions.assertThrows(IllegalKontoException.class, () -> new OrangerEinzahlungsschein(einzahlungFuer,
			new BigInteger("120000000000234478943216899"), betrag, "02-162-8", einbezahltVon));
	}

	@Test
	public void createAnEinzahlungsscheinWithATooLongOrdnungsnummerShouldNotNeAllowed() {
		Assertions.assertThrows(
			IllegalKontoException.class,
			() -> new OrangerEinzahlungsschein(einzahlungFuer, new BigInteger("120000000000234478943216899"), betrag,
				"02-164445646462-8", einbezahltVon));
	}

	@Test
	public void createAnEinzahlungsscheinWithANonNumericOrdnungsnummerShouldNotBeAllowed() {
		Assertions.assertThrows(
			IllegalKontoException.class,
			() -> new OrangerEinzahlungsschein(einzahlungFuer, new BigInteger("120000000000234478943216899"), betrag,
				"02-1f222-8", einbezahltVon));
	}

	@Test
	public void testGetKodierzeile() throws IllegalKontoException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(
			einzahlungFuer, new BigInteger("120000000000234478943216899"), betrag, "01-162-8", einbezahltVon);

		String expected = "0100003949753>120000000000234478943216899+ 010001628>";
		Assertions.assertEquals(expected, einzahlungsschein.getKodierzeile());
	}
}
