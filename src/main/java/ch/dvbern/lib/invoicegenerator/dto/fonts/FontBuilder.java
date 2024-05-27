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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.lowagie.text.Font;
import org.jspecify.annotations.NonNull;

public final class FontBuilder {

	@NonNull
	private final Font baseFont;
	@NonNull
	private final List<FontModifier> modifiers = new ArrayList<>();

	private FontBuilder(@NonNull Font baseFont) {
		this.baseFont = baseFont;
	}

	public static FontBuilder of(@NonNull Font baseFont) {
		return new FontBuilder(baseFont);
	}

	/**
	 * Adds a modifier. Keep in mind that not all combinations are supported by the underlying library. E.g. there
	 * is no FontStyle for BOLD and UNDERLINE.
	 *
	 * @param modifier the font modified
	 * @return this builder
	 */
	@NonNull
	@CanIgnoreReturnValue
	public FontBuilder with(@NonNull FontModifier modifier) {
		this.modifiers.add(modifier);
		return this;
	}

	/*
	 * Convenience method to only apply the provied modifiers
	 */
	@SuppressWarnings("ParameterHidesMemberVariable")
	@NonNull
	@CanIgnoreReturnValue
	public FontBuilder use(@NonNull FontModifier... modifiers) {
		reset();
		this.modifiers.addAll(Arrays.asList(modifiers));
		return this;
	}

	@CanIgnoreReturnValue
	public FontBuilder reset() {
		modifiers.clear();
		return this;
	}

	@NonNull
	public Font build() {
		Font font = new Font(baseFont);
		modifiers.forEach(m -> m.accept(font));

		return font;
	}
}
