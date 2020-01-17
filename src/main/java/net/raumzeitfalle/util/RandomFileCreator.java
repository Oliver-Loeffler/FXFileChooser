package net.raumzeitfalle.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public final class RandomFileCreator {

    public static void main(String[] args){
        Path start = Paths.get("/Users/oliver/Desktop/Many Files/");
        RandomFileCreator rfc = new RandomFileCreator(start);
        rfc.run();
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
                "KMS", "LMS", "job", "sf3", "md5", "xlsx", "docx", "doc", "html",
                "txt", "na0", "na1", "na2", "na3", "nb1", "jpeg", "jpg", "bmp",
                "xls", "pdf", "java", "cpp", "h", "exe", "com", "md", "adoc", "xml",
                "groovy", "gradle","properties","config","cfg","prefs"
        };
    }

    private String getRandomExt() {
        return this.extensions[this.random.nextInt(this.extensions.length-1)];
    }

    private String[] getSillables() {
        return new String[]{
            "Metro", "Device", "Layer", "Customer", "Special", "Auto", "Line",
            "Space", "Clear", "Dark", "Yellow", "DUV", "iline", "LTEM", "BIM",
            "workdocument", "calculation", "summary", "index", "Letter", "TOC",
            "BuildFile"
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

    public void run() {
        for (long i = 0; i < 100_000;i++) {
            writeFile(createRandomFileName() + "." + getRandomExt());
        }
    }

    private void writeFile(String fileName) {
        try {
            Files.write(root.resolve(fileName),fileName.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not write " + root.resolve(fileName).toAbsolutePath());
        }
    }
}
