package net.raumzeitfalle.fx;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.SwingFileChooser;

public class DemoJavaSwingIntegration  {
  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->initAndShowGui());
    }

    private static void initAndShowGui() {
        JFrame frame = new JFrame("JavaFX Dialog in Swing");
        JPanel buttonHolder = new JPanel();
        
    JButton showDialog = new JButton("Show JavaFX Stage as Dialog in Swing: SwingFileChooser.class");
    
    PathFilter xml = PathFilter.create(".xml", p->p.getFileName().toString().toLowerCase().endsWith(".xml"));
    PathFilter txt = PathFilter.create(".txt", p->p.getFileName().toString().toLowerCase().endsWith(".txt"));
    
    PathFilter xlsx = PathFilter.create(".xls or .xlsx", p-> p.getFileName().toString().toLowerCase().endsWith(".xls") 
    		|| p.getFileName().toString().toLowerCase().endsWith(".xlsx"));
    
    PathFilter na0 = PathFilter.forFileExtension(".na0 (LMS binary files)", "n[a-z]\\d");
     
    SwingFileChooser fileChooser = SwingFileChooser.create(xml, xlsx, na0, txt);
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
        frame.setSize(800, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
