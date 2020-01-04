/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.Comparator;

/**
 * Provides various {@link Comparator} variants to compare {@link Path} objects.
 */
class PathComparator {
	
	enum Option {
		ASCENDING,
		DESCENDING;
	}
	
	static Comparator<IndexedPath> ascendingByName() {
		return lexical(Option.ASCENDING);
	}

	static Comparator<IndexedPath> descendingByName() {
		return lexical(Option.DESCENDING);
	}

	private static int getOrder(Option option) {
		return option.equals(Option.ASCENDING) ? 1 : -1;
	}

	static Comparator<IndexedPath> lexical(Option option) {
		return (IndexedPath a, IndexedPath b)-> getOrder(option) * a.asPath().compareTo(b.asPath());
	}

	static Comparator<IndexedPath> descendingLastModified() {
		return byLastModified(Option.DESCENDING);
	}
	
	static Comparator<IndexedPath> ascendingLastModified() {
		return byLastModified(Option.ASCENDING);
	}
	
	static Comparator<IndexedPath> byLastModified(Option option) {
		return (IndexedPath a, IndexedPath b)-> getOrder(option) * a.getTimestamp().compareTo(b.getTimestamp());
	}
					
	private PathComparator() {
		// provides short cuts for commonly used comparators
	}

}
