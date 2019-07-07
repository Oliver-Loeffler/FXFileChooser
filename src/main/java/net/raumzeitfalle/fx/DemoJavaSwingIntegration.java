package net.raumzeitfalle.fx;

import java.awt.GridLayout;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JFileChooser;
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

	private final JPanel buttonHolder;

	private DemoJavaSwingIntegration() {
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
		
		PathFilter[] filter = collectPathFilter().toArray(new PathFilter[0]);
		SwingFileChooser fc = SwingFileChooser.create(Skin.DEFAULT, "Choose any file:", "TestData\\SomeFiles",filter);
		String title =  "<html><center><h3>JavaFX Stage inside Swing JDialog</h3>"
				+ SwingFileChooser.class.getName() + "<br>" + "<font color=#0000FF>(Default Skin)</font>"
				+ "</center></html>";
		
		Example stageInsideSwingDialog = new Example(title,
				() -> fc.showOpenDialog(frame),
				() -> fc.getSelectedFile());
		
		addExample(stageInsideSwingDialog);

		
		// JavaFX stage placed inside a JDialog - dark skin
		title = "<html><center><h3>JavaFX Stage inside Swing JDialog</h3>"
				+ SwingFileChooser.class.getName() + "<br>" + "<font color=#0000FF>(Dark Skin)</font>"
				+ "</center></html>";
		SwingFileChooser darkFc = SwingFileChooser.create(Skin.DARK, "Choose any file:", "TestData\\SomeFiles",filter);

		Example stageInsideSwingDialogDark = new Example(title,
				() -> darkFc.showOpenDialog(frame),
				() -> darkFc.getSelectedFile());
		
		addExample(stageInsideSwingDialogDark);

		// JavaFX file chooser (usually the system file chooser) launched from Swing
		title = "<html><center><h3>JavaFX FileChooser <font color=#ff0000>(Open)</font></h3>"
				+ FileChooser.class.getName() + "</center></html>";
		StandardFileChooser stdFc = new StandardFileChooser();

		Example standardFileChooserOpen = new Example(title,
				() -> stdFc.showOpenDialog(frame),
				() -> stdFc.getSelectedFile());
		
		addExample(standardFileChooserOpen);

		// JavaFX file chooser (usually the system file chooser) launched from Swing
		title = "<html><center><h3>JavaFX FileChooser <font color=#ff0000>(Save)</font></h3>"
				+ FileChooser.class.getName() + "</center></html>";
		StandardFileChooser stdFcSave = new StandardFileChooser();
		Example standardFileChooserSave = new Example(title,
				() -> stdFcSave.showOpenDialog(frame),
				() -> stdFcSave.getSelectedFile());
		
		addExample(standardFileChooserSave);

		// JavaFX standard directory chooser
		title = "<html><center><h3>JavaFX DirectoryChooser <font color=#ff0000>(Open Directory)</font></h3>"
				+ DirectoryChooser.class.getName() + "</center></html>";
		StandardDirectoryChooser stdDirChooser = new StandardDirectoryChooser();
		Example standardDirectoryChooser = new Example(title,
				() -> stdDirChooser.showDialog(frame),
				() -> stdDirChooser.getSelectedFile());
		
		addExample(standardDirectoryChooser);

		frame.getContentPane().add(buttonHolder);
		frame.pack();
		frame.setSize(900, 700);
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

	private static class Example {

		protected String buttonLabel = "Activity";
		private JLabel label = new JLabel("");;
		protected Supplier<File> fileSource;
		protected Supplier<Integer> dialogInteraction;
		
		public Example(String title, Supplier<Integer> interaction, Supplier<File> fileSource) {
			this.buttonLabel = title;
			this.dialogInteraction = interaction;
			this.fileSource = fileSource;
		}
		
		JButton getButton() {
			JButton button = new JButton(buttonLabel);
			button.addActionListener(l -> {
				int option = dialogInteraction.get();
				if (option == JFileChooser.APPROVE_OPTION) {
					SwingUtilities.invokeLater(() -> {
						File file = fileSource.get();
						String text = (null == file) ? "" : String.valueOf(file);
						label.setText(text);
					});
				}
			});
			return button;
		}

		JLabel getLabel() {
			return label;
		}
	}
}
