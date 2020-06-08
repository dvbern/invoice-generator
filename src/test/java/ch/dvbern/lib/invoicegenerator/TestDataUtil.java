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
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.component.Logo;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.QRCodeEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.errors.IllegalKontoException;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Language;

import static ch.dvbern.lib.invoicegenerator.dto.PageConfiguration.LEFT_PAGE_DEFAULT_MARGIN_MM;

public final class TestDataUtil {

	private static final Pattern SPACE = Pattern.compile(" ");

	public static final Address DEBTOR =
		create("Pia-Maria Rutschmann-Schnyder", "Grosse Marktgasse", "28", "9400", "Rorschach");
	public static final Address CREDITOR =
		create("Robert Schneider AG", "Rue du Lac", "1268", "2501", "Biel");

	public static final String ESR_ACOUNT = "01-162-8";
	public static final BigInteger ESR_REFERENCE = new BigInteger(stripSpaces("12 00000 00000 23447 89432 16899"));

	public static final String QR_IBAN = "CH44 3199 9123 0008 8901 2";
	public static final String QR_REFERENCE_FORMATTED = "21 00000 00003 13947 14300 09017";
	public static final BigInteger QR_REFERENCE = new BigInteger(stripSpaces(QR_REFERENCE_FORMATTED));

	public static final BigDecimal AMOUNT = new BigDecimal("3949.75");

	public static final OrangerEinzahlungsschein ORANGER_EINZAHLUNGSSCHEIN = orangerEinzahlungsschein();
	public static final QRCodeEinzahlungsschein QR_CODE_EINZAHLUNGSSCHEIN = qrCodeEinzahlungsschein();

	private TestDataUtil() {
	}

	@Nonnull
	public static Logo defaultLogo() {
		byte[] image = TestUtil.readURL(InvoiceGeneratorTest.class.getResource("dvbern.png"));

		return new Logo(image, LEFT_PAGE_DEFAULT_MARGIN_MM, 10, 30);
	}

	@Nonnull
	public static OrangerEinzahlungsschein orangerEinzahlungsschein() {
		try {
			List<String> creditor = toLines(CREDITOR);
			List<String> debitor = toLines(DEBTOR);

			return new OrangerEinzahlungsschein(creditor, ESR_REFERENCE, AMOUNT, ESR_ACOUNT, debitor);
		} catch (IllegalKontoException ex) {
			throw new InvoiceGeneratorRuntimeException("Test data invalid", ex);
		}
	}

	@Nonnull
	public static QRCodeEinzahlungsschein qrCodeEinzahlungsschein() {
		return new QRCodeEinzahlungsschein(CREDITOR, QR_REFERENCE, AMOUNT, QR_IBAN, DEBTOR, null, Language.DE);
	}

	@Nonnull
	public static List<String> toLines(@Nonnull Address address) {
		return Arrays.asList(
			address.getName(),
			address.getStreet() + ' ' + address.getHouseNo(),
			address.getPostalCode() + ' ' + address.getTown()
		);
	}

	@Nonnull
	private static Address create(
		@Nonnull String name,
		@Nonnull String street,
		@Nonnull String houseNumber,
		@Nonnull String postcode,
		@Nonnull String town) {

		Address address = new Address();
		address.setName(name);
		address.setStreet(street);
		address.setHouseNo(houseNumber);
		address.setPostalCode(postcode);
		address.setTown(town);
		address.setCountryCode("CH");

		return address;
	}

	@Nonnull
	private static String stripSpaces(@Nonnull String input) {
		return SPACE.matcher(input).replaceAll("");
	}
}
