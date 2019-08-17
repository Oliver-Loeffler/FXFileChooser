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

import java.io.File;

import javafx.scene.Node;
import javafx.stage.FileChooser;

public class SwingNodeFileChooser {

	private final FileChooser dialog;
	
	private final FileSystemDialogAdapter<FileChooser, Node, File> wrapper;
	
	public SwingNodeFileChooser() {
		this.dialog = new FileChooser();
		this.wrapper = new FileSystemDialogAdapter<>(dialog,
				(FileChooser chooser, Node node)->chooser.showOpenDialog(node.getScene().getWindow()));
	}
	
	public int showOpenDialog(Node ownerNode) {
		return this.wrapper.runDialog(ownerNode);
	}
	
	public File getSelectedFile() {
		return this.wrapper.getResult();
	}
	
}
