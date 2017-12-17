package net.raumzeitfalle.fx;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.raumzeitfalle.fx.filechooser.SwingFileChooser;

public class DemoJavaSwingIntegration  {
  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->initAndShowGui());
    }

    private static void initAndShowGui() {
        JFrame frame = new JFrame("JavaFX Dialog in Swing");
        JPanel buttonHolder = new JPanel();
        
    JButton showDialog = new JButton("Show Dialog");
    SwingFileChooser fileChooser = SwingFileChooser.create(200_000);
    showDialog.addActionListener(l -> {
        int option = fileChooser.showOpenDialog(frame);
        System.out.println(option);
        
        if (option == SwingFileChooser.APPROVE_OPTION) {
            System.out.println(fileChooser.getSelectedFile().toString());
        }
        
    });
        
        buttonHolder.add(showDialog);
        frame.getContentPane().add(buttonHolder);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
