package net.raumzeitfalle.fx.filechooser;

import java.nio.file.Path;
import java.util.Optional;

@FunctionalInterface
public interface PathSupplier {
    Optional<Path> get();
}
