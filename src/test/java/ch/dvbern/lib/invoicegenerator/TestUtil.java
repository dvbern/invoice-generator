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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.annotation.Nonnull;

import ch.dvbern.lib.invoicegenerator.dto.Invoice;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorException;

public final class TestUtil {

	private TestUtil() {
	}

	@Nonnull
	public static File createFile(
		@Nonnull InvoiceGenerator invoiceGenerator,
		@Nonnull Invoice invoice,
		@Nonnull String filename) throws InvoiceGeneratorException, FileNotFoundException {

		invoiceGenerator.generateInvoice(new FileOutputStream(filename), invoice);

		return new File(filename);
	}
}
