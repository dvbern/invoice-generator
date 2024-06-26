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

import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.ColumnText;
import org.jspecify.annotations.NonNull;

import static ch.dvbern.lib.invoicegenerator.dto.component.AddressComponent.PP_PADDING_BOTTOM;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.joinListToString;

public class AddressRenderer extends TextComponentRenderer<AddressComponent, List<String>> {

	public AddressRenderer(@NonNull AddressComponent componentConfiguration, @NonNull List<String> payload) {
		super(componentConfiguration, payload);
	}

	@Override
	protected void render(
		@NonNull ColumnText columnText,
		@NonNull List<String> payload,
		@NonNull PageConfiguration pageConfiguration) {

		AddressComponent componentConfiguration = getComponentConfiguration();

		String pp = componentConfiguration.getPp();
		if (pp != null) {
			Font underlined = new Font(pageConfiguration.getFonts().getFontBold());
			underlined.setStyle(Font.UNDERLINE);
			Paragraph ppParagraph = new Paragraph(pp, underlined);
			ppParagraph.setSpacingAfter(PP_PADDING_BOTTOM);
			columnText.addElement(ppParagraph);
		}

		Paragraph address = new Paragraph(joinListToString(payload), pageConfiguration.getFonts().getFont());
		address.setMultipliedLeading(pageConfiguration.getMultipliedLeadingAddress());
		columnText.addElement(address);
	}
}
