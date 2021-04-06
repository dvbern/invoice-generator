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

package ch.dvbern.lib.invoicegenerator;

import java.io.File;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.Invoice;
import ch.dvbern.lib.invoicegenerator.dto.InvoiceGeneratorConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontBuilder;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPosition;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import org.junit.jupiter.api.Test;

import static ch.dvbern.lib.invoicegenerator.TestUtil.containsFonts;
import static ch.dvbern.lib.invoicegenerator.TestUtil.createFile;
import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.bold;
import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.boldItalic;
import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.color;
import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.italic;
import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.size;
import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.underline;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class FontConfigurationTest {

	@Test
	public void testFontFactory() throws Exception {
		FontFactory.register("/font/arial.ttf", FontConfiguration.FONT_FACE_OCRB);
		Font base = FontFactory.getFont(FontConfiguration.FONT_FACE_OCRB);

		FontConfiguration fontConfiguration = new FontConfiguration(base);

		InvoiceGeneratorConfiguration config = new InvoiceGeneratorConfiguration(Alignment.LEFT);
		config.setFonts(fontConfiguration);

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(config);
		Invoice invoice = Invoice.createDemoInvoice(TestDataUtil.ORANGER_EINZAHLUNGSSCHEIN);

		File file = createFile(invoiceGenerator, invoice, "target/fontsFontFactory.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			containsFonts(
				containsString("ArialMT"),
				containsString("ArialMT"))
		));
	}

	@Test
	public void testFontsAreConfigurable() throws Exception {

		FontBuilder fontBuilder = FontBuilder.of(new Font(Font.TIMES_ROMAN, 8));

		FontConfiguration fontConfiguration = new FontConfiguration(
			fontBuilder.reset().build(),
			fontBuilder.use(boldItalic()).build(),
			fontBuilder.use(underline(), size(12)).build(),
			fontBuilder.use(size(12), color(java.awt.Color.GREEN)).build(),
			fontBuilder.use(bold(), size(12)).build()
		);

		fontConfiguration.setFontOcrb(new Font(Font.COURIER, FontConfiguration.FONT_SIZE_OCRB));

		InvoiceGeneratorConfiguration config = new InvoiceGeneratorConfiguration(Alignment.LEFT);
		config.setFonts(fontConfiguration);

		config.getPositionStrategyMap().get(RechnungsPosition.class)
			.setFont(fontBuilder.use(italic()).build());

		InvoiceGenerator invoiceGenerator = new InvoiceGenerator(config);
		Invoice invoice = Invoice.createDemoInvoice(TestDataUtil.ORANGER_EINZAHLUNGSSCHEIN);

		File file = createFile(invoiceGenerator, invoice, "target/fontsFontSetters.pdf");

		assertThat(file, allOf(
			anExistingFile(),
			containsFonts(
				equalTo("Courier"), // the (fallback) OCRB font
				equalTo("Times-Roman"),
				equalTo("Times-BoldItalic"),
				equalTo("Times-Italic"),
				equalTo("Times-Bold"),
				endsWith("LiberationSans")) // always added by OpenPdf since version 1.3.18
		));
	}
}
