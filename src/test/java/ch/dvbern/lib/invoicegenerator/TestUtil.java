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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ch.dvbern.lib.invoicegenerator.dto.Invoice;
import ch.dvbern.lib.invoicegenerator.errors.InvoiceGeneratorException;
import com.lowagie.text.pdf.PdfReader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontLike;
import org.apache.pdfbox.text.PDFTextStripper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.jspecify.annotations.NonNull;

public final class TestUtil {

	private TestUtil() {
	}

	@NonNull
	public static File createFile(
		@NonNull InvoiceGenerator invoiceGenerator,
		@NonNull Invoice invoice,
		@NonNull String filename) throws InvoiceGeneratorException, IOException {

		invoiceGenerator.generateInvoice(Files.newOutputStream(Paths.get(filename)), invoice);

		return new File(filename);
	}

	@NonNull
	public static byte[] readURL(@NonNull URL url) {
		try {
			URLConnection con = url.openConnection();
			try (InputStream is = con.getInputStream()) {
				return IOUtils.toByteArray(is);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static int getNumberOfPages(@NonNull File file) throws IOException {
		PdfReader reader = new PdfReader(Files.newInputStream(file.toPath()));
		int numberOfPages = reader.getNumberOfPages();
		reader.close();

		return numberOfPages;
	}

	@NonNull
	public static String getText(@NonNull InputStream pdfStream) {
		try (PDDocument document = PDDocument.load(pdfStream)) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			return pdfStripper.getText(document);
		} catch (@NonNull IOException ex) {
			throw new IllegalStateException("Could not extract text", ex);
		}
	}

	@NonNull
	public static Matcher<File> withPages(int numberOfPages) {
		return new TypeSafeDiagnosingMatcher<File>() {
			@Override
			public boolean matchesSafely(@NonNull File actual, @NonNull Description mismatchDescription) {
				try {
					int actualNumberOfPages = getNumberOfPages(actual);

					if (actualNumberOfPages == numberOfPages) {
						return true;
					}

					mismatchDescription.appendText("has ")
						.appendValue(actualNumberOfPages)
						.appendText(" pages");

					return false;
				} catch (IOException e) {
					mismatchDescription.appendText("could not read pages due to ").appendText(e.getMessage());

					return false;
				}
			}

			@Override
			public void describeTo(@NonNull Description description) {
				description.appendText("A file with  ")
					.appendValue(numberOfPages)
					.appendText(" pages");
			}
		};
	}

	@SafeVarargs
	@NonNull
	public static Matcher<File> containsFonts(@NonNull Matcher<String>... fontNames) {
		Matcher<Iterable<? extends String>> iterableMatcher = Matchers.containsInAnyOrder(fontNames);

		return new TypeSafeDiagnosingMatcher<File>() {
			@Override
			protected boolean matchesSafely(@NonNull File actual, @NonNull Description mismatchDescription) {
				Set<String> fonts = getFonts(actual).stream()
					.map(PDFontLike::getName)
					.collect(Collectors.toSet());

				if (iterableMatcher.matches(fonts)) {
					return true;
				}

				iterableMatcher.describeMismatch(fonts, mismatchDescription);

				return false;
			}

			@Override
			public void describeTo(@NonNull Description description) {
				iterableMatcher.describeTo(description);
			}
		};
	}

	@NonNull
	public static Set<PDFont> getFonts(@NonNull File file) {
		try (PDDocument document = PDDocument.load(file)) {
			Set<PDFont> fonts = stream(document.getPages().iterator())
				.map(PDPage::getResources)
				.flatMap(r -> stream(r.getFontNames())
					.map(c -> {
						try {
							return r.getFont(c);
						} catch (IOException e) {
							throw new IllegalStateException("Failed to get Font " + c, e);
						}
					}))
				.collect(Collectors.toSet());

			return fonts;
		} catch (IOException ex) {
			throw new IllegalStateException("Could not load PDF " + file, ex);
		}
	}

	@NonNull
	private static <T> Stream<T> stream(@NonNull final Iterator<T> iterator) {
		return stream(() -> iterator);
	}

	@NonNull
	private static <T> Stream<T> stream(@NonNull Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
