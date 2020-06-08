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

package ch.dvbern.lib.invoicegenerator;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.BaseLayoutConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.component.AddressComponent;
import ch.dvbern.lib.invoicegenerator.dto.component.AddressRenderer;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentRenderer;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorException;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import ch.dvbern.lib.invoicegenerator.pdf.PdfGenerator;
import com.lowagie.text.DocumentException;

public class BaseGenerator<T extends BaseLayoutConfiguration> {

	@Nonnull
	private final T configuration;
	@Nonnull
	private PdfElementGenerator pdfElementGenerator;

	/**
	 * Erstellt einen neuen default BaseGenerator für die ensprechende Konfiguration.
	 */
	public BaseGenerator() {
		//noinspection unchecked
		this((T) new BaseLayoutConfiguration(new AddressComponent(Alignment.RIGHT)));
	}

	/**
	 * Erstellt einen neuen BaseGenerator für die ensprechende Konfiguration.
	 *
	 * @param configuration Die Konfiguration beinhaltet das Layout des Dokuments (Abstände, Seitengrösse)
	 * sowie statische Elemente, die bei sämtlichen Rechnungen gleich bleiben (Header, Footer, Logo, ...)
	 */
	public BaseGenerator(@Nonnull final T configuration) throws InvoiceGeneratorRuntimeException {
		this.configuration = configuration;
		this.pdfElementGenerator = new PdfElementGenerator(configuration);
	}

	/**
	 * Erstellt aus dem Invoice-Objekt ein PDF.
	 *
	 * @param outputStream Das PDF wird in diesen Stream geschrieben
	 * @param onPageHandler Handler der Komponenten auf der richtigen Seite rendert
	 * @param generator Consumer um den spezifischen Dokumenttyp zu rendern
	 * @throws InvoiceGeneratorException Wird bei bei technischen Problemen geworfen, z.B. falls das Logo nicht
	 *                                   geladen werden kann
	 */
	public void generate(
		@Nonnull OutputStream outputStream,
		@Nonnull OnPageHandler onPageHandler,
		@Nonnull CustomGenerator generator)
		throws InvoiceGeneratorException {

		PdfGenerator pdfGenerator = null;
		try {
			pdfGenerator = new PdfGenerator(outputStream, configuration);

			pdfGenerator.setPageEvent(onPageHandler);

			generator.accept(pdfGenerator);

			onPageHandler.setLastPage();

			// Sometimes, when adding a table, a new page is created as well. This triggers the OnPageHandler events.
			// However, we may not be adding anything else to the new page (no konditionen). In that case the new page
			// ist not written to the actual PDF and therefore the OnPageHandler does not get called with the proper
			// lastPage flag. By making sure that blank pages are written to the PDF, we get the events for all pages
			// and the component renderers are handled correctly (e.g. considering LAST_PAGE)
			pdfGenerator.printEmptyPage();

		} catch (DocumentException exception) {
			throw new InvoiceGeneratorException("Could not generate invoice", exception);
		} finally {
			if (pdfGenerator != null) {
				pdfGenerator.close();
			}
		}
	}

	@Nonnull
	public OnPageHandler getOnPageHandler(@Nonnull List<String> empfaengerAdresse) {
		List<ComponentRenderer<? extends ComponentConfiguration, ?>> components =
			getComponentRenderers(empfaengerAdresse);

		return new OnPageHandler(pdfElementGenerator, components);
	}

	@Nonnull
	public List<ComponentRenderer<? extends ComponentConfiguration, ?>> getComponentRenderers(
		@Nonnull List<String> empfaengerAdresse) {

		List<ComponentRenderer<? extends ComponentConfiguration, ?>> staticComponents =
			configuration.getStaticComponents();

		List<ComponentRenderer<? extends ComponentConfiguration, ?>> components =
			new ArrayList<>(staticComponents);

		components.add(new AddressRenderer(configuration.getEmpfaengerAdresse(), empfaengerAdresse));

		return components;
	}

	/**
	 * @return Erstellt einen simplen OnPageHandler, welcher nur die staticComponents rendert.
	 * @see BaseLayoutConfiguration#getStaticComponents()
	 */
	@Nonnull
	public OnPageHandler createDefaultOnPageHandler() {
		List<ComponentRenderer<? extends ComponentConfiguration, ?>> staticComponents =
			configuration.getStaticComponents();

		return new OnPageHandler(pdfElementGenerator, staticComponents);
	}

	@Nonnull
	public T getConfiguration() {
		return configuration;
	}

	@Nonnull
	public PdfElementGenerator getPdfElementGenerator() {
		return pdfElementGenerator;
	}

	public void setPdfElementGenerator(@Nonnull PdfElementGenerator pdfElementGenerator) {
		this.pdfElementGenerator = pdfElementGenerator;
	}
}
