# SearchableFileChooser (0.0.1)

As the standard JavaFX file chooser uses system dialogs, it is hard to test, it is hard to modifiy.
In some cases eve its browsing performance is poor (depends on operating system and JRE version).



**There are 3 different versions available:**
 * FileChooser placed in a customized JavaFX stage
 * One placed in a JavaFX dialog
 * One placed in a JFXPanel so it can be used in Java Swing applications.


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

