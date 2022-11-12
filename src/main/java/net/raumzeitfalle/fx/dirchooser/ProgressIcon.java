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
package net.raumzeitfalle.fx.dirchooser;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

class ProgressIcon extends AnchorPane {

    private ProgressBar progressBar;

    private Button cancel;

    /*
     * TODO: Decorate the progress counter with an [X] to allow the user to cancel
     * an operation.
     * 
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

        setMinWidth(iconSize * 1.5);

        progressBar.getStyleClass().add("directory-progress-icon");
        getStyleClass().add("directory-icon-pane");

    }

}
