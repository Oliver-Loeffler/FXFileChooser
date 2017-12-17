# SearchableFileChooser (0.0.1)

As the standard JavaFX file chooser uses system dialogs, it is hard to test, it is hard to modifiy.
In some cases eve its browsing performance is poor (depends on operating system and JRE version).

On Microsoft Windows platforms running with Java 8, I've encountered cases where it was impossible to use the Java Swing JFileChooser simply due to the high number of files in a directory. Using the JavaFX FileChooser was also not an option as it did not provide a good way of quick filtering.


## How it works

Instead, the SearchableFileChooser provides access to a DirectoryChooser and a ListView populated with files in the selected directory. The process starts in the users home directory. The ListView is populated by a background service running an update task upon request.

Once the ListView is populated with Path items, those are filtered by the String entered in the filter TextField. The filter condition is "contains" whereas special characters such as '"','?','<','>','|',':','*' are removed.


## Available versions

 * FileChooser placed in a customized JavaFX stage
 * One placed in a JavaFX dialog
 * One placed in a JFXPanel so it can be used in Java Swing applications.


## Features & Ideas
 
 * The FileChooser is based on FXML and CSS and so fully customizable (the ListView might be replaced by a TableView to have more options in terms of sorting - or it will be completely exchangeable - I'll see)
 * Icons are realized as SVGPaths based upon FontAwesome Free 5 (no glyphs, no extra dependencies, the SVGPaths are part of the FXML)
 * The choose directory button provides a menu, where default locations (or a history of locations) can be provided.
 *  File types can be selected from filters
 
 ![default locations](pages/DefaultLocationsExample.png) ![default locations](pages/PathFilterExample.png)

Both items are not yet functional as the API does not yet provide functions to customize them. This is the next step (after adding all tests to Github).

**Ideas**
 * A nice build script.
 * A great skin (CSS) is desirable (well I just got inspired by https://github.com/angelicalleite/museuid and will see :-).


## Using the FileChooser with Swing

```java
    JButton showDialog = new JButton("Show Dialog");
    SwingFileChooser fileChooser = SwingFileChooser.create(frame);
    showDialog.addActionListener(l -> {
        int option = fileChooser.showOpenDialog();
        System.out.println(option);
        if (option == SwingFileChooser.APPROVE_OPTION) {
            System.out.println(fileChooser.getSelectedFile().toString());
        }
    });
```

![Swing version with Filter](pages/Windows81_Swing_Dialog_Filtered.png)


## Using the JavaFX Dialog version

```java
    Button showDialog = new Button("Show Dialog");
    FXFileChooserDialog fc = FXFileChooserDialog.create();
    showDialog.setOnAction(a -> {
        try {
            Optional<Path> path = fc.showOpenDialog(primaryStage);
            System.out.println(path.map(String::valueOf).orElse("Nothing selected"));
        } catch (IOException e) {
            // don't mind 
        }
    });
```

![Swing version with Filter](pages/Windows81_JavaFX_DialogStage.png)


## Example with running process

In cases with many files, the background activity of listing all files is indicated. Aside a label shows the number of currently filtered files and total available files.

The activity is implemented as a Service so it can be cancelled.

```java 
final class FileUpdateService extends javafx.concurrent.Service<Void> {

    ObservableList<Path> pathsToUpdate
    
    @Override
    protected Task<Void> createTask() {
        return new FindFilesTask(rootFolder.getValue(), pathsToUpdate);
    }
}
```

The FindFilesTask so far only lists files matching the Predicate<Path> `Files::isRegularFile`.

![Swing version with Filter](pages/Windows81_Swing_Dialog_ProcessRunning.png)
