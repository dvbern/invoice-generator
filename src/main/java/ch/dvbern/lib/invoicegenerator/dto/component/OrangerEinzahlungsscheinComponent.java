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

import java.util.Objects;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.OrangerEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.OrangerEinzahlungsscheinConfiguration;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import ch.dvbern.lib.invoicegenerator.strategy.einzahlungfuer.EinzahlungFuerStrategy;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;

import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.BETRAG_CHF_1_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.BETRAG_CHF_2_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.BETRAG_RAPPEN_1_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.BETRAG_RAPPEN_2_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.BETRAG_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_1_HEIGHT;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_1_WIDTH;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_1_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_1_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_2_HEIGHT;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_2_WIDTH;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_2_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.EINBEZAHLT_VON_2_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.KODIERZEILE_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.KODIERZEILE_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.KONTO_X_1;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.KONTO_X_2;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.KONTO_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.REFERENZNUMMER_1_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.REFERENZNUMMER_1_Y;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.REFERENZNUMMER_EMPFANGSSCHEIN_FONT_SIZE;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.REFERENZNUMMER_EMPFANGSSCHEIN_X;
import static ch.dvbern.lib.invoicegenerator.OrangerEinzahlungsscheinConstants.REFERENZNUMMER_EMPFANGSSCHEIN_Y;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.lowagie.text.pdf.PdfContentByte.ALIGN_LEFT;
import static com.lowagie.text.pdf.PdfContentByte.ALIGN_RIGHT;

public class OrangerEinzahlungsscheinComponent
	extends ComponentRenderer<SimpleConfiguration, OrangerEinzahlungsschein> {

	@Nonnull
	private final OrangerEinzahlungsscheinConfiguration config;

	public OrangerEinzahlungsscheinComponent(
		@Nonnull OrangerEinzahlungsscheinConfiguration config,
		@Nonnull OrangerEinzahlungsschein orangerEinzahlungsschein,
		@Nonnull OnPage onPage) {

		super(new SimpleConfiguration(onPage));

		this.config = config;
		setPayload(orangerEinzahlungsschein);
	}

	public static void createEinzahlungsschein(
		@Nonnull PdfContentByte directContent,
		@Nonnull PdfElementGenerator pdfElementGenerator,
		@Nonnull OrangerEinzahlungsscheinConfiguration config,
		@Nonnull OrangerEinzahlungsschein orangerEinzahlungsschein) throws DocumentException {

		float xOffset = config.getXOffset();
		float yOffset = config.getYOffset();
		EinzahlungFuerStrategy einzahlungFuerStrategy = config.createEinzahlungFuerStrategy(orangerEinzahlungsschein);

		if (config.isAddEsrBackgroundImage()) {
			Image image = einzahlungFuerStrategy.getEsrBackgroundImage();
			image.setAbsolutePosition(xOffset, yOffset);
			pdfElementGenerator.addBackgroundImage(directContent, checkNotNull(image));
		}

		pdfElementGenerator.writeSingleLine(
			directContent,
			orangerEinzahlungsschein.getKonto(),
			KONTO_X_1 + xOffset,
			KONTO_Y + yOffset,
			ALIGN_LEFT);

		pdfElementGenerator.writeSingleLine(
			directContent,
			orangerEinzahlungsschein.getKonto(),
			KONTO_X_2 + xOffset,
			KONTO_Y + yOffset,
			ALIGN_LEFT);

		pdfElementGenerator.writeSingleOcrbLine(
			directContent,
			Integer.toString(orangerEinzahlungsschein.getBetragInChf()),
			BETRAG_CHF_1_X + xOffset,
			BETRAG_Y + yOffset,
			ALIGN_RIGHT);

		pdfElementGenerator.writeSingleOcrbLine(
			directContent,
			orangerEinzahlungsschein.getBetragInRpAsText(),
			BETRAG_RAPPEN_1_X + xOffset,
			BETRAG_Y + yOffset,
			ALIGN_LEFT);

		pdfElementGenerator.writeSingleOcrbLine(
			directContent,
			Integer.toString(orangerEinzahlungsschein.getBetragInChf()),
			BETRAG_CHF_2_X + xOffset,
			BETRAG_Y + yOffset,
			ALIGN_RIGHT);

		pdfElementGenerator.writeSingleOcrbLine(
			directContent,
			orangerEinzahlungsschein.getBetragInRpAsText(),
			BETRAG_RAPPEN_2_X + xOffset,
			BETRAG_Y + yOffset,
			ALIGN_LEFT);

		pdfElementGenerator.writeSingleLine(
			directContent,
			orangerEinzahlungsschein.getReferenzNrAsText(),
			REFERENZNUMMER_1_X + xOffset,
			REFERENZNUMMER_1_Y + yOffset,
			ALIGN_RIGHT);

		pdfElementGenerator.writeSingleLine(
			directContent,
			orangerEinzahlungsschein.getReferenzNrAsTextFuerEmpfangsschein(),
			REFERENZNUMMER_EMPFANGSSCHEIN_X + xOffset,
			REFERENZNUMMER_EMPFANGSSCHEIN_Y + yOffset,
			ALIGN_LEFT,
			REFERENZNUMMER_EMPFANGSSCHEIN_FONT_SIZE);

		pdfElementGenerator.writeMultiLine(
			directContent,
			orangerEinzahlungsschein.getEinbezahltVon(),
			EINBEZAHLT_VON_1_X + xOffset,
			EINBEZAHLT_VON_1_Y + yOffset,
			EINBEZAHLT_VON_1_WIDTH,
			EINBEZAHLT_VON_1_HEIGHT);

		pdfElementGenerator.writeMultiLine(
			directContent,
			orangerEinzahlungsschein.getEinbezahltVon(),
			EINBEZAHLT_VON_2_X + xOffset,
			EINBEZAHLT_VON_2_Y + yOffset,
			EINBEZAHLT_VON_2_WIDTH,
			EINBEZAHLT_VON_2_HEIGHT);

		einzahlungFuerStrategy
			.writeEinzahlungFuer(directContent, pdfElementGenerator, orangerEinzahlungsschein);

		pdfElementGenerator.writeSingleOcrbLine(
			directContent,
			orangerEinzahlungsschein.getKodierzeile(),
			KODIERZEILE_X + xOffset,
			KODIERZEILE_Y + yOffset,
			ALIGN_RIGHT);
	}

	@Override
	public void render(
		@Nonnull PdfContentByte directContent,
		@Nonnull PdfElementGenerator pdfElementGenerator)
		throws DocumentException {

		Objects.requireNonNull(getPayload());
		createEinzahlungsschein(directContent, pdfElementGenerator, config, getPayload());
	}
}
