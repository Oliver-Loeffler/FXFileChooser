package net.raumzeitfalle.fx.filechooser;

import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SwingFileChooser extends JFXPanel implements ClosableStage {

    private static final long serialVersionUID = -5879082370711306802L;
    
    /**
     * Return value if cancel is chosen.
     */
    public static final int CANCEL_OPTION = 1;

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    public static final int APPROVE_OPTION = 0;

    
    public static SwingFileChooser create(JFrame frame)  {
        SwingFileChooser fc = new SwingFileChooser(frame);
        Platform.runLater(()->{
            try {
                FileChooserModel model = new FileChooserModel();
                Parent view = FileChooserView.create(model, fc);
                Scene scene = new Scene(view);
                fc.setScene(scene);
                fc.setModel(model);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }    
        });
        
        return fc;
    }

    private FileChooserModel model;
    
    private JDialog dialog;
    
    private JFrame parent;
       
    private SwingFileChooser(JFrame parent){
        this.parent = parent;
    }
    
    private void setModel(FileChooserModel model) {
        this.model = model;
    }
    
    public int showOpenDialog() {
        if (null == this.dialog) {
            dialog = new JDialog(parent, "Choose File", true);
            dialog.setContentPane(this);
            dialog.pack();
        }
        this.dialog.setVisible(true);
        
        if (this.model.invalidSelectionProperty().getValue()) {
            return CANCEL_OPTION;
        } else {
            return APPROVE_OPTION;
        }
    }

    public File getSelectedFile() {
        if (this.model.getSelectedFile() != null) {
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
