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

package ch.dvbern.lib.invoicegenerator.dto.fonts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.bold;
import static ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier.size;
import static com.lowagie.text.pdf.BaseFont.EMBEDDED;
import static com.lowagie.text.pdf.BaseFont.WINANSI;

public class FontConfiguration {

	private static final float DEFAULT_FONT_SIZE = 10;
	private static final float FONT_SIZE_TITLE = 14;
	private static final float FONT_SIZE_H1 = 12;
	private static final float FONT_SIZE_H2 = 10;

	public static final String FONT_FACE_OCRB = "OCRB";
	public static final float FONT_SIZE_OCRB = 12;

	@Nullable
	private Font fontOcrb;
	@Nonnull
	private Font font;
	@Nonnull
	private Font fontBold;
	@Nonnull
	private Font fontTitle;
	@Nonnull
	private Font fontH1;
	@Nonnull
	private Font fontH2;

	public FontConfiguration(@Nonnull Font base) {
		FontBuilder builder = FontBuilder.of(base);
		this.font = builder.build();
		this.fontBold = builder.with(bold()).build();
		this.fontTitle = builder.with(size(FONT_SIZE_TITLE)).build();
		this.fontH1 = builder.with(size(FONT_SIZE_H1)).build();
		this.fontH2 = builder.with(size(FONT_SIZE_H2)).build();
	}

	public FontConfiguration(int family) {
		this.font = new Font(family, DEFAULT_FONT_SIZE);
		this.fontBold = new Font(family, DEFAULT_FONT_SIZE, Font.BOLD);
		this.fontTitle = new Font(family, FONT_SIZE_TITLE, Font.BOLD);
		this.fontH1 = new Font(family, FONT_SIZE_H1, Font.BOLD);
		this.fontH2 = new Font(family, FONT_SIZE_H2, Font.BOLD);
	}

	public FontConfiguration(
		@Nonnull Font font,
		@Nonnull Font fontBold,
		@Nonnull Font fontTitle,
		@Nonnull Font fontH1,
		@Nonnull Font fontH2) {
		this.font = font;
		this.fontBold = fontBold;
		this.fontTitle = fontTitle;
		this.fontH1 = fontH1;
		this.fontH2 = fontH2;
	}

	@Nonnull
	public Font getFontOcrb() {
		if (fontOcrb == null) {
			fontOcrb = FontFactory.getFont(FONT_FACE_OCRB, WINANSI, EMBEDDED, FONT_SIZE_OCRB);
		}

		return fontOcrb;
	}

	public void setFontOcrb(@Nullable Font fontOcrb) {
		this.fontOcrb = fontOcrb;
	}

	@Nonnull
	public Font getFont() {
		return font;
	}

	public void setFont(@Nonnull Font font) {
		this.font = font;
	}

	@Nonnull
	public Font getFontBold() {
		return fontBold;
	}

	public void setFontBold(@Nonnull Font fontBold) {
		this.fontBold = fontBold;
	}

	@Nonnull
	public Font getFontTitle() {
		return fontTitle;
	}

	public void setFontTitle(@Nonnull Font fontTitle) {
		this.fontTitle = fontTitle;
	}

	@Nonnull
	public Font getFontH1() {
		return fontH1;
	}

	public void setFontH1(@Nonnull Font fontH1) {
		this.fontH1 = fontH1;
	}

	@Nonnull
	public Font getFontH2() {
		return fontH2;
	}

	public void setFontH2(@Nonnull Font fontH2) {
		this.fontH2 = fontH2;
	}
}
