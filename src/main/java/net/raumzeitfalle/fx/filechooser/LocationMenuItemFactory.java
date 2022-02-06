/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2020 Oliver Loeffler, Raumzeitfalle.net
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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.control.MenuItem;
import net.raumzeitfalle.fx.filechooser.locations.Location;

class LocationMenuItemFactory implements Function<Location,LocationMenuItem> {

    private final Consumer<Location> pathConsumer;

    LocationMenuItemFactory(Consumer<Location> pathConsumer) {
        this.pathConsumer = Objects.requireNonNull(pathConsumer, "pathConsumer must not be null.");
    }

    @Override
    public LocationMenuItem apply(Location location) {
        LocationMenuItem item = new LocationMenuItem(location);
        item.setOnAction(e -> Invoke.later(location, pathConsumer));
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

