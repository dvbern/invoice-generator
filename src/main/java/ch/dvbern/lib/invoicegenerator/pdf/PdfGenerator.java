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
package ch.dvbern.lib.invoicegenerator.pdf;

import java.io.OutputStream;

import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.jspecify.annotations.NonNull;

public class PdfGenerator {

	@NonNull
	private final Document document;
	@NonNull
	private final PdfWriter writer;
	@NonNull
	private final PdfContentByte content;

	public PdfGenerator(@NonNull final OutputStream outputStream, @NonNull PageConfiguration configuration)
		throws DocumentException {

		document = new Document(
			configuration.getPageSize(),
			configuration.getLeftPageMarginInPoints(),
			configuration.getRightPageMarginInPoints(),
			configuration.getTopMarginInPoints(),
			configuration.getBottomMarginInPoints());
		writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		content = writer.getDirectContent();
	}

	@NonNull
	public Document getDocument() {
		return this.document;
	}

	public void close() {
		document.close();
	}

	public void setPageEvent(@NonNull PdfPageEventHelper helper) {
		this.writer.setPageEvent(helper);
	}

	public float getVerticalPosition() {
		return writer.getVerticalPosition(false);
	}

	@NonNull
	public PdfContentByte getDirectContent() {
		return content;
	}

	public void printEmptyPage() {
		writer.setPageEmpty(false);
	}
}
