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

import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.position.H1Position;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import org.jspecify.annotations.NonNull;

public class H1Strategy extends PositionStrategy {

	@Override
	public void addPositionToTable(
		@NonNull PageConfiguration configuration,
		@NonNull PdfPTable table,
		@NonNull Position position,
		boolean lastPosition) {

		if (position instanceof H1Position) {
			final H1Position h1Position = (H1Position) position;
			table.addCell(createHeaderCell(configuration, h1Position.getBeschrieb(), lastPosition));
		}
	}

	@NonNull
	@Override
	protected Font getFont(@NonNull FontConfiguration configuration) {
		return getFont()
			.orElseGet(configuration::getFontH1);
	}

	@Override
	protected float getMultipliedLeading(@NonNull PageConfiguration configuration) {
		return getMultipliedLeading()
			.orElseGet(configuration::getMultipliedLeadingH1);
	}
}
