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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.lib.invoicegenerator.dto.component.AddressComponent;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentRenderer;
import ch.dvbern.lib.invoicegenerator.dto.component.Logo;
import ch.dvbern.lib.invoicegenerator.dto.component.PhraseRenderer;

public class BaseLayoutConfiguration extends PageConfiguration {

	/**
	 * A text which is added to PDF metadata - replacing the auto-generated OpenPDF entry
	 */
	@Nonnull
	private final String producer;
	@Nonnull
	private final AddressComponent empfaengerAdresse;
	@Nullable
	private Logo logo = null;
	@Nullable
	private PhraseRenderer header = null;
	@Nullable
	private PhraseRenderer footer = null;
	@Nonnull
	private final List<ComponentRenderer<? extends ComponentConfiguration, ?>> customComponents = new ArrayList<>();
	@Nullable
	private List<ComponentRenderer<? extends ComponentConfiguration, ?>> staticComponents = null;

	public BaseLayoutConfiguration(@Nonnull String producer, @Nonnull AddressComponent empfaengerAdresse) {
		this.producer = producer;
		this.empfaengerAdresse = empfaengerAdresse;
	}

	@Nullable
	public Logo getLogo() {
		return logo;
	}

	public void setLogo(@Nullable Logo logo) {
		this.logo = logo;
		// clear the cache
		this.staticComponents = null;
	}

	@Nonnull
	public List<ComponentRenderer<? extends ComponentConfiguration, ?>> getStaticComponents() {
		if (staticComponents == null) {
			// populate the cache
			List<ComponentRenderer<? extends ComponentConfiguration, ?>> components = Stream.of(header, footer, logo)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
			components.addAll(customComponents);

			this.staticComponents = components;
		}

		return staticComponents;
	}

	@Nonnull
	public List<ComponentRenderer<? extends ComponentConfiguration, ?>> getCustomComponents() {
		return customComponents;
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", BaseLayoutConfiguration.class.getSimpleName() + '[', "]")
			.add("empfaengerAdresse=" + empfaengerAdresse)
			.add("logo=" + logo)
			.add("header=" + header)
			.add("footer=" + footer)
			.toString();
	}

	@Nonnull
	public String getProducer() {
		return producer;
	}

	@Nullable
	public PhraseRenderer getHeader() {
		if (header == null) {
			return null;
		}

		return header;
	}

	public void setHeader(@Nullable PhraseRenderer header) {
		this.header = header;
		// clear the cache
		this.staticComponents = null;
	}

	@Nullable
	public PhraseRenderer getFooter() {
		return footer;
	}

	public void setFooter(@Nullable PhraseRenderer footer) {
		this.footer = footer;
		// clear the cache
		this.staticComponents = null;
	}

	@Nonnull
	public AddressComponent getEmpfaengerAdresse() {
		return empfaengerAdresse;
	}

	@Nullable
	public String getPp() {
		return empfaengerAdresse.getPp();
	}

	public void setPp(@Nullable String pp) {
		this.empfaengerAdresse.setPp(pp);
	}

	public float getLeftAddressMarginInMM() {
		return empfaengerAdresse.getLeftInMm();
	}

	public void setLeftAddressMarginInMM(float leftAddressMarginInMM) {
		this.empfaengerAdresse.setLeftInMm(leftAddressMarginInMM);
	}

	public float getTopAddressMarginInMM() {
		return empfaengerAdresse.getTopInMm();
	}

	public void setTopAddressMarginInMM(float topAddressMarginInMM) {
		this.empfaengerAdresse.setTopInMm(topAddressMarginInMM);
	}
}
