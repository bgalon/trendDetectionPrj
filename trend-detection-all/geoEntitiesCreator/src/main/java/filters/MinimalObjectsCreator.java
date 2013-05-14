package filters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import config.Configurtaion;
import filters.OSMObjects.ComplexTagContainer;
import filters.OSMObjects.TagContainer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class MinimalObjectsCreator {

	private final GeometryFactory factory = new GeometryFactory();

	public Map<Geometry, Map<String, String>> extractMO(File destFile) throws SAXException,
			IOException {
		Map<Geometry, Map<String, String>> $ = new HashMap<>();
		Set<ComplexTagContainer> containers = new HashSet<>();
		if (destFile == null || !destFile.exists())
			throw new IllegalArgumentException();

		// Extract Ways
		XMLReader reader = XMLReaderFactory.createXMLReader();
		Map<String, Set<ComplexTagContainer>> nodes = new HashMap<>();
		reader.setContentHandler(new WaysFinder(containers, nodes));
		reader.parse(new InputSource(new FileInputStream(destFile)));

		// extract nodes
		reader.setContentHandler(new NodesFinder(nodes));
		reader.parse(new InputSource(new FileInputStream(destFile)));

		// create geometries
		createGeometries($, containers);
		return $;
	}

	private void createGeometries(Map<Geometry, Map<String, String>> $,
			Set<ComplexTagContainer> containers) {
		for (ComplexTagContainer container : containers) {
            Coordinate[] coordinates = extractCoordinates(container.getNodes());

            if (coordinates.length == 1){ //Point
                $.put(factory.createPoint(coordinates[0]),container.tags);
            }else if (coordinates.length > 1){
                if(coordinates[0].equals2D(coordinates[coordinates.length -1])){ //LinearRing or polygon
                    if(container.getTags().containsKey("area") && container.getTags().get("area").equalsIgnoreCase("yes")){ //Polygon
                        $.put(factory.createPolygon(factory.createLinearRing(coordinates),null),
                                container.tags);
                    }else{//LinearRing
                        $.put(factory.createLinearRing(coordinates),
                                container.tags);
                    }
                }else{//Linestring
                        $.put(factory.createLineString(coordinates),container.tags);
                }
            }
		}
	}

	private Coordinate[] extractCoordinates(Collection<TagContainer> nodes) {
		Set<Coordinate> $ = new HashSet<>();
		for (TagContainer node : nodes) {
			$.add(new Coordinate(Double.valueOf(node.getTags().get("lat")), Double.valueOf(node
					.getTags().get("lon"))));
		}
		return $.toArray(new Coordinate[$.size()]);
	}

	public static void main(String[] args) throws SAXException, IOException {
		System.out.println(new MinimalObjectsCreator().extractMO(new File(Configurtaion.OSM_FILE))
				.size());
	}
}