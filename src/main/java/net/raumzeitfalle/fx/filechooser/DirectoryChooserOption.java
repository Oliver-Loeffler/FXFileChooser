package net.raumzeitfalle.fx.filechooser;

import java.util.function.Function;

import net.raumzeitfalle.fx.dirchooser.DirectoryChooser;

public enum DirectoryChooserOption implements Function<FileChooser, PathSupplier> {
    JAVAFX_PLATFORM {       
        @Override
        public PathSupplier apply(FileChooser fileChooser) {
            return FXDirectoryChooser.createIn(fileChooser.currentSearchPath(),
                                               ()->fileChooser.getWindow());
        }
    },
    CUSTOM{       
        @Override
        public PathSupplier apply(FileChooser fileChooser) {
           return new DirectoryChooser.DirChooserPathSupplier(fileChooser);
        }
    };
}
