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
    
    PathFilter xml = PathFilter.create(".xml", p->p.getName().toString().toLowerCase().endsWith(".xml"));
    PathFilter txt = PathFilter.create(".txt", p->p.getName().toString().toLowerCase().endsWith(".txt"));
    PathFilter pdf = PathFilter.create(".pdf", p->p.getName().toString().toLowerCase().endsWith(".pdf"));
    PathFilter png = PathFilter.create(".png", p->p.getName().toString().toLowerCase().endsWith(".png"));
    PathFilter svg = PathFilter.create(".svg", p->p.getName().toString().toLowerCase().endsWith(".svg"));
    PathFilter html = PathFilter.create(".html", p->{
    		String name = p.getName().toString().toLowerCase();
    		return name.endsWith(".html") || name.endsWith(".htm");
    });
    
    PathFilter xlsx = PathFilter.create(".xls or .xlsx", p-> p.getName().toString().toLowerCase().endsWith(".xls") 
    		|| p.getName().toString().toLowerCase().endsWith(".xlsx"));
    
        
    SwingFileChooser fileChooser = SwingFileChooser.create("Choose any file:", xml, xlsx, txt, pdf, png, svg, html);
    showDialog.addActionListener(l -> {
        int option = fileChooser.showOpenDialog(frame);
        System.out.println(option);
        
        if (option == SwingFileChooser.APPROVE_OPTION) {
            System.out.println(fileChooser.getSelectedFile());
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
