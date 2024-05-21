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

import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class ComponentRenderer<C extends ComponentConfiguration, Payload> {

	@NonNull
	private final C componentConfiguration;

	@Nullable
	private Payload payload = null;

	protected ComponentRenderer(@NonNull C componentConfiguration, @NonNull Payload payload) {
		this.componentConfiguration = componentConfiguration;
		this.payload = payload;
	}

	protected ComponentRenderer(@NonNull C componentConfiguration) {
		this.componentConfiguration = componentConfiguration;
	}

	public abstract void render(
		@NonNull PdfContentByte directContent,
		@NonNull PdfElementGenerator pdfElementGenerator)
		throws DocumentException;

	public void render(@NonNull PdfWriter pdfWriter, @NonNull PdfElementGenerator pdfElementGenerator)
		throws DocumentException {
		render(pdfWriter.getDirectContent(), pdfElementGenerator);
	}

	@NonNull
	public C getComponentConfiguration() {
		return componentConfiguration;
	}

	@Nullable
	public Payload getPayload() {
		return payload;
	}

	public void setPayload(@Nullable Payload payload) {
		this.payload = payload;
	}
}
