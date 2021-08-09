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

import java.util.Optional;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import com.lowagie.text.Font;

import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_MULTIPLIED_LEADING;

/**
 * Eine Text-Komponente, welche absolut positioniert werden kann.
 * Wird standardmässig auf jeder Seite ausgegeben.
 */
public class PhraseComponent extends TextComponent {

	@Nullable
	private final Font font;
	@Nonnull
	private final Alignment alignment;

	private final float multipliedLeading;

	/**
	 * Erstellt mit den Parametern eine neue Component Instanz.
	 *
	 * @param leftInMm Abstand der Komponente in mm zum linken Seitenrand
	 * @param topInMm Abstand der Komponente in mm zum oberen Seitenrand
	 * @param widthInMm Breite in mm
	 * @param heightInMm Höhe in mm
	 * @param onPage Component auf diesen Seiten rendern
	 * @param font Font in gewünschter Grösse
	 * @param alignment Text links oder rechts ausrichten
	 * @param multipliedLeading Abstand vor dem Paragraph in Point (Faktor der FontSize)
	 */
	public PhraseComponent(
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm,
		@Nonnull OnPage onPage,
		@Nullable Font font,
		@Nonnull Alignment alignment,
		float multipliedLeading) {

		super(leftInMm, topInMm, widthInMm, heightInMm, onPage);
		this.font = font;
		this.alignment = alignment;
		this.multipliedLeading = multipliedLeading;
	}

	/**
	 * Erstellt mit den Parametern eine neue Component Instanz.
	 *
	 * @param leftInMm Abstand der Komponente in mm zum linken Seitenrand
	 * @param topInMm Abstand der Komponente in mm zum oberen Seitenrand
	 * @param widthInMm Breite in mm
	 * @param heightInMm Höhe in mm
	 */
	public PhraseComponent(
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm) {

		this(
			leftInMm,
			topInMm,
			widthInMm,
			heightInMm,
			null,
			Alignment.LEFT,
			DEFAULT_MULTIPLIED_LEADING);
	}

	/**
	 * Erstellt mit den Parametern eine neue Component Instanz.
	 *
	 * @param leftInMm Abstand der Komponente in mm zum linken Seitenrand
	 * @param topInMm Abstand der Komponente in mm zum oberen Seitenrand
	 * @param widthInMm Breite in mm
	 * @param heightInMm Höhe in mm
	 * @param font Font in gewünschter Grösse
	 * @param alignment Text links oder rechts ausrichten
	 * @param multipliedLeading Abstand vor dem Paragraph in Point (Faktor der FontSize)
	 */
	public PhraseComponent(
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm,
		@Nullable Font font,
		@Nonnull Alignment alignment,
		float multipliedLeading) {

		this(leftInMm, topInMm, widthInMm, heightInMm, OnPage.ALL, font, alignment, multipliedLeading);
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", PhraseComponent.class.getSimpleName() + '[', "]")
			.add("alignment=" + alignment)
			.add("font=" + Optional.ofNullable(font).map(Font::getFamilyname).orElse(null))
			.add("multipliedLeading=" + multipliedLeading)
			.add(super.toString())
			.toString();
	}

	@Nonnull
	public Optional<Font> getFont() {
		return Optional.ofNullable(font);
	}

	@Nonnull
	public Alignment getAlignment() {
		return alignment;
	}

	public float getMultipliedLeading() {
		return multipliedLeading;
	}
}
