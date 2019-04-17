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

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import com.google.common.base.MoreObjects;

/**
 * Eine abstrakte Text-Komponente, welches absolut positioniert werden kann.
 *
 * @author Xaver Weibel
 */
public abstract class TextComponent implements ComponentConfiguration {

	@Nonnull
	private OnPage onPage;
	private float leftInMm;
	private float topInMm;
	private float widthInMm;
	private float heightInMm;

	protected TextComponent(
		float leftInMm,
		float topInMm,
		float widthInMm,
		float heightInMm,
		@Nonnull OnPage onPage) {

		this.leftInMm = leftInMm;
		this.topInMm = topInMm;
		this.widthInMm = widthInMm;
		this.heightInMm = heightInMm;
		this.onPage = onPage;
	}

	@Nonnull
	@Override
	public OnPage getOnPage() {
		return onPage;
	}

	@Override
	public void setOnPage(@Nonnull OnPage onPage) {
		this.onPage = onPage;
	}

	@Override
	@Nonnull
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("leftInMm", leftInMm)
			.add("topInMm", topInMm)
			.add("widthInMm", widthInMm)
			.add("heightInMm", heightInMm)
			.add("onPage", onPage)
			.toString();
	}

	public float getLeftInMm() {
		return leftInMm;
	}

	public void setLeftInMm(float leftInMm) {
		this.leftInMm = leftInMm;
	}

	public float getTopInMm() {
		return topInMm;
	}

	public void setTopInMm(float topInMm) {
		this.topInMm = topInMm;
	}

	public float getWidthInMm() {
		return widthInMm;
	}

	public void setWidthInMm(float widthInMm) {
		this.widthInMm = widthInMm;
	}

	public float getHeightInMm() {
		return heightInMm;
	}

	public void setHeightInMm(float heightInMm) {
		this.heightInMm = heightInMm;
	}
}
