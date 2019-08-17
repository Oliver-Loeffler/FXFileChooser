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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SwingFileChooser extends JFXPanel implements HideableWindow {

    private static final long serialVersionUID = -5879082370711306802L;
    
    /**
     * Return value if cancel is chosen.
     */
    public static final int CANCEL_OPTION = 1;

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    public static final int APPROVE_OPTION = 0;
    
    public static SwingFileChooser create(Skin skin,PathFilter ...filter) {
    		return create(skin,"Choose file:", "", filter);
    }
    
    public static SwingFileChooser create(Skin skin,String title, PathFilter ...filter) {
    		return create(skin,title, "", filter);
    }
       
    public static SwingFileChooser create(Skin skin,String title, String pathToBrowse, PathFilter ...filter)  {
        
        Path startHere = startPath(pathToBrowse);
        SwingFileChooser fc = new SwingFileChooser(title);
        PathSupplier pathSupplier = SwingDirectoryChooser.createIn(startHere, fc);
                
        Platform.runLater(()->{
            try {
                FileChooserModel model = FileChooserModel.startingIn(startHere);
                for (PathFilter f : filter) {
                	model.addOrRemoveFilter(f);
                }        
                Parent view = FileChooserView.create(model, pathSupplier, fc,skin);
                Scene scene = new Scene(view);
                fc.setScene(scene);
                fc.setModel(model);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }    
        });
        
        return fc;
    }

    private static Path startPath(String pathToBrowse) {
        Path startHere = Paths.get(pathToBrowse);
        if (pathToBrowse.equals("")) {
            startHere = Paths.get("./");
        }
        return startHere;
    }
    
    private FileChooserModel model;
    
    private JDialog dialog;   
    
    private final String title;
    
    private SwingFileChooser(String title) {
    		this.title = (null != title) ? title : "Choose file:";
    }
    
    private void setModel(FileChooserModel model) {
        this.model = model;
    }
    
    public int showOpenDialog(Component parent) {
        if (null == this.dialog) {
            Frame frame = JOptionPane.getFrameForComponent(parent);
            dialog = new JDialog(frame, this.title, true);
            dialog.setContentPane(this);
            Dimension size = new Dimension(750, 550);
            this.setPreferredSize(size);
            this.setMinimumSize(size);
            dialog.pack();
            dialog.setResizable(true);            
        }
       
        
        this.dialog.setVisible(true);
        if (this.model.invalidSelectionProperty().getValue()) {
            return CANCEL_OPTION;
        } else {
            return APPROVE_OPTION;
        }
    }

    public File getSelectedFile() {
        if (null != this.model.getSelectedFile()) {
            return this.model.getSelectedFile().toFile();    
        } else {
            return null;
        }
    }

    @Override
    public void hide() {
        this.dialog.setVisible(false);
    }
    
}
