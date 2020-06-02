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
package ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer;

import java.io.IOException;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;

public abstract class EinzahlungFuerStrategy {

	@Nonnull
	private final Image esrImage;

	protected EinzahlungFuerStrategy() {
		try {
			esrImage = Image.getInstance(EinzahlungFuerStrategy.class.getResource(getEsrBackgroundImageName()));
			esrImage.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			esrImage.setAbsolutePosition(0, 0);
		} catch (DocumentException | IOException exception) {
			throw new InvoiceGeneratorRuntimeException("Could not initialize image", exception);
		}
	}

	@Nonnull
	public Image getEsrBackgroundImage() {
		return this.esrImage;
	}

	abstract String getEsrBackgroundImageName();

	public abstract void writeEinzahlungFuer(
		@Nonnull PdfContentByte directContent,
		@Nonnull PdfElementGenerator pdfElementGenerator,
		@Nonnull OrangerEinzahlungsschein orangerEinzahlungsschein)
		throws DocumentException;
}
