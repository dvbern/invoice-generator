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

import java.util.List;
import java.util.StringJoiner;

import ch.dvbern.lib.invoicegenerator.dto.component.AddressComponent;
import ch.dvbern.lib.invoicegenerator.dto.component.OrangerEinzahlungsscheinComponent;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.DummyEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.EinzahlungsscheinConfiguration;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.ESR_HEIGHT_WITH_MARGIN;

/**
 * Definiert das Layout der Rechnungen (Header, Footer, Zahlungskonto, ..) sowie statische Elemente, die bei
 * sämtlichen Rechnungen gleich bleiben.
 *
 * @author Xaver Weibel
 */
public class InvoiceGeneratorConfiguration extends BaseLayoutConfiguration {

	@NonNull
	private final Alignment summaryTablePosition;
	@Nullable
	private List<String> zahlungsKonditionen = null;

	@NonNull
	private final EinzahlungsscheinConfiguration einzahlungsscheinConfiguration = new EinzahlungsscheinConfiguration();

	/**
	 * Erstellt eine neue InvoiceGeneratorConfiguration mit default Werten. Sämtliche Parameter können über
	 * Setter-Methoden überschrieben werden.
	 *
	 * @param producer PDF Metadata-Producer
	 * @param summaryTablePosition Falls Alignment.LEFT, wird die Zusammenfassung links und die Adresse rechts
	 * angezeigt. Falls Alignment.RIGHT, wird die Zusammenfassung rechts und die Adresse links angezeigt.
	 */
	public InvoiceGeneratorConfiguration(@NonNull String producer, @NonNull Alignment summaryTablePosition) {
		super(producer, new AddressComponent(summaryTablePosition));
		this.summaryTablePosition = summaryTablePosition;
	}

	public InvoiceGeneratorConfiguration(
		@NonNull String producer,
		@NonNull AddressComponent addressComponent,
		@NonNull Alignment summaryTablePosition) {
		super(producer, addressComponent);
		this.summaryTablePosition = summaryTablePosition;
	}

	public void addDummyESR() {
		addDummyESR(OnPage.NOT_LAST);
	}

	public void addDummyESR(@NonNull OnPage onPage) {
		DummyEinzahlungsschein einzahlungsschein = new DummyEinzahlungsschein();
		getCustomComponents().add(new OrangerEinzahlungsscheinComponent(
			einzahlungsscheinConfiguration,
			einzahlungsschein,
			onPage));

		setBottomMarginInPoints(ESR_HEIGHT_WITH_MARGIN + einzahlungsscheinConfiguration.getYOffset());
	}

	@Nullable
	public List<String> getZahlungsKonditionen() {
		return zahlungsKonditionen;
	}

	public void setZahlungsKonditionen(@Nullable List<String> zahlungsKonditionen) {
		this.zahlungsKonditionen = zahlungsKonditionen;
	}

	@NonNull
	public Alignment getSummaryTablePosition() {
		return summaryTablePosition;
	}

	@Override
	@NonNull
	public String toString() {
		return new StringJoiner(", ", InvoiceGeneratorConfiguration.class.getSimpleName() + '[', "]")
			.add("summaryTablePosition=" + summaryTablePosition)
			.add("zahlungsKonditionen=" + zahlungsKonditionen)
			.add("einzahlungsscheinConfiguration=" + einzahlungsscheinConfiguration)
			.add(super.toString())
			.toString();
	}

	public void setEsrLeftOffsetInMm(float esrLeftOffsetInMm) {
		this.einzahlungsscheinConfiguration.setLeftOffsetInMm(esrLeftOffsetInMm);
	}

	public void setEsrTopOffsetInMm(float esrTopOffsetInMm) {
		this.einzahlungsscheinConfiguration.setTopOffsetInMm(esrTopOffsetInMm);
	}

	public void setEinzahlungsscheinNotOnPageOne(boolean einzahlungsscheinNotOnPageOne) {
		this.einzahlungsscheinConfiguration.setEinzahlungsscheinNotOnPageOne(einzahlungsscheinNotOnPageOne);
	}

	public void setAddEsrBackgroundImage(boolean addEsrBackgroundImage) {
		this.einzahlungsscheinConfiguration.setAddEsrBackgroundImage(addEsrBackgroundImage);
	}

	@NonNull
	public EinzahlungsscheinConfiguration getEinzahlungsscheinConfiguration() {
		return einzahlungsscheinConfiguration;
	}
}
