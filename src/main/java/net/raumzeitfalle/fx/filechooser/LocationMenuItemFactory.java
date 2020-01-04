package net.raumzeitfalle.fx.filechooser;

import javafx.scene.control.MenuItem;
import net.raumzeitfalle.fx.filechooser.locations.Location;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class LocationMenuItemFactory implements Function<Location,LocationMenuItem> {

    private final Consumer<Location> pathConsumer;

    LocationMenuItemFactory(Consumer<Location> pathConsumer) {
        this.pathConsumer = Objects.requireNonNull(pathConsumer, "pathConsumer must not be null.");
    }

    @Override
    public LocationMenuItem apply(Location location) {
        LocationMenuItem item = new LocationMenuItem(location);
        item.setOnAction(e -> Invoke.later(()->pathConsumer.accept(location)));
        return item;
    }
}

class LocationMenuItem extends MenuItem {

    private final Location location;

    LocationMenuItem(Location location) {
        this.location = Objects.requireNonNull(location, "Given location instance must never be null.");
        setText(location.getName());
        setDisable(!location.exists());
    }

    public boolean matchesLocation(Location otherLocation) {
        return this.location.equals(otherLocation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationMenuItem)) return false;
        LocationMenuItem that = (LocationMenuItem) o;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode() + 13;
    }
}

