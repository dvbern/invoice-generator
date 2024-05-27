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

package ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein;

import java.util.StringJoiner;

import ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer.EinzahlungFuerBankStrategy;
import ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer.EinzahlungFuerPostStrategy;
import ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer.EinzahlungFuerStrategy;
import com.lowagie.text.Utilities;
import org.jspecify.annotations.NonNull;

public class EinzahlungsscheinConfiguration {

	private float xOffset;
	private float yOffset;
	private boolean addEsrBackgroundImage;
	private boolean einzahlungsscheinNotOnPageOne = false;

	public void setLeftOffsetInMm(float leftOffsetInMm) {
		this.xOffset = Utilities.millimetersToPoints(leftOffsetInMm);
	}

	public void setTopOffsetInMm(float topOffsetInMm) {
		this.yOffset = -Utilities.millimetersToPoints(topOffsetInMm);
	}

	@NonNull
	public EinzahlungFuerStrategy createEinzahlungFuerStrategy(@NonNull OrangerEinzahlungsschein einzahlungsschein) {
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
	@NonNull
	public String toString() {
		return new StringJoiner(", ", EinzahlungsscheinConfiguration.class.getSimpleName() + '[', "]")
			.add("xOffset=" + xOffset)
			.add("yOffset=" + yOffset)
			.add("addEsrBackgroundImage=" + addEsrBackgroundImage)
			.add("einzahlungsscheinNotOnPageOne=" + einzahlungsscheinNotOnPageOne)
			.toString();
	}
}
