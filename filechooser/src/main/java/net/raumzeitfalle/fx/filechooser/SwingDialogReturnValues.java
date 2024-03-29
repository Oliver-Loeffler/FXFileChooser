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

import javax.swing.JFileChooser;

public enum SwingDialogReturnValues {

    /**
     * Return value if cancel is chosen.
     */
    CANCEL_OPTION(JFileChooser.CANCEL_OPTION),

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    APPROVE_OPTION(JFileChooser.APPROVE_OPTION),

    /**
     * Return value if an error occurred.
     */
    ERROR_OPTION(JFileChooser.ERROR_OPTION);

    private final int returnValue;

    private SwingDialogReturnValues(int returnValue) {
        this.returnValue = returnValue;
    }

    public int getValue() {
        return this.returnValue;
    }

}
