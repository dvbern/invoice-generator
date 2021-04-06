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
package ch.dvbern.lib.invoicegenerator.dto;

import java.util.StringJoiner;

import javax.annotation.Nonnull;

/**
 * Eine Invoice hat eine Summary-Liste sowie eine Total-Liste. Beide Listen beinhalten SummaryEntries.
 *
 * @author Xaver Weibel
 */

public class SummaryEntry {

	@Nonnull
	private final String label;
	@Nonnull
	private final String value;
	private final boolean bold;
	private final boolean underlined;

	/**
	 * Erstellt einen neuen Summary Entry.
	 *
	 * @param label Der Wert in der ersten Spalte, normalerweise das Label
	 * @param value Der Wert in der zweiten Spalte, normalerweise der entsprechende Wert
	 * @param bold true, falls die Zeile fett dargestellt werden soll, ansonsten false
	 * @param underlined true, falls die Zeile unterstrichen dargestellt werden soll, ansonsten false.
	 */
	public SummaryEntry(@Nonnull String label, @Nonnull String value, boolean bold, boolean underlined) {
		this.label = label;
		this.value = value;
		this.bold = bold;
		this.underlined = underlined;
	}

	public SummaryEntry(@Nonnull String label, @Nonnull String value) {
		this(label, value, false, false);
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", SummaryEntry.class.getSimpleName() + '[', "]")
			.add("label='" + label + '\'')
			.add("value='" + value + '\'')
			.add("bold=" + bold)
			.add("underlined=" + underlined)
			.toString();
	}

	@Nonnull
	public String getLabel() {
		return label;
	}

	@Nonnull
	public String getValue() {
		return value;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isUnderlined() {
		return underlined;
	}
}
