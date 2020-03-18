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
package ch.dvbern.lib.invoicegenerator.pdf;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nonnull;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Utilities;

import static com.lowagie.text.pdf.BaseFont.EMBEDDED;
import static com.lowagie.text.pdf.BaseFont.WINANSI;

@SuppressWarnings("PMD.ClassNamingConventions")
public final class PdfUtilities {

	public static final float ESR_HEIGHT = Utilities.millimetersToPoints(106);
	public static final float ESR_HEIGHT_WITH_MARGIN = ESR_HEIGHT + 12;
	public static final float DEFAULT_SPACE_BEFORE = Utilities.millimetersToPoints(5);
	public static final int QR_RECHNUNG_IMAGE_WIDTH_IN_MM = 220;
	public static final float QR_RECHNUNG_HEIGHT = Utilities.millimetersToPoints(220);
	public static final float QR_RECHNUNG_HEIGHT_WITH_MARGIN = QR_RECHNUNG_HEIGHT + 10;

	public static final String FONT_FACE_OCRB = "OCRB";
	public static final float FONT_SIZE_OCRB = 12;
	public static final String FONT_FACE_PROXIMA_NOVA = "Proxima Nova Light";
	public static final String FONT_FACE_PROXIMA_NOVA_BOLD = "Proxima Nova Semibold";
	public static final float DEFAULT_FONT_SIZE = 10;
	public static final float FONT_SIZE_H1 = 14;
	public static final float FONT_SIZE_H2 = 12;

	public static final float DEFAULT_MULTIPLIED_LEADING = 1.2f;
	public static final float DEFAULT_MULTIPLIED_TITLE_LEADING = 2.4f;
	public static final float DEFAULT_MULTIPLIED_H1_LEADING = 1.8f;
	public static final float DEFAULT_MULTIPLIED_H2_LEADING = 1.8f;

	public static final int PADDING_BOTTOM = 6;

	public static final String NEWLINE = "\n";

	// Muss vor den FontFactory.getFont aufrufen definiert werden
	static {
		FontFactory.register("/font/arial.ttf", FONT_FACE_OCRB);
		FontFactory.register("/font/arial.ttf", FONT_FACE_PROXIMA_NOVA);
		FontFactory.register("/font/arial.ttf", FONT_FACE_PROXIMA_NOVA_BOLD);
	}

	public static final Font OCRB_FONT = FontFactory.getFont(FONT_FACE_OCRB, WINANSI, EMBEDDED, FONT_SIZE_OCRB);
	public static final Font DEFAULT_FONT = FontFactory.getFont(FONT_FACE_PROXIMA_NOVA, WINANSI, EMBEDDED,
		DEFAULT_FONT_SIZE);
	public static final Font DEFAULT_FONT_BOLD = FontFactory.getFont(FONT_FACE_PROXIMA_NOVA_BOLD, WINANSI, EMBEDDED,
		DEFAULT_FONT_SIZE);
	public static final Font TITLE_FONT = FontFactory.getFont(FONT_FACE_PROXIMA_NOVA_BOLD, WINANSI, EMBEDDED,
		FONT_SIZE_H1);
	public static final Font H1_FONT = FontFactory.getFont(FONT_FACE_PROXIMA_NOVA_BOLD, WINANSI, EMBEDDED,
		FONT_SIZE_H2);
	public static final Font H2_FONT = DEFAULT_FONT_BOLD;

	private PdfUtilities() {
	}

	@Nonnull
	public static Font createFontWithSize(@Nonnull Font originatingFont, float size) {
		Font newFont = new Font(originatingFont);
		newFont.setSize(size);

		return newFont;
	}

	@Nonnull
	public static Font createFontWithColor(@Nonnull Font originatingFont, @Nonnull Color color) {
		Font newFont = new Font(originatingFont);
		newFont.setColor(color);

		return newFont;
	}

	@Nonnull
	public static String joinListToString(@Nonnull List<String> list) {
		return String.join(NEWLINE, list);
	}
}
