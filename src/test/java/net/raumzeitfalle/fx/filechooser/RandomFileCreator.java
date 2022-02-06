/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.fx.filechooser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public final class RandomFileCreator {

    public static void main(String[] args){
        Path start = Paths.get("/Volumes/Daten/Temp/ManyFiles/");
        try {
			Files.createDirectories(start);
		} catch (IOException e) {
			/* just ignore what happens here */
		}
        RandomFileCreator rfc = new RandomFileCreator(start);
        rfc.run(1_046_446);
    }

    private final Path root;

    private final String[] sillables;

    private final String[] extensions;

    private final String[] separators;

    private final Random random;

    public RandomFileCreator(Path startIn){
        this.root = startIn;
        this.sillables = getSillables();
        this.extensions = getExtensions();
        this.separators = getSeparators();
        this.random = new Random();
    }

    private String[] getSeparators() {
        return new String[] {
                ".","-",""
        };
    }

    private String[] getExtensions() {
        return new String[]{
                "kms", "lms", "job", "sf3", ",sf4", "md5", "crc", "xls", "xlsx",
                "xlsm", "docx", "doc", "docm", "html", "htm", "css", "sass",
                "txt", "na0", "na1", "na2", "na3", "nb1", "jpeg", "jpg", "bmp",
                "tiff", "jasc", "mdp", "pdf", "java", "cpp", "h", "exe", "com",
                "md", "adoc", "xml", "py", "xhtml", "groovy", "gradle","properties",
                "config","cfg","prefs", "url", "rdp", "rest", "fxml", "settings", "kt",
                "js", "ts", "go", "sphinx", "thumbdb" 
        };
    }

    private String getRandomExt() {
        return this.extensions[this.random.nextInt(this.extensions.length-1)];
    }

    private String[] getSillables() {
        return new String[]{
            "metro", "device", "layer", "customer", "special", "auto", "line",
            "Space", "clear", "dark", "yellow", "green", "orange", "duv", "iline",
            "ltem", "bim", "report", "statistics", "documentation", "architecture",
            "diagrams", "shares", "earning", "returns", "complaints", "worksheet",
            "workbook", "table", "workdocument", "calculation", "summary", "index", 
            "letter", "templates", "toc", "overview", "projects", "BuildFile", "make",
            "draft", "final", "collection", "archive", "gallery", "mappings"
        };
    }

    private String createRandomFileName() {
        List<String> baseElements = Arrays.stream(this.sillables).collect(Collectors.toList());
        int randomElements = this.random.nextInt(6);
        int numberOfElement = randomElements == 0 ? 1 : randomElements;
        int separator = this.random.nextInt(this.separators.length-1);

        StringBuilder nameBuilder = new StringBuilder();
        for (int x = numberOfElement; x > 0; x--) {
            String y = baseElements.get(this.random.nextInt(baseElements.size()-1));
            baseElements.remove(x);
            nameBuilder.append(y);
            if (x > 1) {
                nameBuilder.append(this.separators[separator]);
            }
        }
        return nameBuilder.toString();
    };

    public void run(long max) {
    	long createdFiles = 0;
    	long divider = 1000;
    	if (max <= 1000)
    		divider = 100;
    	if (max <= 500)
    		divider = 50;
    	if (max <= 100)
    		divider = 10;
    	if (max >= 100_000)
    		divider = 10_000;
    	if (max >= 500_000)
    		divider = 50_000;
    	if (max >= 1_000_000)
    		divider = 100_000;
    	while (createdFiles < max) {
    		String newFile = createRandomFileName() + "." + getRandomExt();
    		Path toWrite = root.resolve(newFile);
    		if (Files.notExists(toWrite)) {        		
    			writeFile(toWrite, newFile);
    			createdFiles += 1;
    		}    		
    		if (createdFiles % divider == 0) {
    			System.out.println(createdFiles);
    		}
    	}
    }

    private void writeFile(Path fileToWrite, String fileName) {
        try {
            Files.write(fileToWrite,fileName.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not write " + root.resolve(fileName).toAbsolutePath());
        }
    }
}
