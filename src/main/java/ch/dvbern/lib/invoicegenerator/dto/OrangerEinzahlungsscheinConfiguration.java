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

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer.EinzahlungFuerBankStrategy;
import ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer.EinzahlungFuerPostStrategy;
import ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer.EinzahlungFuerStrategy;
import com.google.common.base.MoreObjects;
import com.lowagie.text.Utilities;

public class OrangerEinzahlungsscheinConfiguration {

	private float xOffset;
	private float yOffset;
	private boolean addEsrBackgroundImage;
	private boolean einzahlungsscheinNotOnPageOne = false;

	public void setEsrLeftOffsetInMm(float esrLeftOffsetInMm) {
		this.xOffset = Utilities.millimetersToPoints(esrLeftOffsetInMm);
	}

	public void setEsrTopOffsetInMm(float esrTopOffsetInMm) {
		this.yOffset = -Utilities.millimetersToPoints(esrTopOffsetInMm);
	}

	@Nonnull
	public EinzahlungFuerStrategy createEinzahlungFuerStrategy(@Nonnull OrangerEinzahlungsschein einzahlungsschein) {
		return (einzahlungsschein instanceof OrangerEinzahlungsscheinBank) ?
			new EinzahlungFuerBankStrategy(xOffset, yOffset) :
			new EinzahlungFuerPostStrategy(xOffset, yOffset);
	}

	public float getXOffset() {
		return xOffset;
	}

	public float getYOffset() {
		return yOffset;
	}

	public boolean isAddEsrBackgroundImage() {
		return addEsrBackgroundImage;
	}

	public void setAddEsrBackgroundImage(boolean addEsrBackgroundImage) {
		this.addEsrBackgroundImage = addEsrBackgroundImage;
	}

	public boolean isEinzahlungsscheinNotOnPageOne() {
		return einzahlungsscheinNotOnPageOne;
	}

	public void setEinzahlungsscheinNotOnPageOne(boolean einzahlungsscheinNotOnPageOne) {
		this.einzahlungsscheinNotOnPageOne = einzahlungsscheinNotOnPageOne;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("xOffset", xOffset)
			.add("yOffset", yOffset)
			.add("addEsrBackgroundImage", addEsrBackgroundImage)
			.add("einzahlungsscheinNotOnPageOne", einzahlungsscheinNotOnPageOne)
			.toString();
	}
}
