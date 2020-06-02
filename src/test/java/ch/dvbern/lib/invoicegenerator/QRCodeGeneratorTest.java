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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.component.QRCodeComponent;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.EinzahlungsscheinConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.QRCodeEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationMessage.Type;
import net.codecrete.qrbill.generator.ValidationResult;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QRCodeGeneratorTest {

	@Test
	public void testTheCreationOfABankEsrWithLoooongContent()
		throws DocumentException, IOException {

		ByteArrayOutputStream outputStream = create(TestDataUtil.QR_CODE_EINZAHLUNGSSCHEIN);

		String path = "target/QRCode.pdf";
		FileOutputStream fileOutputStream = new FileOutputStream(path);
		outputStream.writeTo(fileOutputStream);

		FileInputStream fileInputStream = new FileInputStream(path);
		String text = TestUtil.getText(fileInputStream);

		assertThat(text, allOf(
			containsString(TestDataUtil.QR_IBAN),
			containsString(TestDataUtil.CREDITOR.getName()),
			containsString(TestDataUtil.DEBTOR.getName()),
			containsString(TestDataUtil.QR_REFERENCE_FORMATTED)
		));
	}

	@Test
	public void testQRIBANValidation() throws DocumentException {
		final QRCodeEinzahlungsschein einzahlungsschein = new QRCodeEinzahlungsschein(
			TestDataUtil.CREDITOR,
			TestDataUtil.QR_REFERENCE,
			TestDataUtil.AMOUNT,
			"CH93 0076 2011 6238 5295 7", // not a valid QR-IBAN
			TestDataUtil.DEBTOR,
			null,
			null);

		//noinspection ResultOfMethodCallIgnored
		InvoiceGeneratorRuntimeException ex =
			assertThrows(InvoiceGeneratorRuntimeException.class, () -> create(einzahlungsschein));

		QRBillValidationError cause = (QRBillValidationError) ex.getCause();
		assertThat(cause, pojo(QRBillValidationError.class)
			.where(QRBillValidationError::getValidationResult, pojo(ValidationResult.class)
				.where(ValidationResult::getValidationMessages, containsInAnyOrder(
					pojo(ValidationMessage.class)
						.where(ValidationMessage::getType, equalTo(Type.ERROR))
						.where(ValidationMessage::getField, equalTo("reference"))
						.where(ValidationMessage::getMessageKey, equalTo("valid_iso11649_creditor_ref"))
				))
			));
	}

	@Nonnull
	private ByteArrayOutputStream create(@Nonnull QRCodeEinzahlungsschein einzahlungsschein)
		throws QRBillValidationError {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		document.newPage();

		EinzahlungsscheinConfiguration config = new EinzahlungsscheinConfiguration();
		QRCodeComponent component = new QRCodeComponent(config, einzahlungsschein, OnPage.LAST);
		component.render(writer, new PdfElementGenerator(new PageConfiguration()));

		document.close();

		return outputStream;
	}
}
