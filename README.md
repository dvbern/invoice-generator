# DV Bern Invoice Generator Library

This library can be used to create PDF invoices with or without a payment slip.

## Usage

1. Create an InvoiceGeneratorConfiguration. This contains the layout for invoices (Header, Footer, Bank account, ...). There are also static elements available, that remain the same for all invoices, such as payment conditions.
2. An InvoiceGenerator instance can now be created with the InvoiceGeneratorConfiguration.
3. The InvoiceGenerator can now be used to create PDF invoices from Invoice objects. (bearbeitet) 

```java

private final InvoiceGeneratorConfiguration configuration = new InvoiceGeneratorConfiguration(Alignment.LEFT);
InvoiceGenerator invoiceGenerator = new InvoiceGenerator(configuration);
final Invoice invoice = new Invoice(columnTitle, titel, summary, einleitung, adresse, einzahlungsschein, positionen, total);
invoiceGenerator.generateInvoice(new FileOutputStream("target/Invoice.pdf"), invoice);

```

## Configuring fonts

To be able to use specific fonts, in particular the OCR-B font required for the coding line of the payment slip, these must
be registered as follows:

```java
// Must be defined before calling FontFactory.getFont()
static {
	FontFactory.register("/font/br_ocrb.ttf", FONT_FACE_OCRB);				// font for coding line of the payment slip 
	FontFactory.register("/font/arial.ttf", FONT_FACE_PROXIMA_NOVA); 		// font for invoice
	FontFactory.register("/font/arial.ttf", FONT_FACE_PROXIMA_NOVA_BOLD); 	// font for invoice (bold)
}
```
By default, the font Arial is used both for the invoice and for the coding line.

## Where can I get the artifact?
Sorry, we don't have any artifacts on a public repository. You will have to build this project and https://github.com/dvbern/oss-maven-parent-pom yourself.


