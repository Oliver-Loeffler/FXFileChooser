# FXFileChooser

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![javadoc](https://javadoc.io/badge2/net.raumzeitfalle.fx/filechooser/0.0.7/javadoc.svg)](https://javadoc.io/doc/net.raumzeitfalle.fx/filechooser/0.0.7) [![Build Status](https://travis-ci.org/Oliver-Loeffler/FXFileChooser.svg?branch=master)](https://travis-ci.org/Oliver-Loeffler/FXFileChooser) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=net.raumzeitfalle.fx%3Afilechooser&metric=alert_status)](https://sonarcloud.io/dashboard?id=net.raumzeitfalle.fx%3Afilechooser) [![codecov](https://codecov.io/gh/Oliver-Loeffler/FXFileChooser/branch/master/graph/badge.svg)](https://codecov.io/gh/Oliver-Loeffler/FXFileChooser) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.raumzeitfalle.fx/filechooser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.raumzeitfalle.fx/filechooser)

Custom JavaFX file chooser which allows quick manual filtering, which allows to add Path predicates as filter and which is testable using TestFX.

As the standard JavaFX file chooser uses system dialogs, so it is hard to test and it is not easy to modifiy (e. g. new Skin). In some cases the system controls even show poor performance opening folders with many files (depends on operating system and JRE version).

On Microsoft Windows platforms running with Java 8, I've encountered cases where it was impossible to use the Java Swing JFileChooser, simply due to the high number of files in a directory. Using the JavaFX FileChooser was also not an option as I required a simplistic way to filter the files by name.

## Licensing and attributions

See `LICENSE` and `NOTICE` for details. The project is licensed using the Apache License, Version 2.0 
and attributes to FontAwesome Free 5.01 (Font Awesome Free License and CC BY 4.0 license).

## Adding FXFileChooser to your Maven or Gradle project

### Dependency for Maven `POM.xml`

```xml
<dependency>
  <groupId>net.raumzeitfalle.fx</groupId>
  <artifactId>filechooser</artifactId>
  <version>0.0.6</version>
</dependency>
```
### Dependency for Gradle `build.gradle`

```Groovy
implementation 'net.raumzeitfalle.fx:filechooser:0.0.8'
```


## How it works

FXFileChooser provides access to a DirectoryChooser and a ListView populated with files in the selected directory. The process starts in the users home directory. The ListView is populated by a background service running an update task upon request. 

Once the ListView is populated with Path items, those are filtered by the String entered in the filter TextField. The filter condition is "contains" whereas special characters such as ` '"','?','<','>','|',':','*'` are removed.

It turned out that with slow network connections the experience is great when using a single stream and updating the ListView in the streams forEach method. However, this can still take some seconds.

**Ideas**
 1. Provide some kind of directory content pre fetching for large network shares. 
 2. Indicate update progress and update ListView with one operation (one single update works fine, in case of using pre fetching that would be okay). Otherwise one would see an empty List and would have to wait.
 3. In the above case, ensure that files which are selected but do not exist are removed from view on selection or hover OR dont accept the OK action in case the file does no longer exist and trigger update then.  
 3. Update the view in one step (one single update works fine, in case of using pre fetching that would be okay). But then also update only what has changed. Never clear the list, only remove items which do no longer exist and add items which are not in the view.
 4. Keep the selection (if file still exists after update)


## Available versions

 * FileChooser placed in a customized JavaFX stage
 * One placed in a JavaFX dialog
 * One placed in a JFXPanel so it can be used in Java Swing applications.


## Features & Ideas
 
 * FXFileChooser is based on FXML and CSS and so fully customizable (the ListView might be replaced by a TableView to have more options in terms of sorting - or it will be completely exchangeable - I'll see)
 * Icons are realized as SVGPaths based upon FontAwesome Free 5 (no glyphs, no extra dependencies, the SVGPaths are part of the FXML)
 * The choose directory button provides a menu, where default locations (or a history of locations) can be provided. **(tbd.)**
 *  File types can be selected from filters **(tbd.)**
 
 ![default locations](pages/DefaultLocationsExample.png) ![path filter](pages/PathFilterExample.png) ![search option](pages/SortingMenuExample.png)
 * add sorting by name or time
 * consider using a TableView instead a plain list
 * consider regular expression support for filtering

**Ideas**
 * A nice build script.
 * A great skin (CSS) is desirable (well I just got inspired by https://github.com/angelicalleite/museuid and will see :-).


Tests are missing and currently I'm playing with TestFX - but it's not yet working as I like it.


## Using the FileChooser with Swing

```java
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
```

![Swing version with Filter](pages/OSX_Swing_JFXPanel.png)


## Using the JavaFX Dialog version

```java
public class FxDialogDemo extends Application  {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("Show File Chooser Dialog");
        FXFileChooserDialog dialog = FXFileChooserDialog.create(Skin.DARK);
        button.setOnAction(evt-> dialog.showOpenDialog(primaryStage).ifPresent(this::showSelection));

        Scene scene = new Scene(button);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }

    private void showSelection(Path selectedPath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("File Selection");
        alert.setContentText(selectedPath.toString());
        alert.show();
    }
}
```


![Swing version with Filter](pages/OSX_JavaFX_Dialog.png)


## A version with a completely customizable stage

```java
public class FxStageDemo extends Application  {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("Show File Chooser Stage");
        FXFileChooserStage fc = FXFileChooserStage.create(Skin.DARK);
        button.setOnAction(evt-> fc.showOpenDialog(primaryStage).ifPresent(this::showSelection));

        Scene scene = new Scene(button);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }

    private void showSelection(Path selectedPath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("File Selection");
        alert.setContentText(selectedPath.toString());
        alert.show();
    }
}
```

