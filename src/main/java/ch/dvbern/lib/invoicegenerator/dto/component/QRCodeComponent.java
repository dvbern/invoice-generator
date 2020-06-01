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

import ch.dvbern.lib.invoicegenerator.dto.EinzahlungsscheinConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import ch.dvbern.lib.invoicegenerator.dto.QRCodeEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorRuntimeException;
import ch.dvbern.lib.invoicegenerator.pdf.PdfElementGenerator;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import net.codecrete.qrbill.canvas.PNGCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;

import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.QR_RECHNUNG_IMAGE_WIDTH_IN_MM;
import static com.lowagie.text.Utilities.millimetersToPoints;

public class QRCodeComponent extends ComponentRenderer<SimpleConfiguration, QRCodeEinzahlungsschein> {

	private static final int QR_RECHNUNG_RESOLUTION = 315;
	private static final int PERCENT_MULTIPLIER = 100;
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
		bill.getFormat().setGraphicsFormat(GraphicsFormat.PNG);

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
		PNGCanvas canvas = new PNGCanvas(
			QRBill.QR_BILL_WIDTH,
			QRBill.QR_BILL_HEIGHT,
			QR_RECHNUNG_RESOLUTION,
			bill.getFormat().getFontFamily());
		bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
		QRBill.draw(bill, canvas);

		return canvas.toByteArray();
	}

	@Override
	public void render(
		@Nonnull PdfContentByte directContent, @Nonnull PdfElementGenerator pdfElementGenerator)
		throws InvoiceGeneratorRuntimeException {
		Objects.requireNonNull(getPayload());

		try {
			byte[] png = generateQRCode(getPayload());
			Image image = Image.getInstance(png);
			float percent = PERCENT_MULTIPLIER * millimetersToPoints(QR_RECHNUNG_IMAGE_WIDTH_IN_MM) / image.getWidth();
			// Warum wird skaliert?
			image.scalePercent(percent);
			image.setAbsolutePosition(this.xOffset, this.yOffset);

			PdfContentByte canvas = directContent.getPdfWriter().getDirectContentUnder();
			canvas.addImage(image);
		} catch (QRBillValidationError | IOException e) {
			throw new InvoiceGeneratorRuntimeException("Could not initialize QR Code", e);
		}
	}
}
