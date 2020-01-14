package net.raumzeitfalle.fx;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.Skin;
import net.raumzeitfalle.fx.filechooser.SwingFileChooser;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Optional;

public class SwingDemo {
    public static void main(String ... args) {

        JFrame frame = new JFrame("Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton showDialogButton = new JButton("show SwingFileChooser");
        frame.getContentPane().add(showDialogButton);

        SwingFileChooser fileChooser = SwingFileChooser.create(Skin.DARK);

        showDialogButton.addActionListener(l -> {
            int option = fileChooser.showOpenDialog(frame);
            if (option == SwingFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(frame, fileChooser.getSelectedFile().toString());
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}