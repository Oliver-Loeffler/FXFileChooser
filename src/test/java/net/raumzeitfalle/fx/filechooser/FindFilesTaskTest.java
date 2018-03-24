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
import javafx.embed.swing.JFXPanel;

class FindFilesTaskTest {
    
    private JFXPanel panel;
    
    private ObservableList<Path> listOfPaths = FXCollections.observableArrayList();;
    
    private final Path searchLocation = Paths.get("TestData/SomeFiles");
    
    private static final Path NO_FILES_IN_HERE = Paths.get("TestData/NoFilesHere");
        
    @BeforeAll
    static void prepare() throws IOException {
        Files.createDirectories(NO_FILES_IN_HERE);
    }
    
    @AfterAll
    static void cleanup() throws IOException {
        Files.delete(NO_FILES_IN_HERE);
    }
    
    @BeforeEach
    void initUiToolkitOnDemand() {
        if (null == panel) {
            panel = new JFXPanel();    
        }
        listOfPaths.clear();
    }
    

    @Test
    void runningTheTask_inPopulatedFolder() throws Exception {      
        
        FindFilesTask task = new FindFilesTask(searchLocation, listOfPaths);
        runAndWait(task);
        
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
    
    @Test
    void runningTheTask_inEmptyFolder() throws Exception {      
        
        FindFilesTask task = new FindFilesTask(NO_FILES_IN_HERE, listOfPaths);
        runAndWait(task);
               
        assertEquals(0, this.listOfPaths.size());
    }

    private void runAndWait(FindFilesTask task) throws InterruptedException, ExecutionException {
        Executors.newSingleThreadExecutor().submit(task).get();
    }

}
