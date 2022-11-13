/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.filechooser.locations;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NamedLocationTest {

    @Test
    void creation_withName() {
        Location namedLocation = Locations.withName("MyName", Paths.get("./"));
        assertTrue(namedLocation instanceof NamedLocation);
        assertEquals("MyName", namedLocation.getName());
        assertEquals(Paths.get("./"), namedLocation.getPath());
    }

    @Test
    void creation_at() {
        Location path = Locations.at(Paths.get("path"));
        assertTrue(path instanceof NamedLocation);
        assertEquals("path", path.getName());
        assertEquals(Paths.get("path"), path.getPath());
    }

    @Test
    void creation_at_directory() {
        Location atRoot = Locations.at(Paths.get("/directory/other"));
        assertTrue(atRoot instanceof NamedLocation);
        assertEquals(Paths.get("/directory/other"), Paths.get(atRoot.getName()));
        assertEquals(Paths.get("/directory/other"), atRoot.getPath());
    }

    @Test
    void creation_at_file() {
        // important, this location must point to an existing file!
        Location atFile = Locations.at(Paths.get("./pom.xml"));
        assertTrue(atFile instanceof NamedLocation);
        assertEquals(".", atFile.getName());
        assertEquals(Paths.get("."), atFile.getPath());
    }

    @Test
    void identity_different_instances() {
        Location a = Locations.withName("MyName", Paths.get("./"));
        Location b = Locations.withName("MyName", Paths.get("./"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

    }

    @Test
    void identity_same_instance() {
        Location a = Locations.withName("MyName", Paths.get("./"));
        Location c = a;
        assertEquals(a, c);
        assertEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void identity_sameName_differentPath() {
        Location a = Locations.withName("MyName", Paths.get("./"));
        Location d = Locations.withName("MyName", Paths.get("./someOther/subDirectory"));
        assertNotEquals(a, d);
        assertNotEquals(a.hashCode(), d.hashCode());
    }

    @Test
    void identity_differentPath_sameName() {
        Location a = Locations.withName("MyName", Paths.get("./"));
        Location e = Locations.withName("JustDifferentName", Paths.get("./"));
        assertNotEquals(a, e);
        assertNotEquals(a.hashCode(), e.hashCode());
    }

    @Test
    void identity_sameValues_differentType_butSameInterface() {
        Location a = Locations.withName("MyName", Paths.get("./"));
        Location f = new Location() {

            @Override
            public Path getPath() {
                return Paths.get("./");
            }

            @Override
            public String getName() {
                return "MyName";
            }

            @Override
            public boolean exists() {
                return false;
            }
        };

        assertEquals(a, f);
        assertEquals(a.hashCode(), f.hashCode());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    void identity_with_different_types() {
        Location a = Locations.withName("MyName", Paths.get("./"));
        String location = "Its not a location";
        assertFalse(a.equals(location));
    }

    @Test
    void identity_with_null() {
        Location a = Locations.withName("MyName", Paths.get("./"));
        assertNotEquals(a, (Location) null);
    }

    @Test
    void exception_is_thrown_when_constructorArgIsNull() {
        Throwable t = assertThrows(NullPointerException.class, () -> new NamedLocation(null));
        assertEquals("location must not be null", t.getMessage());
    }

    @Test
    void exception_is_thrown_when_constructorPathIsNull() {
        Throwable t = assertThrows(NullPointerException.class, () -> new NamedLocation("NameOfLoc", null));
        assertEquals("path must not be null", t.getMessage());
    }

    @Test
    void exception_is_thrown_when_constructorNameIsNull() {
        Path path = Paths.get("somewhere");
        Throwable t = assertThrows(NullPointerException.class, () -> new NamedLocation(null, path));
        assertEquals("name must not be null", t.getMessage());
    }

    @Test
    void location_exists(@TempDir Path existingDirectory) {
        Location existingLocation = Locations.withName("ExistingTempDir", existingDirectory);
        assertTrue(existingLocation.exists());
    }

    @Test
    void location_not_exists(@TempDir Path existingDirectory) {
        Location notExistingLocation = Locations.withName("ExistingTempDir", existingDirectory.resolve("shouldNot/exist"));
        assertFalse(notExistingLocation.exists());
    }

    @Test
    void compare() {
        Location a = Locations.withName("A", Paths.get("./"));
        Location b = Locations.withName("B", Paths.get("./"));
        Location c = Locations.withName("C", Paths.get("./"));

        assertEquals(-1, a.compareTo(b));
        assertEquals(1, c.compareTo(b));
        assertEquals(0, b.compareTo(b));
    }
}
