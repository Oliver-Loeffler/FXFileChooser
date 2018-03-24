package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

public class FindFilesTaskTest {

    private JFXPanel javaFXPanel;
    
    private ObservableList<Path> listOfPaths;
    
    private final Path searchLocation = Paths.get("TestData/SomeFiles");
    
    private static final Path noFilesHere = Paths.get("TestData/NoFilesHere");
    
    @BeforeAll
    public static void prepare() throws IOException {
        Files.createDirectories(noFilesHere);
    }
    
    @AfterAll
    public static void cleanup() throws IOException {
        Files.delete(noFilesHere);
    }
    
    @BeforeEach
    public void initUiToolkit() {
        this.javaFXPanel = new JFXPanel();
        this.listOfPaths = FXCollections.observableArrayList();
        ListView<Path> listView = new ListView<>(listOfPaths);
        Scene scene = new Scene(listView);
        this.javaFXPanel.setScene(scene);
    }
    

    @Test
    public void test() throws Exception {      
        
        FindFilesTask task = new FindFilesTask(searchLocation, listOfPaths);
        Object result = runAndWait(task);
               
        assertNull(result);
        assertEquals(11, this.listOfPaths.size());
        
        Set<String> fileNames = this.listOfPaths.stream()
                .map(Path::getFileName)
                .map(String::valueOf)
                .collect(Collectors.toSet());
        
        assertTrue(fileNames.contains("HorrbibleSpreadSheet.xls"));
        assertTrue(fileNames.contains("JustNumbers.csv"));
        assertTrue(fileNames.contains("NewerDocument.docx"));
        assertTrue(fileNames.contains("OldDocument.doc"));
        assertTrue(fileNames.contains("SupposedToBeXtensible.xml"));
        assertTrue(fileNames.contains("TestFile1.txt"));
        assertTrue(fileNames.contains("TestFile2.txt"));
        assertTrue(fileNames.contains("TestFile3.txt"));
        assertTrue(fileNames.contains("TestFile4.txt"));
        assertTrue(fileNames.contains("TestFile5.txt"));
        assertTrue(fileNames.contains("XtremeHorrbibleSpreadSheet.xlsx"));
        
    }

    private <T> Object runAndWait(Task<T> task) throws InterruptedException, ExecutionException {
        return Executors.newSingleThreadExecutor().submit(task).get();
    }

}
