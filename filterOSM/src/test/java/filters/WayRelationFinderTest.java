package filters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import filters.OSMObjects.ComplexTagContainer;

public class WayRelationFinderTest {

	private static final String TEST_FILE = "osmFiles/test.osm";

	@Test
	public void test() throws SAXException, FileNotFoundException, IOException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		Set<ComplexTagContainer> elements = new HashSet<>();
		Map<String, Set<ComplexTagContainer>> nodes = new HashMap<>();
		reader.setContentHandler(new WaysFinder(elements, nodes));
		reader.parse(new InputSource(new FileInputStream(TEST_FILE)));

		Assert.assertTrue(nodes.containsKey("102010"));
		Assert.assertTrue(nodes.get("102010").contains(new ComplexTagContainer("149056397")));
		Assert.assertTrue(elements.contains(new ComplexTagContainer("149056397")));
	}
}
