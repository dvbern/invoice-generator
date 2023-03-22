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

import java.awt.Color;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.lib.invoicegenerator.dto.Alignment;
import ch.dvbern.lib.invoicegenerator.dto.PageConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.SummaryEntry;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontBuilder;
import ch.dvbern.lib.invoicegenerator.dto.fonts.FontModifier;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPositionColumnTitle;
import ch.dvbern.lib.invoicegenerator.strategy.position.PositionStrategy;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.jetbrains.annotations.Contract;

import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.PADDING_BOTTOM;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.joinListToString;

public class PdfElementGenerator {

	public static final int POSITIONEN_COLUMNS = 4;
	public static final int SUMMARY_COLUMNS = 3;
	public static final int FULL_WIDTH = 100;
	@Nonnull
	private final PageConfiguration configuration;

	public PdfElementGenerator(@Nonnull PageConfiguration configuration) {
		this.configuration = configuration;
	}

	@Nullable
	@Contract("null->null; !null->!null")
	public Paragraph createParagraph(@Nullable List<String> list) {
		if (list == null) {
			return null;
		}

		return createParagraph(joinListToString(list));
	}

	@Nonnull
	public Paragraph createParagraph(@Nonnull String string) {
		Paragraph paragraph = new Paragraph(string, configuration.getFonts().getFont());
		paragraph.setLeading(0, configuration.getMultipliedLeadingDefault());

		return paragraph;
	}

	@Nonnull
	public Paragraph createTitle(@Nonnull String title) {
		return createTitle(title, configuration.getMultipliedTitleLeading());
	}

	@Nonnull
	public Paragraph createTitle(@Nonnull String title, float leading) {
		Paragraph paragraph = new Paragraph(title, configuration.getFonts().getFontTitle());
		paragraph.setLeading(0, leading);
		paragraph.setSpacingAfter(PADDING_BOTTOM);

		return paragraph;
	}

	@Nonnull
	public PdfPCell createTitleCell(
		@Nonnull final String string,
		final boolean rightAlign,
		final float multipliedLeading) {

		final Phrase phrase = new Phrase(string, configuration.getFonts().getFontBold());
		PdfPCell cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.BOTTOM);
		cell.setBorderColor(Color.GRAY);
		cell.setPaddingBottom(PADDING_BOTTOM);
		cell.setLeading(0, multipliedLeading);

		if (rightAlign) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		}

		return cell;
	}

	@Nonnull
	public PdfPTable createRechnungspositionsTabelle(
		@Nonnull RechnungsPositionColumnTitle rechnungsPositionColumnTitle,
		@Nonnull List<Position> positionen)
		throws DocumentException {

		PdfPTable table = new PdfPTable(POSITIONEN_COLUMNS);
		table.setSpacingBefore(configuration.getSpaceBefore());
		table.setWidths(configuration.getRechnungsTabelleWidths().getPositionenColumnWidths());
		table.setWidthPercentage(FULL_WIDTH);
		float defaultLeading = configuration.getMultipliedLeadingDefault();
		table.addCell(createTitleCell(rechnungsPositionColumnTitle.getLeistung(), false, defaultLeading));
		table.addCell(createTitleCell(rechnungsPositionColumnTitle.getMenge(), true, defaultLeading));
		table.addCell(createTitleCell(rechnungsPositionColumnTitle.getPreis(), true, defaultLeading));
		table.addCell(createTitleCell(rechnungsPositionColumnTitle.getTotal(), true, defaultLeading));
		table.setHeaderRows(1);

		for (int i = 0; i < positionen.size(); i++) {
			Position position = positionen.get(i);

			PositionStrategy strategy = configuration.getPositionStrategyMap().get(position.getClass());
			strategy.addPositionToTable(configuration, table, position, i + 1 == positionen.size());
		}

		return table;
	}

	@Nonnull
	public PdfPTable createSummaryTable(
		@Nonnull List<SummaryEntry> summaryEntries,
		@Nonnull Alignment tableAlignment,
		@Nullable Paragraph descriptionContent,
		int valueHorizontalAllign) throws DocumentException {

		float multipliedLeadingDefault = configuration.getMultipliedLeadingDefault();
		PdfPTable table = new PdfPTable(SUMMARY_COLUMNS);
		table.setKeepTogether(true);
		float[] relativeWidths = configuration.getRechnungsTabelleWidths().getSummaryColumnWidths(tableAlignment);
		table.setWidths(relativeWidths);
		table.setWidthPercentage(FULL_WIDTH);
		PdfPCell descriptionCell = new PdfPCell(descriptionContent);
		descriptionCell.setBorder(Rectangle.NO_BORDER);
		descriptionCell.setVerticalAlignment(Element.ALIGN_TOP);

		descriptionCell.setLeading(0, multipliedLeadingDefault);
		descriptionCell.setRowspan(summaryEntries.size());

		// am Beginn hinzufügen wenn rechts
		if (tableAlignment == Alignment.RIGHT) {
			table.addCell(descriptionCell);
		}

		IntStream.range(0, summaryEntries.size()).forEach(i -> {
			createSummaryEntryCell(table, summaryEntries.get(i), valueHorizontalAllign,
				multipliedLeadingDefault);

			// Da die Description cell rowspan verwendet muss die Description cell noch während die 1. row erstellt
			// wird der Tabelle hinzugefügt werden
			if (i == 0 && tableAlignment == Alignment.LEFT) {
				table.addCell(descriptionCell);
			}
		});

		return table;
	}

	private void createSummaryEntryCell(
		@Nonnull PdfPTable table,
		@Nonnull SummaryEntry summaryEntry,
		int valueHorizontalAllign,
		final float leading) {

		Font font = summaryEntry.isBold() ? configuration.getFonts().getFontBold() :
			configuration.getFonts().getFont();
		PdfPCell labelCell = new PdfPCell(new Phrase(summaryEntry.getLabel(), font));
		PdfPCell valueCell = new PdfPCell(new Phrase(summaryEntry.getValue(), font));
		labelCell.setLeading(0, leading);
		valueCell.setLeading(0, leading);
		valueCell.setHorizontalAlignment(valueHorizontalAllign);

		if (summaryEntry.isUnderlined()) {
			labelCell.setPaddingBottom(PADDING_BOTTOM);
			valueCell.setPaddingBottom(PADDING_BOTTOM);
			labelCell.setBorder(Rectangle.BOTTOM);
			valueCell.setBorder(Rectangle.BOTTOM);
			labelCell.setBorderColor(Color.GRAY);
			valueCell.setBorderColor(Color.GRAY);
		} else {
			labelCell.setBorder(Rectangle.NO_BORDER);
			valueCell.setBorder(Rectangle.NO_BORDER);
		}

		table.addCell(labelCell);
		table.addCell(valueCell);
	}

	public void writeSingleLine(
		@Nonnull PdfContentByte directContent,
		@Nonnull String text,
		float lowerLeftXPosition,
		float lowerLeftYPosition,
		int alignment) {

		writeSingleLine(
			directContent,
			lowerLeftXPosition,
			lowerLeftYPosition,
			alignment,
			new Phrase(text, configuration.getFonts().getFont()));
	}

	private void writeSingleLine(
		@Nonnull PdfContentByte directContent,
		float lowerLeftXPosition,
		float lowerLeftYPosition,
		int alignment,
		@Nonnull Phrase phrase) {

		ColumnText.showTextAligned(directContent, alignment, phrase, lowerLeftXPosition, lowerLeftYPosition, 0);
	}

	public void writeSingleOcrbLine(
		@Nonnull PdfContentByte directContent,
		@Nonnull String text,
		float lowerLeftXPosition,
		float lowerLeftYPosition,
		int alignment) {

		Font font = configuration.getFonts().getFontOcrb();
		writeSingleLine(directContent, lowerLeftXPosition, lowerLeftYPosition, alignment, new Phrase(text, font));
	}

	public void writeSingleLine(
		@Nonnull PdfContentByte directContent,
		@Nonnull String text,
		float lowerLeftXPosition,
		float lowerLeftYPosition,
		int alignment,
		float fontSize) {

		Font font = FontBuilder.of(configuration.getFonts().getFont()).with(FontModifier.size(fontSize)).build();

		writeSingleLine(directContent, lowerLeftXPosition, lowerLeftYPosition, alignment, new Phrase(text, font));
	}

	public void writeMultiLine(
		@Nonnull PdfContentByte directContent,
		@Nonnull List<String> text,
		float lowerLeftXPosition,
		float lowerLeftYPosition,
		float width,
		float height)
		throws DocumentException {

		final ColumnText columnText = new ColumnText(directContent);
		float urx = lowerLeftXPosition + width;
		float ury = lowerLeftYPosition + height;
		columnText.setSimpleColumn(lowerLeftXPosition, lowerLeftYPosition, urx, ury);
		columnText.setLeading(0, configuration.getMultipliedLeadingDefault());
		columnText.setText(new Phrase(joinListToString(text), configuration.getFonts().getFont()));
		columnText.go();
	}

	public void addBackgroundImage(
		@Nonnull PdfContentByte directContent,
		@Nonnull Image image)
		throws DocumentException {

		PdfContentByte canvas = directContent.getPdfWriter().getDirectContentUnder();
		canvas.addImage(image);
	}

	@Nonnull
	public PageConfiguration getConfiguration() {
		return configuration;
	}
}
