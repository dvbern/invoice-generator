/*
 * Copyright © 2020 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.lib.invoicegenerator.dto.component;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.EinzahlungsscheinConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.einzahlungsschein.QRCodeEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;

/**
 * Can be used to add a QR Code Receipt and Payment part to a page.
 *
 * Assumes, that the page dimensions are A4.
 */
public class QRCodeComponent extends ComponentRenderer<SimpleConfiguration, QRCodeEinzahlungsschein> {

	private static final String CURRENCY = "CHF";

	private float xOffset = 0;
	private float yOffset = 0;

	public QRCodeComponent(
		@Nonnull EinzahlungsscheinConfiguration config,
		@Nonnull QRCodeEinzahlungsschein qrCodeEinzahlungsschein,
		@Nonnull OnPage onPage) {
		super(new SimpleConfiguration(onPage), qrCodeEinzahlungsschein);

		this.xOffset = config.getXOffset();
		this.yOffset = config.getYOffset();
	}

	public static byte[] generateQRCode(@Nonnull QRCodeEinzahlungsschein qrCodeEinzahlungsschein) throws IOException,
		QRBillValidationError {
		// Set Rechnung format
		Bill bill = new Bill();
		bill.getFormat().setLanguage(qrCodeEinzahlungsschein.getLanguage());
		bill.getFormat().setGraphicsFormat(GraphicsFormat.PDF);

		// Set Rechnung data
		bill.setAccount(qrCodeEinzahlungsschein.getKonto());
		bill.setAmount(qrCodeEinzahlungsschein.getBetrag());
		bill.setCurrency(CURRENCY);
		bill.setReference(qrCodeEinzahlungsschein.getReferenzNr().toString());
		bill.setUnstructuredMessage(qrCodeEinzahlungsschein.getAdditionalText());

		// Set creditor
		bill.setCreditor(qrCodeEinzahlungsschein.getEinzahlungFuer());

		// Set debtor
		bill.setDebtor(qrCodeEinzahlungsschein.getEinzahlungVon());

		// Generate QR bill
		PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT);
		bill.getFormat().setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
		QRBill.draw(bill, canvas);

		return canvas.toByteArray();
	}

	@Override
	public void render(@Nonnull PdfWriter pdfWriter, @Nonnull PdfElementGenerator pdfElementGenerator)
		throws InvoiceGeneratorRuntimeException {
		Objects.requireNonNull(getPayload());

		try {
			byte[] pdf = generateQRCode(getPayload());
			// reads the binary PDF file generated from the QR Bill library and adds it to actual invoice
			PdfReader pdfReader = new PdfReader(pdf);
			PdfImportedPage importedPage = pdfWriter.getImportedPage(pdfReader, 1);
			pdfWriter.getDirectContent().addTemplate(importedPage, xOffset, yOffset);
		} catch (QRBillValidationError | IOException e) {
			throw new InvoiceGeneratorRuntimeException("Could not initialize QR Code", e);
		}
	}

	@Override
	public void render(
		@Nonnull PdfContentByte directContent,
		@Nonnull PdfElementGenerator pdfElementGenerator) throws DocumentException {
	}
}
