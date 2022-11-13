/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2022 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.dirchooser;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;

class DirectoryWalker {

    private Path current;

    private int maxDepth;

    private int currentDepth;

    private DirectoryTreeItem rootNode;

//    private static Filter<Path> pathFilter = getPathFilter();

    public DirectoryWalker(Path start) {
        this(start, 0);
    }

    public DirectoryWalker(Path start, int maxDepth) {
        this.current = start;
        this.maxDepth = maxDepth;
        this.currentDepth = 0;
        this.rootNode = new DirectoryTreeItem(start);
    }

//    private DirectoryWalker(DirectoryWalker walker, Path subDir) {
//        this.current = walker.current.resolve(subDir);
//        this.maxDepth = walker.maxDepth;
//        this.currentDepth = walker.currentDepth + 1;
//        this.rootNode = new DirectoryTreeItem(current);
//    }

    DirectoryTreeItem read(ReadOnlyBooleanProperty cancelled) {
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (null == pathname) {
                    return false;
                }
                if (pathname.isDirectory()) {
                    return true;
                }
                return false;
            }
        };
        int size = 0;
        for (File f : current.toFile().listFiles(ff)) {
            addNode(f.toPath(), cancelled);
            size += 1;
        }
        this.rootNode.setSize(size);
//        try (DirectoryStream<Path> dirs = Files.newDirectoryStream(current, pathFilter)) {
//            for (Path path : dirs) {
//                if (cancelled.getValue())
//                    break;
//                addNode(path, cancelled);
//            }
//        } catch (IOException e) {
//            Logger.getLogger(DirectoryWalker.class.getName())
//                  .log(Level.WARNING, "error rading directory content", e);
//        }
        return this.rootNode;
    }

    private void addNode(Path path, ReadOnlyBooleanProperty cancelled) {
        if (currentDepth <= maxDepth) {
            if (cancelled.get())
                return;
            DirectoryTreeItem leaf = new DirectoryTreeItem(path);
//            DirectoryTreeItem leaf = new DirectoryWalker(this, path).read(cancelled);
            rootNode.getChildren().add(leaf);
            /*
             * TODO: Make the directory tree sortable 
             * TODO: Make the directory tree filterable
             * 
             */
            if (cancelled.get())
                return;

            FXCollections.sort(rootNode.getChildren(), 
                              (a, b) -> a.getValue().compareToIgnoreCase(b.getValue()));
        }
    }

//    private static Filter<Path> getPathFilter() {
//        return DirectoryWalker::isDirectory;
//    }

//    private static boolean isDirectory(Path path) {
//        /*
//         * On Windows, testing for junctions requires testing for existence of the file
//         * system entry. Here path.toFile().exists() does not work, whereas
//         * Files.exists(path) works fine.
//         */
//        return path.toFile().isDirectory() && Files.exists(path);
//    }

}
