/*
 * Copyright © 2020 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.lib.invoicegenerator;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.EinzahlungsscheinConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.QRCodeEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.component.QRCodeComponent;
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
