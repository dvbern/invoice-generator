/*
 * Copyright (C) 2023 DV Bern AG, Switzerland
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

package ch.dvbern.lib.invoicegenerator.dto.layout;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import com.lowagie.text.pdf.PdfPTable;

public class RechnungsTabelleWidths {

	private final float[] positionenColumnWidths;
	private final Map<Alignment, float[]> summaryColumnWidths;

	/**
	 * The sum of rechnungPositionen* widths should be 100.
	 * The sum of mainColumnWidth + summaryTableLabelWidth + summaryTableValueWidth should be 100.
	 * <p>
	 * {@link PdfPTable#setWidths(float[])}
	 */
	public RechnungsTabelleWidths(
		float rechnungsPositionLeistungWidth,
		float rechnungsPositionMengeWidth,
		float rechnungsPositionPreisWidth,
		float rechnungsPositionTotalWidth,
		float mainColumnWidth,
		float summaryTableLabelWidth,
		float summaryTableValueWidth
	) {
		this.positionenColumnWidths = new float[] {
			rechnungsPositionLeistungWidth,
			rechnungsPositionMengeWidth,
			rechnungsPositionPreisWidth,
			rechnungsPositionTotalWidth
		};

		this.summaryColumnWidths = new EnumMap<>(Alignment.class);
		this.summaryColumnWidths.put(Alignment.RIGHT, new float[] {
			mainColumnWidth,
			summaryTableLabelWidth,
			summaryTableValueWidth
		});
		this.summaryColumnWidths.put(Alignment.LEFT, new float[] {
			summaryTableLabelWidth,
			summaryTableValueWidth,
			mainColumnWidth,
		});
	}

	//CSOFF: MagicNumberCheck
	public RechnungsTabelleWidths() {
		this(
			60,
			10,
			15,
			15,
			60,
			21,
			19
		);
	}

	public float[] getPositionenColumnWidths() {
		return positionenColumnWidths;
	}

	public float[] getSummaryColumnWidths(@Nonnull Alignment alignment) {
		return this.summaryColumnWidths.get(alignment);
	}
}
