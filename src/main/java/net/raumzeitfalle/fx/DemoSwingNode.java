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
package net.raumzeitfalle.fx;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.SwingFileChooser;
import net.raumzeitfalle.fx.filechooser.SwingNodeFileChooser;

public class DemoSwingNode extends Application {

	public static void main(String[] args) {
		Application.launch(new String[0]);
	}

	@Override
	public void start(Stage stage) {
		final SwingNode swingNode = new SwingNode();

		createSwingContent(swingNode);

		StackPane pane = new StackPane();
		pane.getChildren().add(swingNode);

		stage.setTitle("Swing in JavaFX");
		stage.setScene(new Scene(pane, 250, 150));
		stage.show();
	}

	private void createSwingContent(final SwingNode swingNode) {
		SwingUtilities.invokeLater(() -> {

			SwingNodeFileChooser swingNodeFc = new SwingNodeFileChooser();
			JButton button = new JButton("Click me!");
			JLabel label = new JLabel("File Selection");
			JPanel buttonHolder = new JPanel(new FlowLayout());

			button.addActionListener(l -> {
				int option = swingNodeFc.showOpenDialog(swingNode);
				if (option == SwingFileChooser.APPROVE_OPTION) {
					SwingUtilities.invokeLater(() -> {
						File file = swingNodeFc.getSelectedFile();
						String text = (null == file) ? "" : String.valueOf(file);
						label.setText("Selected file: " + text);
					});
				}

			});

			buttonHolder.add(button);
			buttonHolder.add(label);

			swingNode.setContent(buttonHolder);

		});
	}

}
