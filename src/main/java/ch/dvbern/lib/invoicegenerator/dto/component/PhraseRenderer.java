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

package ch.dvbern.lib.invoicegenerator.dto.component;

import java.util.List;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_MULTIPLIED_LEADING;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.joinListToString;
import static com.lowagie.text.Element.ALIGN_LEFT;
import static com.lowagie.text.Element.ALIGN_RIGHT;

public class PhraseRenderer extends TextComponentRenderer<PhraseComponent, List<String>> {

	public PhraseRenderer(
		@NonNull List<String> lines,
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm,
		@NonNull OnPage onPage,
		@Nullable Font font,
		@NonNull Alignment alignment,
		float leading) {

		super(new PhraseComponent(leftInMm, topInMm, widthInMm, heightInMm, onPage, font, alignment, leading), lines);
	}

	public PhraseRenderer(
		@NonNull List<String> lines,
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm) {

		this(
			lines,
			leftInMm,
			topInMm,
			widthInMm,
			heightInMm,
			null);
		setPayload(lines);
	}

	public PhraseRenderer(
		@NonNull List<String> lines,
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm,
		@Nullable Font font) {

		this(
			lines,
			leftInMm,
			topInMm,
			widthInMm,
			heightInMm,
			OnPage.ALL,
			font,
			Alignment.LEFT,
			DEFAULT_MULTIPLIED_LEADING);
		setPayload(lines);
	}

	public PhraseRenderer(@NonNull PhraseComponent componentConfiguration) {
		super(componentConfiguration);
	}

	@Override
	protected void render(
		@NonNull ColumnText columnText,
		@NonNull List<String> payload,
		@NonNull PageConfiguration pageConfiguration) {

		PhraseComponent componentConfiguration = getComponentConfiguration();

		switch (componentConfiguration.getAlignment()) {
		case LEFT:
			columnText.setAlignment(ALIGN_LEFT);
			break;
		case RIGHT:
			columnText.setAlignment(ALIGN_RIGHT);
			break;
		}

		columnText.setLeading(0, componentConfiguration.getMultipliedLeading());
		Font font = componentConfiguration.getFont()
			.orElseGet(() -> pageConfiguration.getFonts().getFont());

		columnText.setText(new Phrase(joinListToString(payload), font));
	}
}
