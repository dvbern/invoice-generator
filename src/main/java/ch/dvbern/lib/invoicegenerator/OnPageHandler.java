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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.component.ComponentRenderer;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Wendet {@link ComponentRenderer} auf eine Seite an, wenn die per OnPage konfigurierte Seite erreicht wird.
 */
public class OnPageHandler extends PdfPageEventHelper {

	public static final Consumer<PdfContentByte> NOOP = whatever -> {
	};

	@Nonnull
	private final PdfElementGenerator pdfElementGenerator;

	@Nonnull
	private final Consumer<PdfContentByte> directContentConsumer;

	@Nonnull
	private final Map<OnPage, List<ComponentRenderer<? extends ComponentConfiguration, ?>>> components;

	private boolean lastPage = false;

	public OnPageHandler(
		@Nonnull PdfElementGenerator pdfElementGenerator,
		@Nonnull List<ComponentRenderer<? extends ComponentConfiguration, ?>> components) {

		this(pdfElementGenerator, components, NOOP);
	}

	public OnPageHandler(
		@Nonnull PdfElementGenerator pdfElementGenerator,
		@Nonnull List<ComponentRenderer<? extends ComponentConfiguration, ?>> components,
		@Nonnull Consumer<PdfContentByte> directContentConsumer) {

		this.pdfElementGenerator = pdfElementGenerator;
		this.components = components.stream()
			.collect(Collectors.groupingBy(c -> c.getComponentConfiguration().getOnPage()));
		this.directContentConsumer = directContentConsumer;
	}

	@Override
	public void onEndPage(@Nonnull PdfWriter pdfWriter, @Nonnull Document document) {
		PdfContentByte directContent = pdfWriter.getDirectContent();

		components.entrySet().stream()
			.filter(entry -> entry.getKey().isPrintable(document.getPageNumber(), lastPage))
			.map(Entry::getValue)
			.filter(Objects::nonNull)
			.flatMap(Collection::stream)
			.forEach(component -> {
				try {
					component.render(directContent, pdfElementGenerator);
				} catch (DocumentException ex) {
					throw new InvoiceGeneratorRuntimeException("Could not write component " + component, ex);
				}
			});

		directContentConsumer.accept(directContent);
	}

	public void setLastPage() {
		this.lastPage = true;
	}
}
