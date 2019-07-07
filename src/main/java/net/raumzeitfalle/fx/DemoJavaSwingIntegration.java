package net.raumzeitfalle.fx;

import java.awt.GridLayout;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.Skin;
import net.raumzeitfalle.fx.filechooser.StandardDirectoryChooser;
import net.raumzeitfalle.fx.filechooser.StandardFileChooser;
import net.raumzeitfalle.fx.filechooser.SwingFileChooser;

public class DemoJavaSwingIntegration {

	public static void main(String[] args) {
		DemoJavaSwingIntegration app = new DemoJavaSwingIntegration();
		SwingUtilities.invokeLater(app::initAndShowGui);
	}

	private final Logger logger;

	private final JPanel buttonHolder;

	DemoJavaSwingIntegration() {
		this.logger = Logger.getLogger(DemoJavaFxStage.class.getSimpleName());
		this.buttonHolder = createButtonHolder();
	}

	private List<PathFilter> collectPathFilter() {
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

		return Arrays.asList(all, xml, txt, pdf, png, svg, html, xls, htmlAndExcel);
	}

	private void initAndShowGui() {
		JFrame frame = new JFrame("JavaFX in Swing");

		// JavaFX stage placed inside a JDialog - default skin
		Example stageInsideSwingDialog = new Example() {
			{
				String buttonLabel = "<html><center><h3>JavaFX Stage inside Swing JDialog</h3>"
						+ SwingFileChooser.class.getName() + "<br>"
						+ "<font color=#0000FF>(Default Skin)</font>" + "</center></html>";
				
				button = new JButton(buttonLabel);
				PathFilter[] filter = collectPathFilter().toArray(new PathFilter[0]);
				SwingFileChooser fc = SwingFileChooser.create(Skin.DEFAULT, "Choose any file:", "TestData\\SomeFiles",
						filter);
				label = new JLabel("");

				button.addActionListener(l -> {
					int option = fc.showOpenDialog(frame);
					logger.log(Level.INFO, "Dialog return value: " + option);

					if (option == SwingFileChooser.APPROVE_OPTION) {
						SwingUtilities.invokeLater(
								() -> label.setText(String.valueOf(fc.getSelectedFile())));
					}
				});
			}
		};

		addExample(stageInsideSwingDialog);

		// JavaFX stage placed inside a JDialog - dark skin
		Example stageInsideSwingDialogDark = new Example() {
			{
				String buttonLabel = "<html><center><h3>JavaFX Stage inside Swing JDialog</h3>"
						+ SwingFileChooser.class.getName() + "<br>"
						+ "<font color=#0000FF>(Dark Skin)</font>" + "</center></html>";
				
				button = new JButton(buttonLabel);
				PathFilter[] filter = collectPathFilter().toArray(new PathFilter[0]);
				SwingFileChooser fc = SwingFileChooser.create(Skin.DARK, "Choose any file:", "TestData\\SomeFiles",
						filter);
				label = new JLabel("");

				button.addActionListener(l -> {
					int option = fc.showOpenDialog(frame);
					logger.log(Level.INFO, "Dialog return value: " + option);

					if (option == SwingFileChooser.APPROVE_OPTION) {
						SwingUtilities.invokeLater(
								() -> label.setText(String.valueOf(fc.getSelectedFile())));
					}
				});
			}
		};

		addExample(stageInsideSwingDialogDark);

		// JavaFX file chooser (usually the system file chooser) launched from Swing
		Example standardFileChooserOpen = new Example() {
			{
				String buttonLabel = "<html><center><h3>JavaFX FileChooser <font color=#ff0000>(Open)</font></h3>"
						+ FileChooser.class.getName() + "</center></html>";
				
				button = new JButton(buttonLabel);
				label = new JLabel("");
				StandardFileChooser stdFc = new StandardFileChooser();

				button.addActionListener(l -> {
					int option = stdFc.showOpenDialog(frame);
					if (option == SwingFileChooser.APPROVE_OPTION) {
						SwingUtilities.invokeLater(() -> {
							File file = stdFc.getSelectedFile();
							String text = (null == file) ? "" : String.valueOf(file);
							label.setText(text);
						});
					}
				});
			}
		};

		addExample(standardFileChooserOpen);

		// JavaFX file chooser (usually the system file chooser) launched from Swing
		Example standardFileChooserSave = new Example() {
			{
				
				String buttonLabel = "<html><center><h3>JavaFX FileChooser <font color=#FF0000>(Save)</font></h3>"
						+ FileChooser.class.getName() + "</center></html>";
				
				button = new JButton(buttonLabel);
				
				label = new JLabel("");
				StandardFileChooser stdFc = new StandardFileChooser();

				button.addActionListener(l -> {
					int option = stdFc.showSaveDialog(frame);
					if (option == SwingFileChooser.APPROVE_OPTION) {
						SwingUtilities.invokeLater(() -> {
							File file = stdFc.getSelectedFile();
							String text = (null == file) ? "" : String.valueOf(file);
							label.setText(text);
						});
					}
				});
			}
		};

		addExample(standardFileChooserSave);

		// JavaFX standard directory chooser
		Example standardDirectoryChooser = new Example() {
			{
				String buttonLabel = "<html><center><h3>JavaFX DirectoryChooser</h3>"
						+ DirectoryChooser.class.getName() + "<br>"
						+ "(Open Directory File)" + "</center></html>";
				
				button = new JButton(buttonLabel);
				label = new JLabel("");
				StandardDirectoryChooser stdDirChooser = new StandardDirectoryChooser();

				button.addActionListener(l -> {
					int option = stdDirChooser.showDialog(frame);
					if (option == SwingFileChooser.APPROVE_OPTION) {
						SwingUtilities.invokeLater(() -> {
							File file = stdDirChooser.getSelectedFile();
							String text = (null == file) ? "" : String.valueOf(file);
							label.setText(text);
						});
					}
				});
			}
		};

		addExample(standardDirectoryChooser);

		frame.getContentPane().add(buttonHolder);
		frame.pack();
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JPanel createButtonHolder() {
		GridLayout gridLayout = new GridLayout(6, 3);
		gridLayout.setHgap(20);
		gridLayout.setVgap(10);

		JPanel buttonHolder = new JPanel(gridLayout);
		return buttonHolder;
	}

	private void addExample(Example example) {
		this.buttonHolder.add(example.getButton());
		this.buttonHolder.add(example.getLabel());
	}

	private abstract class Example {

		protected JButton button;
		protected JLabel label;

		JButton getButton() {
			return button;
		}

		JLabel getLabel() {
			return label;
		}
	}
}
