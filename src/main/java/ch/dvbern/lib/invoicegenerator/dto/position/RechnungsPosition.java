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
package ch.dvbern.lib.invoicegenerator.dto.position;

import java.util.StringJoiner;

import org.jspecify.annotations.NonNull;

/**
 * RechnungsPosition-Implementierung einer Position.
 *
 * @author Xaver Weibel
 * @see H1Position
 * @see H2Position
 */
public class RechnungsPosition implements Position {

	@NonNull
	private final String leistung;
	@NonNull
	private final String menge;
	@NonNull
	private final String preis;
	@NonNull
	private final String total;

	/**
	 * Erstellt eine neue Rechnungsposition
	 *
	 * @param leistung Die Leistung respektive der Beschrieb der Rechnungsposition
	 * @param menge Die Menge als String. Dies wurde bewusst als String definiert, da die Library das Runden /
	 * Formatieren von Zahlen nicht übernehmen will
	 * @param preis Der Preis als String. Dieser wurde bewusst als String definiert, da die Library das Runden /
	 * Formatieren von Zahlen nicht übernehmen will
	 * @param total Das Total als String. Das Total wurde bewusst als String definiert, da die Library das Runden /
	 * Formatieren von Zahlen nicht übernehmen will
	 */
	public RechnungsPosition(
		@NonNull String leistung,
		@NonNull String menge,
		@NonNull String preis,
		@NonNull String total) {

		this.leistung = leistung;
		this.menge = menge;
		this.preis = preis;
		this.total = total;
	}

	@Override
	@NonNull
	public String toString() {
		return new StringJoiner(", ", RechnungsPosition.class.getSimpleName() + '[', "]")
			.add("leistung='" + leistung + '\'')
			.add("menge='" + menge + '\'')
			.add("preis='" + preis + '\'')
			.add("total='" + total + '\'')
			.toString();
	}

	@NonNull
	public String getLeistung() {
		return leistung;
	}

	@NonNull
	public String getMenge() {
		return menge;
	}

	@NonNull
	public String getPreis() {
		return preis;
	}

	@NonNull
	public String getTotal() {
		return total;
	}
}


