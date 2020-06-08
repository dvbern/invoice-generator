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

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.position.H2Position;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

public class H2Strategy extends PositionStrategy {

	@Override
	public void addPositionToTable(
		@Nonnull PageConfiguration configuration,
		@Nonnull PdfPTable table,
		@Nonnull Position position,
		boolean lastPosition) {

		if (position instanceof H2Position) {
			final H2Position h2Position = (H2Position) position;
			table.addCell(createHeaderCell(configuration, h2Position.getBeschrieb(), lastPosition));
		}
	}

	@Nonnull
	@Override
	protected Font getFont(@Nonnull FontConfiguration configuration) {
		return getFont()
			.orElseGet(configuration::getFontH2);
	}

	@Override
	protected float getMultipliedLeading(@Nonnull PageConfiguration configuration) {
		return getMultipliedLeading()
			.orElseGet(configuration::getMultipliedLeadingH2);
	}
}
