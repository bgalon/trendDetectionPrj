package filters;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import config.Configurtaion;

public class BoudaryExtractorTest {

	@Test
	public void testExtractSimulatedBoundary() throws SAXException, IOException {
		assertEquals(2, new BoudaryExtractor()
				.extractBoundary(6, new File(Configurtaion.TEST_FILE)).size());
	}

	@Test
	public void testExtractBoundary() throws SAXException, IOException {
		assertEquals(14, new BoudaryExtractor()
				.extractBoundary(8, new File(Configurtaion.OSM_FILE)).size());
	}
}
