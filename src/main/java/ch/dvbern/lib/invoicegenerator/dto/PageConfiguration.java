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
package ch.dvbern.lib.invoicegenerator.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.fonts.FontConfiguration;
import ch.dvbern.lib.invoicegenerator.dto.position.H1Position;
import ch.dvbern.lib.invoicegenerator.dto.position.H2Position;
import ch.dvbern.lib.invoicegenerator.dto.position.Position;
import ch.dvbern.lib.invoicegenerator.dto.position.RechnungsPosition;
import ch.dvbern.lib.invoicegenerator.strategy.position.H1Strategy;
import ch.dvbern.lib.invoicegenerator.strategy.position.H2Strategy;
import ch.dvbern.lib.invoicegenerator.strategy.position.PositionStrategy;
import ch.dvbern.lib.invoicegenerator.strategy.position.RechnungsPositionStrategy;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;

import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_MULTIPLIED_H1_LEADING;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_MULTIPLIED_H2_LEADING;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_MULTIPLIED_LEADING;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_MULTIPLIED_TITLE_LEADING;
import static ch.dvbern.lib.invoicegenerator.pdf.PdfUtilities.DEFAULT_SPACE_BEFORE;
import static com.lowagie.text.Utilities.millimetersToPoints;

/**
 * Definiert das Basis-Layout des Dokuments
 *
 * @author Xaver Weibel
 */
public class PageConfiguration {

	public static final float LEFT_PAGE_DEFAULT_MARGIN_MM = 20;
	public static final float RIGHT_PAGE_DEFAULT_MARGIN_MM = 20;
	public static final float TOP_PAGE_DEFAULT_MARGIN_MM = 100;
	public static final float BOTTOM_PAGE_DEFAULT_MARGIN_MM = 15;

	private float leftPageMarginInPoints = millimetersToPoints(LEFT_PAGE_DEFAULT_MARGIN_MM);
	private float rightPageMarginInPoints = millimetersToPoints(RIGHT_PAGE_DEFAULT_MARGIN_MM);
	private float topMarginInPoints = millimetersToPoints(TOP_PAGE_DEFAULT_MARGIN_MM);
	private float bottomMarginInPoints = millimetersToPoints(BOTTOM_PAGE_DEFAULT_MARGIN_MM);

	private float spaceBefore = DEFAULT_SPACE_BEFORE;
	private float multipliedLeadingDefault = DEFAULT_MULTIPLIED_LEADING;
	private float multipliedTitleLeading = DEFAULT_MULTIPLIED_TITLE_LEADING;
	private float multipliedLeadingH1 = DEFAULT_MULTIPLIED_H1_LEADING;
	private float multipliedLeadingH2 = DEFAULT_MULTIPLIED_H2_LEADING;

	@Nonnull
	private FontConfiguration fonts = new FontConfiguration(Font.TIMES_ROMAN);

	@Nonnull
	private Rectangle pageSize = PageSize.A4;

	@Nonnull
	private final Map<Class<? extends Position>, PositionStrategy> positionStrategyMap = new HashMap<>();

	/**
	 * Erstellt eine neue LayoutConfiguration mit default Werten. Sämtliche Parameter können über
	 * Setter-Methoden überschrieben werden.
	 */
	public PageConfiguration() {
		this.positionStrategyMap.put(H1Position.class, new H1Strategy());
		this.positionStrategyMap.put(H2Position.class, new H2Strategy());
		this.positionStrategyMap.put(RechnungsPosition.class, new RechnungsPositionStrategy());
	}

	public void setMargins(
		float leftPageMarginInMm,
		float rightPageMarginInMm,
		float topMarginInMm,
		float bottomMarginInMm) {

		this.leftPageMarginInPoints = millimetersToPoints(leftPageMarginInMm);
		this.rightPageMarginInPoints = millimetersToPoints(rightPageMarginInMm);
		this.topMarginInPoints = millimetersToPoints(topMarginInMm);
		this.bottomMarginInPoints = millimetersToPoints(bottomMarginInMm);
	}

	public float getLeftPageMarginInPoints() {
		return leftPageMarginInPoints;
	}

	public void setLeftPageMarginInPoints(float leftPageMarginInPoints) {
		this.leftPageMarginInPoints = leftPageMarginInPoints;
	}

	public float getRightPageMarginInPoints() {
		return rightPageMarginInPoints;
	}

	public void setRightPageMarginInPoints(float rightPageMarginInPoints) {
		this.rightPageMarginInPoints = rightPageMarginInPoints;
	}

	public float getTopMarginInPoints() {
		return topMarginInPoints;
	}

	public void setTopMarginInPoints(float topMarginInPoints) {
		this.topMarginInPoints = topMarginInPoints;
	}

	public float getBottomMarginInPoints() {
		return bottomMarginInPoints;
	}

	public void setBottomMarginInPoints(float bottomMarginInPoints) {
		this.bottomMarginInPoints = bottomMarginInPoints;
	}

	@Nonnull
	public Rectangle getPageSize() {
		return pageSize;
	}

	public void setPageSize(@Nonnull Rectangle pageSize) {
		this.pageSize = pageSize;
	}

	public float getContentWidth() {
		return pageSize.getWidth() - leftPageMarginInPoints - rightPageMarginInPoints;
	}

	public float getContentHight() {
		return pageSize.getHeight() - topMarginInPoints - bottomMarginInPoints;
	}

	public float getSpaceBefore() {
		return spaceBefore;
	}

	public void setSpaceBefore(float spaceBefore) {
		this.spaceBefore = spaceBefore;
	}

	public float getMultipliedTitleLeading() {
		return multipliedTitleLeading;
	}

	public void setMultipliedTitleLeading(float multipliedTitleLeading) {
		this.multipliedTitleLeading = multipliedTitleLeading;
	}

	public float getMultipliedLeadingDefault() {
		return multipliedLeadingDefault;
	}

	public void setMultipliedLeadingDefault(float multipliedLeadingDefault) {
		this.multipliedLeadingDefault = multipliedLeadingDefault;
	}

	public float getMultipliedLeadingH1() {
		return multipliedLeadingH1;
	}

	public void setMultipliedLeadingH1(float multipliedLeadingH1) {
		this.multipliedLeadingH1 = multipliedLeadingH1;
	}

	public float getMultipliedLeadingH2() {
		return multipliedLeadingH2;
	}

	public void setMultipliedLeadingH2(float multipliedLeadingH2) {
		this.multipliedLeadingH2 = multipliedLeadingH2;
	}

	@Nonnull
	public FontConfiguration getFonts() {
		return fonts;
	}

	public void setFonts(@Nonnull FontConfiguration fonts) {
		this.fonts = fonts;
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", PageConfiguration.class.getSimpleName() + '[', "]")
			.add("leftPageMarginInPoints=" + leftPageMarginInPoints)
			.add("rightPageMarginInPoints=" + rightPageMarginInPoints)
			.add("topMarginInPoints=" + topMarginInPoints)
			.add("bottomMarginInPoints=" + bottomMarginInPoints)
			.add("positionStrategyMap=" + positionStrategyMap)
			.toString();
	}

	@Nonnull
	public Map<Class<? extends Position>, PositionStrategy> getPositionStrategyMap() {
		return positionStrategyMap;
	}
}
