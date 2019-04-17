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

@SuppressWarnings("PMD.ClassNamingConventions")
public final class OrangerEinzahlungsscheinConstants {

	public static final int PAGE_WIDTH_IN_POINTS = 595;
	public static final int POINTS_PER_INCH = 72;

	public static final int ANTEIL_INCHES_PRO_ZEILE = 6;
	public static final int ANTEIL_INCHES_PRO_ZELLE = 10;

	public static final int EMPFANGSSCHEIN_X = 2 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE) + 3;
	public static final int EMPFANGSSCHEIN_WIDTH = 20 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE - 3;

	public static final int KODIERZEILE_X = PAGE_WIDTH_IN_POINTS - (3 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE) - 3;
	public static final int KODIERZEILE_Y = 4 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;

	public static final int REFERENZNUMMER_1_X = PAGE_WIDTH_IN_POINTS -
		4 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE);
	public static final int REFERENZNUMMER_1_Y = 16 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 2;

	public static final int REFERENZNUMMER_EMPFANGSSCHEIN_X = EMPFANGSSCHEIN_X;
	public static final int REFERENZNUMMER_EMPFANGSSCHEIN_Y = 10 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 2;
	public static final int REFERENZNUMMER_EMPFANGSSCHEIN_FONT_SIZE = 9;

	public static final int BETRAG_Y = 12 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 2;
	public static final int BETRAG_RAPPEN_1_X = 20 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE);
	public static final int BETRAG_CHF_1_X = 16 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE);
	public static final int BETRAG_RAPPEN_2_X = BETRAG_RAPPEN_1_X + 25 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE);
	public static final int BETRAG_CHF_2_X = BETRAG_CHF_1_X + 25 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE);

	public static final int KONTO_X_1 = 11 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE);
	public static final int KONTO_X_2 = 36 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE);
	public static final int KONTO_Y = 14 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 4;

	public static final int EINBEZAHLT_VON_1_X = 50 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE) + 1;
	public static final int EINBEZAHLT_VON_1_Y = 6 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 6;
	public static final int EINBEZAHLT_VON_1_HEIGHT = 7 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;
	public static final int EINBEZAHLT_VON_1_WIDTH = 31 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE;

	public static final int EINBEZAHLT_VON_2_X = EMPFANGSSCHEIN_X;
	public static final int EINBEZAHLT_VON_2_Y = 3 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 6;
	public static final int EINBEZAHLT_VON_2_HEIGHT = 6 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;
	public static final int EINBEZAHLT_VON_2_WIDTH = EMPFANGSSCHEIN_WIDTH;

	public static final int EINZAHLUNG_FUER_1_X = 25 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE) + 3;
	public static final int EINZAHLUNG_FUER_1_Y = 15 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 6;
	public static final int EINZAHLUNG_FUER_1_HEIGHT = 7 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;
	public static final int EINZAHLUNG_FUER_1_WIDTH = 22 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE;

	public static final int EINZAHLUNG_FUER_2_X = EMPFANGSSCHEIN_X;
	public static final int EINZAHLUNG_FUER_2_Y = EINZAHLUNG_FUER_1_Y;
	public static final int EINZAHLUNG_FUER_2_HEIGHT = 7 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;
	public static final int EINZAHLUNG_FUER_2_WIDTH = EMPFANGSSCHEIN_WIDTH;

	public static final int EINZAHLUNG_FUER_BANK_1_X = 25 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE) + 3;
	public static final int EINZAHLUNG_FUER_BANK_1_Y = 21 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 4;
	public static final int EINZAHLUNG_FUER_BANK_1_HEIGHT = 2 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;
	public static final int EINZAHLUNG_FUER_BANK_1_WIDTH = EINZAHLUNG_FUER_1_WIDTH;

	public static final int EINZAHLUNG_FUER_BANK_2_X = EMPFANGSSCHEIN_X;
	public static final int EINZAHLUNG_FUER_BANK_2_Y = EINZAHLUNG_FUER_BANK_1_Y;
	public static final int EINZAHLUNG_FUER_BANK_2_HEIGHT = 2 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;
	public static final int EINZAHLUNG_FUER_BANK_2_WIDTH = EMPFANGSSCHEIN_WIDTH;

	public static final int ZUGUNSTEN_VON_1_X = 25 * (POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZELLE) + 3;
	public static final int ZUGUNSTEN_VON_1_Y = 15 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE + 4;
	public static final int ZUGUNSTEN_VON_1_HEIGHT = 5 * POINTS_PER_INCH / ANTEIL_INCHES_PRO_ZEILE;
	public static final int ZUGUNSTEN_VON_1_WIDTH = EINZAHLUNG_FUER_1_WIDTH;

	public static final int ZUGUNSTEN_VON_2_X = EMPFANGSSCHEIN_X;
	public static final int ZUGUNSTEN_VON_2_Y = ZUGUNSTEN_VON_1_Y;
	public static final int ZUGUNSTEN_VON_2_HEIGHT = ZUGUNSTEN_VON_1_HEIGHT;
	public static final int ZUGUNSTEN_VON_2_WIDTH = EMPFANGSSCHEIN_WIDTH;

	public static final int KONTO_PARTS = 3;
	public static final int MAX_LENGTH_OF_ORDNUNGSNUMMER = 6;

	private OrangerEinzahlungsscheinConstants() {
	}

}
