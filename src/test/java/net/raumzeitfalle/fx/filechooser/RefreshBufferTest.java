/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RefreshBufferTest {

    @ParameterizedTest
    @CsvSource({
        "1,        10",
        "5,        10",
        "100,      10",
        "1001,     20",
        "5000,     20",
        "5001,     50",
        "15000,    50",
        "15001,   100",
        "50000,   100",
        "50001,   200",
        "100000,  200",
        "100001,  500",
        "600001, 1000",})
    void cacheSize(Integer items, Integer expectedBufferSize) {
        int bufferSize = RefreshBuffer.determineBufferSize(items);
        assertEquals(bufferSize, expectedBufferSize);
    }
}
