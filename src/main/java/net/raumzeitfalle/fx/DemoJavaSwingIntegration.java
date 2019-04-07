package net.raumzeitfalle.fx;

import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.Skin;
import net.raumzeitfalle.fx.filechooser.SwingFileChooser;

public class DemoJavaSwingIntegration implements WindowListener {

	public static void main(String[] args) {
		DemoJavaSwingIntegration app = new DemoJavaSwingIntegration();
		SwingUtilities.invokeLater(app::initAndShowGui);
	}

	private final List<PathFilter> filter;
	private SwingFileChooser fileChooser;
	private final Logger logger = Logger.getLogger(DemoJavaFxStage.class.getSimpleName());

	DemoJavaSwingIntegration() {
		PathFilter all = PathFilter.acceptAllFiles("all files");
		PathFilter xml = PathFilter.forFileExtension("eXtensible Markup Language (xml)", "xml");
		PathFilter txt = PathFilter.forFileExtension("TXT", "txt");

		PathFilter pdf = PathFilter.forFileExtension("PDF: Portable Document Format", "pdf");
		PathFilter png = PathFilter.forFileExtension("*.png", "png");

		PathFilter svg = PathFilter.forFileExtension("Scalable Vector Graphics (*.svg)", "svg");

		PathFilter html = PathFilter.forFileExtension("*.html", "html")
				.combine(PathFilter.forFileExtension("*.htm", "html"));
		PathFilter xls = PathFilter.forFileExtension("*.xls", "xls")
				.combine(PathFilter.forFileExtension("*.xlsx", "xlsx"));

		PathFilter htmlAndExcel = html.combine(xls).combine(png);

		filter = Arrays.asList(all, xml, txt, pdf, png, svg, html, xls, htmlAndExcel);
	}

	private void initAndShowGui() {
		JFrame frame = new JFrame("JavaFX Dialog in Swing");
		JPanel buttonHolder = new JPanel(new FlowLayout());

		JButton showDialog = new JButton("Show JavaFX Stage as Dialog in Swing: SwingFileChooser.class");
		this.fileChooser = SwingFileChooser.create(Skin.DEFAULT, "Choose any file:",
				this.filter.toArray(new PathFilter[0]));
		JLabel chosenFile = new JLabel("placeholder for filename to be selected.");

		showDialog.addActionListener(l -> {
			int option = fileChooser.showOpenDialog(frame);
			logger.log(Level.INFO, "Dialog return value: " + option);

			if (option == SwingFileChooser.APPROVE_OPTION) {
				SwingUtilities.invokeLater(
						() -> chosenFile.setText("Selected file: " + fileChooser.getSelectedFile().toString()));
			}

		});

		buttonHolder.add(showDialog);
		buttonHolder.add(chosenFile);
		frame.getContentPane().add(buttonHolder);
		frame.pack();
		frame.setSize(800, 200);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// required by WindowListener but no need toadd something here
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// required by WindowListener but no need toadd something here
	}

	@Override
	public void windowClosed(WindowEvent e) {
		/*
		 * Gracefully shutdown the executor service which possibly
		 * runs indexing threads.
		 * 
		 */
		this.fileChooser.shutdown();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// required by WindowListener but no need toadd something here
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// required by WindowListener but no need toadd something here
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// required by WindowListener but no need toadd something here
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// required by WindowListener but no need toadd something here
	}

}
