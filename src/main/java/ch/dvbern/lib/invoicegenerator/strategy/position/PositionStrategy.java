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
package ch.dvbern.lib.invoicegenerator.strategy.position;

import java.util.Optional;

import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class PositionStrategy {

	@Nullable
	private Font font = null;

	@Nullable
	private Float multipliedLeading = null;

	public abstract void addPositionToTable(
		@NonNull PageConfiguration configuration,
		@NonNull PdfPTable table,
		@NonNull Position position,
		boolean lastPosition);

	@NonNull
	protected abstract Font getFont(@NonNull FontConfiguration configuration);

	protected abstract float getMultipliedLeading(@NonNull PageConfiguration configuration);

	@NonNull
	public PdfPCell createHeaderCell(
		@NonNull PageConfiguration configuration,
		@NonNull String text,
		boolean underlined) {

		Phrase phrase = new Phrase(text, getFont(configuration.getFonts()));
		PdfPCell cell = new PdfPCell(phrase);
		cell.setLeading(0, getMultipliedLeading(configuration));
		if (underlined) {
			cell.setPaddingBottom(PdfUtilities.PADDING_BOTTOM);
			cell.setBorder(Rectangle.BOTTOM);
		} else {
			cell.setBorder(Rectangle.NO_BORDER);
		}
		cell.setColspan(PdfElementGenerator.POSITIONEN_COLUMNS);
		return cell;
	}

	@NonNull
	public Optional<Font> getFont() {
		return Optional.ofNullable(font);
	}

	public void setFont(@Nullable Font font) {
		this.font = font;
	}

	@NonNull
	public Optional<Float> getMultipliedLeading() {
		return Optional.ofNullable(multipliedLeading);
	}

	public void setMultipliedLeading(@Nullable Float multipliedLeading) {
		this.multipliedLeading = multipliedLeading;
	}
}
