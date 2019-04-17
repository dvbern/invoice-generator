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
package ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.OrangerEinzahlungsscheinBank;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;

import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_1_HEIGHT;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_1_WIDTH;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_1_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_1_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_2_HEIGHT;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_2_WIDTH;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_2_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINZAHLUNG_FUER_BANK_2_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_1_HEIGHT;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_1_WIDTH;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_1_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_1_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_2_HEIGHT;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_2_WIDTH;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_2_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.ZUGUNSTEN_VON_2_Y;

public class EinzahlungFuerBankStrategy extends EinzahlungFuerStrategy {

	private static final String ESR_IMG = "einzahlungsschein-a4-esr-bank.jpg";

	private final float xOffset;
	private final float yOffset;

	public EinzahlungFuerBankStrategy(float xOffset, float yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Nonnull
	@Override
	public String getEsrBackgroundImageName() {
		return EinzahlungFuerBankStrategy.ESR_IMG;
	}

	@Override
	public void writeEinzahlungFuer(
		@Nonnull PdfContentByte directContent,
		@Nonnull PdfElementGenerator pdfElementGenerator,
		@Nonnull OrangerEinzahlungsschein orangerEinzahlungsschein)
		throws DocumentException {

		if (orangerEinzahlungsschein instanceof OrangerEinzahlungsscheinBank) {
			OrangerEinzahlungsscheinBank orangerEinzahlungsscheinBank = (OrangerEinzahlungsscheinBank)
				orangerEinzahlungsschein;

			pdfElementGenerator.writeMultiLine(
				directContent,
				orangerEinzahlungsschein.getEinzahlungFuer(),
				EINZAHLUNG_FUER_BANK_1_X + xOffset,
				EINZAHLUNG_FUER_BANK_1_Y + yOffset,
				EINZAHLUNG_FUER_BANK_1_WIDTH,
				EINZAHLUNG_FUER_BANK_1_HEIGHT);

			pdfElementGenerator.writeMultiLine(
				directContent,
				orangerEinzahlungsschein.getEinzahlungFuer(),
				EINZAHLUNG_FUER_BANK_2_X + xOffset,
				EINZAHLUNG_FUER_BANK_2_Y + yOffset,
				EINZAHLUNG_FUER_BANK_2_WIDTH,
				EINZAHLUNG_FUER_BANK_2_HEIGHT);

			pdfElementGenerator.writeMultiLine(
				directContent,
				orangerEinzahlungsscheinBank.getZugunstenVon(),
				ZUGUNSTEN_VON_1_X + xOffset,
				ZUGUNSTEN_VON_1_Y + yOffset,
				ZUGUNSTEN_VON_1_WIDTH,
				ZUGUNSTEN_VON_1_HEIGHT);

			pdfElementGenerator.writeMultiLine(
				directContent,
				orangerEinzahlungsscheinBank.getZugunstenVon(),
				ZUGUNSTEN_VON_2_X + xOffset,
				ZUGUNSTEN_VON_2_Y + yOffset,
				ZUGUNSTEN_VON_2_WIDTH,
				ZUGUNSTEN_VON_2_HEIGHT);
		}
	}
}
