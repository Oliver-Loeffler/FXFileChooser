package net.raumzeitfalle.fx.filechooser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RefreshBufferTest {

	@ParameterizedTest
	@CsvSource({
		"1,       10",
		"5,       10",
		"100,     10",
		"1001,    20",
		"5000,    20",
		"5001,    50",
		"15000,   50",
		"15001,  100",
		"50000,  100",
		"50001,  200",
		"100000, 200",
		"100001, 500"
	})
	void cacheSize(Integer items, Integer expectedBufferSize) {
		
		int bufferSize = RefreshBuffer.determineBufferSize(items);
		
		assertEquals(bufferSize, expectedBufferSize);
		
	}

}
