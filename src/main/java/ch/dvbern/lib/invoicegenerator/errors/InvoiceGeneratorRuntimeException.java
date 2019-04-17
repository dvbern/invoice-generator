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
package ch.dvbern.lib.invoicegenerator.errors;

import javax.annotation.Nonnull;

/**
 * Runtime-Exception, die bei technischen Problemen geworfen wird
 *
 * @author Xaver Weibel
 */
public class InvoiceGeneratorRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -7535655007426984656L;

	/**
	 * Erstellt eine neue Instanz mit der entsprechenden Message
	 *
	 * @param message die Message
	 */
	public InvoiceGeneratorRuntimeException(@Nonnull String message) {
		super(message);
	}

	/**
	 * Erstellt eine neue Instanz mit der entsprechenden Message und Cause
	 *
	 * @param message die Message
	 * @param cause die Cause
	 */
	public InvoiceGeneratorRuntimeException(@Nonnull String message, @Nonnull Throwable cause) {
		super(message, cause);
	}
}
