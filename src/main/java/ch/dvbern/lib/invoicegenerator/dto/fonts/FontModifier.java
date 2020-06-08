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

import java.awt.Color;

import javax.annotation.Nonnull;

import com.lowagie.text.Font;

@FunctionalInterface
public interface FontModifier {
	void accept(@Nonnull Font font);

	@Nonnull
	static FontModifier size(float size) {
		return font -> font.setSize(size);
	}

	@Nonnull
	static FontModifier color(@Nonnull Color color) {
		return font -> font.setColor(color);
	}

	@Nonnull
	static FontModifier style(int style) {
		return font -> font.setStyle(style);
	}

	@Nonnull
	static FontModifier bold() {
		return style(Font.BOLD);
	}

	@Nonnull
	static FontModifier italic() {
		return style(Font.ITALIC);
	}

	@Nonnull
	static FontModifier boldItalic() {
		return style(Font.BOLDITALIC);
	}

	@Nonnull
	static FontModifier underline() {
		return style(Font.UNDERLINE);
	}
}
