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
 * H1 Implementierung einer Position.
 *
 * @author Xaver Weibel
 * @see H2Position
 * @see RechnungsPosition
 */
public class H1Position implements Position {

	@NonNull
	private final String beschrieb;

	/**
	 * Erstellt eine neue H1Position mit dem übergebenen Beschrieb.
	 *
	 * @param beschrieb Der Text der Position
	 */
	public H1Position(@NonNull String beschrieb) {
		this.beschrieb = beschrieb;
	}

	@NonNull
	public String getBeschrieb() {
		return beschrieb;
	}

	@Override
	@NonNull
	public String toString() {
		return new StringJoiner(", ", H1Position.class.getSimpleName() + '[', "]")
			.add("beschrieb='" + beschrieb + '\'')
			.toString();
	}
}
