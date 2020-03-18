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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.InvoiceGeneratorConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.QRCodeEinzahlungsschein;
import ch.dvbern.lib.invoicegenerator.dto.component.QRCodeComponent;
import ch.dvbern.lib.invoicegenerator.pdf.PdfGenerator;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.lowagie.text.Utilities.millimetersToPoints;
import static org.junit.Assert.assertEquals;

public class QRCodeGeneratorTest {
	private final BigDecimal betrag = new BigDecimal("3949.75");
	private final Address einbezahltVon = new Address();
	private final Address einzahlungFuer = new Address();

	private final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.LEFT);

	@Before
	public void init() {
		initAddress(einbezahltVon, "Rutschmann Pia", "28", "Marktgasse", "Rorschach", "94900");
		initAddress(einzahlungFuer, "Robert Schneider SA", "55A", "Case postale", "Biel/Bienne", "2501");
	}

	private void initAddress(Address address,
		String name,
		String houseNumber,
		String street,
		String town,
		String postcode) {
		address.setName(name);
		address.setHouseNo(houseNumber);
		address.setStreet(street);
		address.setTown(town);
		address.setPostalCode(postcode);
		address.setCountryCode("CH");
	}

	@Test
	public void testTheCreationOfABankEsrWithLoooongContent()
		throws DocumentException, IOException {
		final QRCodeEinzahlungsschein einzahlungsschein = new QRCodeEinzahlungsschein(
			einzahlungFuer,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"CH44 3199 9123 0008 8901 2",
			einbezahltVon,
			null,
			null);

		create("target/QRCode.pdf", einzahlungsschein);
	}

	@Test
	public void testQRIBANValidation() throws DocumentException {
		final QRCodeEinzahlungsschein einzahlungsschein = new QRCodeEinzahlungsschein(
			einzahlungFuer,
			new BigInteger("120000000000234478943216899"),
			betrag,
			"CH93 0076 2011 6238 5295 7", //Not a valid QR-IBAN
			einbezahltVon,
			null,
			null);
		try {
			create("target/QRCodeGenerationError.pdf", einzahlungsschein);
		} catch (QRBillValidationError e) {
			List<ValidationMessage> valMessages = e.getValidationResult().getValidationMessages();

			assertEquals (1, valMessages.size());
			assertEquals ("ERROR", valMessages.get(0).getType().toString());
			assertEquals("valid_iso11649_creditor_ref", valMessages.get(0).getMessageKey());

		} catch (IOException e) {
			// do Nothing
		}
	}

	private void create(
		@Nonnull String path,
		@Nonnull QRCodeEinzahlungsschein einzahlungsschein) throws IOException, QRBillValidationError {

		PdfGenerator generator = new PdfGenerator(new FileOutputStream(path), configuration);

		PdfContentByte content = generator.getDirectContent();

		// Save QR bill
		byte[] png = QRCodeComponent.generateQRCode(einzahlungsschein);
		Image image = Image.getInstance(png);
		float percent = 100 * millimetersToPoints(200) / image.getWidth();
		image.scalePercent(percent);
		float absoluteY = PageSize.A4.getHeight() - image.getScaledHeight();
		image.setAbsolutePosition(20, absoluteY);
		content.addImage(image);

		generator.close();
		Assert.assertTrue(new File(path).isFile());
	}
}
