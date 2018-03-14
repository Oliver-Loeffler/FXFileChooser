package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

enum FileIcons {
    
    CSV(".csv", "icons/file-csv.png"),
    DOC(".doc", "icons/file-doc.png"),
    DOCX(".docx", "icons/file-docx4.png"),
    UNKNOWN(null, "icons/file-unknown.png"),
    XLS(".xls", "icons/file-xls.png"),
    XLSX(".xlsx", "icons/file-xlsx4.png"),
    TXT(".txt", "icons/file-txt.png"),
    XML(".xml", "icons/file-xml.png");
        
    private final String suffix;
    
    private final String iconFileName;
    
    private FileIcons(String suffix, String iconFileName) {
        this.suffix = suffix;
        this.iconFileName = iconFileName;
    }
    
    
    private static ImageView create(String iconFileName, double fitSize) {
        String img = FileIcons.class.getResource(iconFileName).toExternalForm();
        ImageView image = new ImageView(img);
        image.preserveRatioProperty().set(true);
        image.setFitHeight(fitSize);
        return image;
    }

    static StackPane fromFile(Path path, double fitSize) {
       StackPane pane = new StackPane();
       Path file = path.getFileName();
       ImageView image = null;
       if (null != file) {
           String fileName = file.toString().toLowerCase();
           for (FileIcons icon : FileIcons.values()) {
               String suffix = icon.suffix;
               if (null != suffix) {
                   if (fileName.endsWith(suffix)) {       
                       image = create(icon.iconFileName,fitSize);
                   }
               }
           }    
       }
       if (null == image) {
           image = create(FileIcons.UNKNOWN.iconFileName,fitSize);
       }
       
       pane.getChildren().add(image);
       pane.setMinWidth(fitSize*1.5);
       image.getStyleClass().add("file-icon");
       pane.getStyleClass().add("file-icon-pane");
       
       return pane;
    }
}
