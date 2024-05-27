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

package ch.dvbern.lib.invoicegenerator.dto.component;

import ch.dvbern.lib.invoicegenerator.dto.OnPage;
import org.jspecify.annotations.NonNull;

public class SimpleConfiguration implements ComponentConfiguration {

	@NonNull
	private OnPage onPage;

	public SimpleConfiguration(@NonNull OnPage onPage) {
		this.onPage = onPage;
	}

	@NonNull
	@Override
	public OnPage getOnPage() {
		return this.onPage;
	}

	@Override
	public void setOnPage(@NonNull OnPage onPage) {
		this.onPage = onPage;
	}
}
