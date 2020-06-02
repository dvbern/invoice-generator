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

import java.util.List;

import javax.annotation.Nonnull;

import com.lowagie.text.Utilities;

@SuppressWarnings("PMD.ClassNamingConventions")
public final class PdfUtilities {

	public static final float ESR_HEIGHT = Utilities.millimetersToPoints(106);
	public static final float ESR_HEIGHT_WITH_MARGIN = ESR_HEIGHT + 12;
	public static final float DEFAULT_SPACE_BEFORE = Utilities.millimetersToPoints(5);

	public static final float DEFAULT_MULTIPLIED_LEADING = 1.2f;
	public static final float DEFAULT_MULTIPLIED_TITLE_LEADING = 2.4f;
	public static final float DEFAULT_MULTIPLIED_H1_LEADING = 1.8f;
	public static final float DEFAULT_MULTIPLIED_H2_LEADING = 1.8f;

	public static final int PADDING_BOTTOM = 6;

	public static final String NEWLINE = "\n";

	private PdfUtilities() {
	}

	@Nonnull
	public static String joinListToString(@Nonnull List<String> list) {
		return String.join(NEWLINE, list);
	}
}
