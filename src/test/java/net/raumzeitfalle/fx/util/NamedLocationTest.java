package net.raumzeitfalle.fx.util;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

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
	void creation_at_root() {
		
		Location atRoot = Locations.at(Paths.get("/"));
	
		assertTrue(atRoot instanceof NamedLocation);
		assertEquals("/", atRoot.getName());
		assertEquals(Paths.get("/"), atRoot.getPath());
	}
	
	@Test
	void creation_at_directory() {
		
		Location atRoot = Locations.at(Paths.get("/directory/"));
	
		assertTrue(atRoot instanceof NamedLocation);
		assertEquals("/directory", atRoot.getName());
		assertEquals(Paths.get("/directory"), atRoot.getPath());
	}
	
	@Test
	void creation_at_file() {
		
		Location atFile = Locations.at(Paths.get("./README.md"));
	
		assertTrue(atFile instanceof NamedLocation);
		assertEquals(".", atFile.getName());
		assertEquals(Paths.get("."), atFile.getPath());
	}
	
	@Test
	void identity_different_instances() {
		
		Location a = Locations.withName("MyName", Paths.get("./"));
		Location b = Locations.withName("MyName", Paths.get("./"));
		
		assertEquals(a,b);
		assertEquals(a.hashCode(), b.hashCode());
		
	}

	@Test
	void identity_same_instance() {
		
		Location a = Locations.withName("MyName", Paths.get("./"));
		Location c = a;
		
		assertEquals(a,c);
		assertEquals(a.hashCode(), c.hashCode());
			
	}
	
	@Test
	void identity_sameName_differentPath() {
		
		Location a = Locations.withName("MyName", Paths.get("./"));		
		Location d = Locations.withName("MyName", Paths.get("./someOther/subDirectory"));
		
		assertNotEquals(a,d);
		assertNotEquals(a.hashCode(), d.hashCode());
	}
	
	@Test
	void identity_differentPath_sameName() {
		
		Location a = Locations.withName("MyName", Paths.get("./"));
		Location e = Locations.withName("JustDifferentName", Paths.get("./"));
		
		assertNotEquals(a,e);
		assertNotEquals(a.hashCode(), e.hashCode());
	}
	
	@Test
	void identity_sameValues_differentType() {
		
		Location a = Locations.withName("MyName", Paths.get("./"));
		Location f = new Location() {
			
			@Override
			public Path getPath() { return Paths.get("./"); }
			
			@Override
			public String getName() { return "MyName"; }
			
			@Override
			public boolean exists() { return false; }
		};
		
		assertEquals(a,f);
	}
	
	@Test
	void identity_with_null() {
		
		Location a = Locations.withName("MyName", Paths.get("./"));
		assertFalse(a.equals(null));	
		
	}

}
