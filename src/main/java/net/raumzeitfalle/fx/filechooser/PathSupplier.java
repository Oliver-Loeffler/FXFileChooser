package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.function.Consumer;

@FunctionalInterface
public interface PathSupplier {
    void getUpdate(Consumer<Path> update);
}
