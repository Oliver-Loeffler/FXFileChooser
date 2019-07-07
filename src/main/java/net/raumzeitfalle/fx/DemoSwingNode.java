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
