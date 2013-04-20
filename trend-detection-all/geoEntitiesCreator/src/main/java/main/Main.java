package main;

import filters.MinimalObjectsCreator;
import il.ac.technion.geoinfo.ssntd.datalayer.SpatialStorage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.SAXException;

import boundaryCreation.ShapeParser;

import com.vividsolutions.jts.geom.Geometry;

public class Main {
	public static void main(String[] args) throws Exception {
		SpatialStorage spatial = null;// = new SpatialStorageGraphImpl(null);
		int level = spatial.getLevel();

		createMinimalObjects(spatial);

		createBoundaries(spatial,
				"e:/Thesis/TrendDetection/Data/londonBoundaries/OA_2011_BGC_London/");
		createBoundaries(spatial,
				"e:/Thesis/TrendDetection/Data/londonBoundaries/LSOA_2011_BGC_London/");
		createBoundaries(spatial,
				"e:/Thesis/TrendDetection/Data/londonBoundaries/MSOA_2011_BGC_London/");
	}

	private static void createMinimalObjects(SpatialStorage spatial) throws SAXException,
			IOException, Exception {
		Map<Geometry, Map<String, String>> mos = new MinimalObjectsCreator().extractMO(new File(
				"/osmFiles/london_small.osm"));
		for (Entry<Geometry, Map<String, String>> spatialEntity : mos.entrySet()) {
			spatial.addSpatialEntity(spatialEntity.getKey(), spatialEntity.getValue());
		}
		spatial.closeLevel();
	}

	private static void createBoundaries(SpatialStorage spatial, String inputFolder)
			throws IOException, Exception {
		Map<Geometry, Map<String, String>> boundaries = new ShapeParser().parse(inputFolder);
		for (Entry<Geometry, Map<String, String>> spatialEntity : boundaries.entrySet()) {
			spatial.addSpatialEntity(spatialEntity.getKey(), spatialEntity.getValue());
		}
		spatial.closeLevel();
	}
}