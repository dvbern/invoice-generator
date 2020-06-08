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

import java.awt.Color;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPosition;
import ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class RechnungsPositionStrategy extends PositionStrategy {

	@Override
	public void addPositionToTable(
		@Nonnull PageConfiguration configuration,
		@Nonnull PdfPTable table,
		@Nonnull Position position,
		boolean lastPosition) {

		if (position instanceof RechnungsPosition) {
			final RechnungsPosition rechnungsPosition = (RechnungsPosition) position;
			table.addCell(createCell(configuration, rechnungsPosition.getLeistung(), false, lastPosition));
			table.addCell(createCell(configuration, rechnungsPosition.getMenge(), true, lastPosition));
			table.addCell(createCell(configuration, rechnungsPosition.getPreis(), true, lastPosition));
			table.addCell(createCell(configuration, rechnungsPosition.getTotal(), true, lastPosition));
		}
	}

	@Nonnull
	private PdfPCell createCell(
		@Nonnull PageConfiguration configuration,
		@Nonnull final String string,
		boolean rigthAllign,
		boolean lastPosition) {

		PdfPCell cell = new PdfPCell(new Phrase(string, getFont(configuration.getFonts())));
		cell.setLeading(0, getMultipliedLeading(configuration));

		if (lastPosition) {
			cell.setPaddingBottom(PdfUtilities.PADDING_BOTTOM);
			cell.setBorder(Rectangle.BOTTOM);
			cell.setBorderColor(Color.GRAY);
		} else {
			cell.setBorder(Rectangle.NO_BORDER);
		}

		if (rigthAllign) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		}

		return cell;
	}

	@Nonnull
	@Override
	protected Font getFont(@Nonnull FontConfiguration configuration) {
		return getFont()
			.orElseGet(configuration::getFont);
	}

	@Override
	protected float getMultipliedLeading(@Nonnull PageConfiguration configuration) {
		return getMultipliedLeading()
			.orElseGet(configuration::getMultipliedLeadingDefault);
	}
}
