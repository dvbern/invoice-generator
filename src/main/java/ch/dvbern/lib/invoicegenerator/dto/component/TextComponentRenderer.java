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

import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import org.jspecify.annotations.NonNull;

import static com.lowagie.text.Utilities.millimetersToPoints;

public abstract class TextComponentRenderer<T extends TextComponent, Payload> extends ComponentRenderer<T, Payload> {

	protected TextComponentRenderer(@NonNull T componentConfiguration) {
		super(componentConfiguration);
	}

	protected TextComponentRenderer(@NonNull T componentConfiguration, @NonNull Payload payload) {
		super(componentConfiguration, payload);
	}

	protected abstract void render(
		@NonNull ColumnText columnText,
		@NonNull Payload payload,
		@NonNull PageConfiguration pageConfiguration);

	@Override
	public void render(
		@NonNull PdfContentByte directContent,
		@NonNull PdfElementGenerator pdfElementGenerator) throws DocumentException {

		Payload payload = getPayload();
		if (payload == null) {
			return;
		}

		PageConfiguration pageConfiguration = pdfElementGenerator.getConfiguration();

		T componentConfiguration = getComponentConfiguration();

		final float height = millimetersToPoints(componentConfiguration.getHeightInMm());
		final float width = millimetersToPoints(componentConfiguration.getWidthInMm());
		final float loverLeftX = millimetersToPoints(componentConfiguration.getLeftInMm());
		float pageHeight = pageConfiguration.getPageSize().getHeight();
		final float loverLeftY = pageHeight - millimetersToPoints(componentConfiguration.getTopInMm()) - height;

		final ColumnText columnText = new ColumnText(directContent);
		columnText.setSimpleColumn(loverLeftX, loverLeftY, loverLeftX + width, loverLeftY + height);

		render(columnText, payload, pageConfiguration);
		columnText.go();
	}
}
