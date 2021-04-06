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
package ch.dvbern.lib.invoicegenerator.dto.component;

import java.io.IOException;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;

import static com.lowagie.text.Utilities.millimetersToPoints;
import static java.util.Objects.requireNonNull;

/**
 * Ein Logo, welches auf einer Rechnung absolut positioniert werden kann.
 *
 * @author Xaver Weibel
 */
public class Logo extends ComponentRenderer<SimpleConfiguration, Image> {

	private static final int PERCENT_MULTIPLICATOR = 100;

	private final float absoluteX;
	private final float yOffset;

	/**
	 * Erstellt ein neues Logo
	 *
	 * @param image Die Bilddatei
	 * @param leftInMm Abstand in mm zum linken Seitenrand
	 * @param topInMm Abstand in mm zum oberen Seitenrand
	 * @param widthInMm Die breite auf welche das Bild skaliert wird. Die Höhe wird im selben Verhältnis skaliert.
	 */
	public Logo(@Nonnull byte[] image, float leftInMm, float topInMm, float widthInMm)
		throws InvoiceGeneratorRuntimeException {
		this(image, leftInMm, topInMm, widthInMm, OnPage.ALL);
	}

	/**
	 * Erstellt ein neues Logo
	 *
	 * @param imgData Die Bilddatei
	 * @param leftInMm Abstand in mm zum linken Seitenrand
	 * @param topInMm Abstand in mm zum oberen Seitenrand
	 * @param widthInMm Die breite auf welche das Bild skaliert wird. Die Höhe wird im selben Verhältnis skaliert.
	 * @param onPage Konfiguriert, auf welcher Seite das Bild ausgegeben werden soll.
	 */
	public Logo(@Nonnull byte[] imgData, float leftInMm, float topInMm, float widthInMm, @Nonnull OnPage onPage)
		throws InvoiceGeneratorRuntimeException {
		super(new SimpleConfiguration(onPage));
		try {
			Image image = Image.getInstance(imgData);
			float percent = PERCENT_MULTIPLICATOR * millimetersToPoints(widthInMm) / image.getWidth();
			image.scalePercent(percent);
			absoluteX = millimetersToPoints(leftInMm);
			yOffset = millimetersToPoints(topInMm) + image.getScaledHeight();

			setPayload(image);
		} catch (DocumentException | IOException exception) {
			throw new InvoiceGeneratorRuntimeException("Could not initialize imgData", exception);
		}
	}

	@Override
	public void render(@Nonnull PdfContentByte directContent, @Nonnull PdfElementGenerator pdfElementGenerator)
		throws DocumentException {

		float absoluteY = PageSize.A4.getHeight() - yOffset;
		requireNonNull(getPayload()).setAbsolutePosition(absoluteX, absoluteY);

		directContent.addImage(getPayload());
	}
}
