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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.InvoiceGeneratorConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.EinzahlungsscheinConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.OrangerEinzahlungsscheinBank;
import ch.dvbern.lib.invoicegenerator.errors.IllegalKontoException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import ch.dvbern.lib.invoicegenerator.pdf.PdfGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ch.dvbern.lib.invoicegenerator.dto.component.OrangerEinzahlungsscheinComponent.createEinzahlungsschein;

public class OrangerEinzahlungsscheinGeneratorTest {

	@SuppressWarnings("StringConcatenationMissingWhitespace")
	private static final List<String> LONG_TEXT = Collections.singletonList("sdfsdfsddfdgjdfklgjdflgdälgjdfklgjgljdfk"
		+ "lgjdöklgjsklfgjklgjklsdgjkldgjdlgjdlfgjdfgjdfgljdfgldfjgldfgdfgdgdgljkljkljljkljkljkljlj"
		+ "öljljljljljljvdfdlgjdölgjdölgjdföklgjdöfklgjdöfklgjdföklgjdfgljdfögljdfögkljdökljsdökljsdöklfjsdöklfjsd"
		+ "ölkfjsdöklfjsdöklfjsdöklfjsdöklfjsöfljsdöfljsdlsjdöfklsjdlfsjdföljsdölkfjsdöklfjsdölfjsdölfjsdöfklsdjf"
		+ "öklsdfjsdöklfjsdölfjsdlkfjsdölfjsdölfjsdöklfjsdlfjsdölfjsödlfjsdölffjdlfjsdölfjsdölfjsdlfjsdöklfjsdölfjsd"
		+ "öfklsdjöklfjsdöklfjsdöfkljlksdjflöksdjfölsksdjfsdölkfjsdöl");

	private final BigDecimal betrag = new BigDecimal("3949.75");
	private final List<String> einbezahltVon = Arrays.asList("Rutschmann Pia", "Marktgasse 28", "94900 Rorschach");
	private final List<String> einzahlungFuer = Arrays.asList(
		"Robert Schneider SA", "Grands magasins", "Case postale", "2501 Biel/Bienne");

	private final List<String> einbezahltVonLong = LONG_TEXT;
	private final List<String> einzahlungFuerLong = LONG_TEXT;

	private final List<String> einzahlungFuerBank = Arrays.asList("Schweizerische Bank", "6430 Schwyz");
	private final List<String> zugunstenVon = Arrays.asList("DV Bern AG", "Nussbaumstrasse 21", "3006 Bern");

	private final List<String> einzahlungFuerBankLong = LONG_TEXT;
	private final List<String> zugunstenVonLong = LONG_TEXT;
	private final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.LEFT);
	private final PdfElementGenerator pdfElementGenerator = new PdfElementGenerator(configuration);
	private final EinzahlungsscheinConfiguration defaultConfig = createConfig();

	@Test
	public void testTheCreationOfASampleEsr() throws IllegalKontoException, DocumentException, IOException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(
			einzahlungFuer,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"01-162-8",
			einbezahltVon);

		create("target/Einzahlungsschein.pdf", einzahlungsschein, defaultConfig);
	}

	@Test
	public void testTheCreationOfAEsrWithAnOffset() throws IllegalKontoException, DocumentException, IOException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(
			einzahlungFuer,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"01-162-8",
			einbezahltVon);

		EinzahlungsscheinConfiguration config = createConfig();
		config.setLeftOffsetInMm(2);
		config.setTopOffsetInMm(-5);

		create("target/EinzahlungsscheinWithAnOffset.pdf", einzahlungsschein, config);
	}

	@Test
	public void testTheCreationOfAEsrWithLoooongContent()
		throws IllegalKontoException, DocumentException, IOException {
		final OrangerEinzahlungsschein einzahlungsschein = new OrangerEinzahlungsschein(
			einzahlungFuerLong,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"01-162-8",
			einbezahltVonLong);

		create("target/EinzahlungsscheinXL.pdf", einzahlungsschein, defaultConfig);
	}

	@Test
	public void testTheCreationOfASampleBankEsr() throws IllegalKontoException, DocumentException, IOException {
		final OrangerEinzahlungsscheinBank einzahlungsschein = new OrangerEinzahlungsscheinBank(
			einzahlungFuerBank,
			zugunstenVon,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"01-162-8",
			einbezahltVon);

		create("target/EinzahlungsscheinBank.pdf", einzahlungsschein, defaultConfig);
	}

	@Test
	public void testTheCreationOfABankEsrWithLoooongContent()
		throws IllegalKontoException, DocumentException, IOException {
		final OrangerEinzahlungsscheinBank einzahlungsschein = new OrangerEinzahlungsscheinBank(
			einzahlungFuerBankLong,
			zugunstenVonLong,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"01-162-8",
			einbezahltVonLong);

		create("target/EinzahlungsscheinBankXL.pdf", einzahlungsschein, defaultConfig);
	}

	@Test
	public void testTheCreationOfABankEsrWithAnOffset() throws IllegalKontoException, DocumentException, IOException {
		final OrangerEinzahlungsscheinBank einzahlungsschein = new OrangerEinzahlungsscheinBank(
			einzahlungFuerBank,
			zugunstenVon,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"01-162-8",
			einbezahltVon);

		EinzahlungsscheinConfiguration config = createConfig();
		config.setTopOffsetInMm(5);

		create("target/EinzahlungsscheinBankWithOffset.pdf", einzahlungsschein, defaultConfig);
	}

	private void create(
		@Nonnull String path,
		@Nonnull OrangerEinzahlungsschein einzahlungsschein,
		@Nonnull EinzahlungsscheinConfiguration config) throws FileNotFoundException {

		PdfGenerator generator = new PdfGenerator(new FileOutputStream(path), configuration);

		PdfContentByte content = generator.getDirectContent();
		createEinzahlungsschein(content, pdfElementGenerator, config, einzahlungsschein);

		generator.close();
		Assertions.assertTrue(new File(path).isFile());
	}

	@Nonnull
	private EinzahlungsscheinConfiguration createConfig() {
		EinzahlungsscheinConfiguration config = new EinzahlungsscheinConfiguration();
		config.setAddEsrBackgroundImage(true);

		return config;
	}
}
