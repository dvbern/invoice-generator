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

import java.util.StringJoiner;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import com.lowagie.text.Utilities;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static ch.dvbern.lib.invoicegenerator.dto.Alignment.LEFT;
import static ch.dvbern.lib.invoicegenerator.dto.PageConfiguration.LEFT_PAGE_DEFAULT_MARGIN_MM;

public class AddressComponent extends TextComponent {

	public static final float RECHTE_ADRESSE_LEFT_MARGIN_MM = 140;
	public static final float TOP_ADRESSE_DEFAULT_MARGIN_MM = 50;

	public static final float ADRESSE_HEIGHT = Utilities.millimetersToPoints(40);
	public static final float ADRESSE_WIDTH = Utilities.millimetersToPoints(65);
	public static final float PP_PADDING_BOTTOM = Utilities.millimetersToPoints(3);

	/**
	 * PP-Frankierungs Text
	 *
	 * @see
	 * <a href="https://www.post.ch/de/geschaeftlich/themen-a-z/sendungen-frankieren/briefe-frankieren-inland/pp-frankierung">Post Referenz</a>
	 */
	@Nullable
	private String pp;

	/**
	 * @param summaryTablePosition Falls Alignment.LEFT, wird die Adresse rechts angezeigt.
	 * Falls Alignment.RIGHT, wird die Adresse links angezeigt.
	 */
	public AddressComponent(@NonNull Alignment summaryTablePosition) {
		this(
			null,
			summaryTablePosition == LEFT ? RECHTE_ADRESSE_LEFT_MARGIN_MM : LEFT_PAGE_DEFAULT_MARGIN_MM,
			TOP_ADRESSE_DEFAULT_MARGIN_MM,
			ADRESSE_WIDTH,
			ADRESSE_HEIGHT,
			OnPage.ALL);
	}

	public AddressComponent(
		@Nullable String pp,
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm,
		@NonNull OnPage onPage) {

		super(leftInMm, topInMm, widthInMm, heightInMm, onPage);
		this.pp = pp;
	}

	@Override
	@NonNull
	public String toString() {
		return new StringJoiner(", ", AddressComponent.class.getSimpleName() + '[', "]")
			.add("pp='" + pp + '\'')
			.add(super.toString())
			.toString();
	}

	@Nullable
	public String getPp() {
		return pp;
	}

	public void setPp(@Nullable String pp) {
		this.pp = pp;
	}
}
