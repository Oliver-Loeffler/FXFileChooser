package net.raumzeitfalle.fx;


import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.SimplePathFilter;
import net.raumzeitfalle.fx.filechooser.SwingFileChooser;

public class DemoJavaSwingIntegration implements WindowListener {
  
    public static void main(String[] args) {
    		DemoJavaSwingIntegration app = new DemoJavaSwingIntegration();
        SwingUtilities.invokeLater(()->app.initAndShowGui());
    }

    private final List<PathFilter> filter;
    private SwingFileChooser fileChooser;
    
    DemoJavaSwingIntegration() {
    		PathFilter all = SimplePathFilter.acceptAll("all files");
    	 	PathFilter xml = SimplePathFilter.forFileExtension("eXtensible Markup Language (xml)", "xml");
    	    PathFilter txt = SimplePathFilter.forFileExtension("TXT", "txt");
    	    PathFilter pdf = SimplePathFilter.forFileExtension("PDF: Portable Document Format", "pdf");
    	    PathFilter png = SimplePathFilter.forFileExtension("*.png", "png");
    	    PathFilter svg = SimplePathFilter.forFileExtension("Scalable Vector Graphics (*.svg)", "svg");
    	    PathFilter html = SimplePathFilter.forFileExtension("*.html", "html").combine(SimplePathFilter.forFileExtension("*.htm", "html"));
    	    PathFilter xls = SimplePathFilter.forFileExtension("*.xls", "xls").combine(SimplePathFilter.forFileExtension("*.xlsx", "xlsx"));
    	    
    	    PathFilter htmlAndExcel = html.combine(xls).combine(png);
    	    
    	    filter = Arrays.asList(all,xml,txt,pdf,png,svg,html,xls,htmlAndExcel);

    }
    
    private void initAndShowGui() {
        JFrame frame = new JFrame("JavaFX Dialog in Swing");
        JPanel buttonHolder = new JPanel();
        
	    JButton showDialog = new JButton("Show JavaFX Stage as Dialog in Swing: SwingFileChooser.class");
	    this.fileChooser = SwingFileChooser.create("Choose any file:", this.filter.toArray(new PathFilter[0]));
	    
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

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {
		this.fileChooser.shutdown();
		
	}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
    
}
