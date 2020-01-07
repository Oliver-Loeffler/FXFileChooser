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

import java.awt.Window;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import javafx.stage.DirectoryChooser;

public class StandardDirectoryChooser {
	    
    /**
     * The wrapped system dialog from JavaFX.
     *  
     */
	private final DirectoryChooser dialog;

	private final FileSystemDialogAdapter<DirectoryChooser, Window, File> adapter;
		
	public StandardDirectoryChooser() {
		this.dialog = new DirectoryChooser();
		this.adapter = new FileSystemDialogAdapter<>(dialog,
				(DirectoryChooser chooser, Window window)->chooser.showDialog(null));
		
		Consumer<Window> beforeOpen = window -> { 
			window.setFocusableWindowState(false);
			window.setEnabled(false);
		};
		
		Consumer<Window> afterClosing = window -> 		
			SwingUtilities.invokeLater(() -> {
				window.setEnabled(true);
				window.setFocusableWindowState(true);
				window.toFront();
			});
		
		this.adapter
		    .beforeOpenDialog(beforeOpen)
		    .afterClosingDialog(afterClosing);
	}
	
	public DirectoryChooser getDialog() {
		return dialog;
	}
	
	public int showDialog(Window window) {
		return this.adapter.runDialog(window);
	}
		
	public File getSelectedFile() {
		return this.adapter.getResult();
	}

}
