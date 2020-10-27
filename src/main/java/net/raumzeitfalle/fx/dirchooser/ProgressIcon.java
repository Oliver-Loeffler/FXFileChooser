package net.raumzeitfalle.fx.dirchooser;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

class ProgressIcon extends AnchorPane {
		
	private ProgressBar progressBar;
	
	private Button cancel;
	
	/*
	 * TODO:
	 * Figure out how to offer the cancel action to the user.
	 */
	public ProgressIcon(double iconSize, javafx.event.EventHandler<ActionEvent> cancelHandler) {
	
		progressBar = new ProgressBar();
		progressBar.setProgress(-1);
		progressBar.setMaxSize(iconSize, iconSize);
		progressBar.setPrefSize(iconSize, iconSize);
		cancel = new Button("X");
		cancel.setOnAction(cancelHandler);
				
		getChildren().add(progressBar);
		
		AnchorPane.setLeftAnchor(progressBar, 0d);
		AnchorPane.setRightAnchor(progressBar, 0d);
		AnchorPane.setTopAnchor(progressBar, 0d);
		AnchorPane.setBottomAnchor(progressBar, 0d);
				
		setMinWidth(iconSize*1.5);
		
		progressBar.getStyleClass().add("directory-progress-icon");
	    getStyleClass().add("directory-icon-pane");
		
	}
	
}
