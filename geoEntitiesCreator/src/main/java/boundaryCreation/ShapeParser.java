package boundaryCreation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

public class ShapeParser {

	public enum BoundaryScale {
		MSOA {
			// @Override
			// public String getDefaultNameAttribute() {
			// return "MSOA11CD";
			// }

			@Override
			public List<String> getAttributeNames() {
				return Arrays.asList(new String[] { "MSOA11CD", "MSOA11NM", "LAD11NM" });
			}

		},
		LSOA {
			// @Override
			// public String getDefaultNameAttribute() {
			// return "LSOA11CD";
			// }

			@Override
			public List<String> getAttributeNames() {
				return Arrays.asList(new String[] { "LSOA11CD", "LSOA11NM", "MSOA11NM" });
			}

		},
		OA {
			// @Override
			// public String getDefaultNameAttribute() {
			// return "OA11CD";
			// } // WD11NM_BF

			@Override
			public List<String> getAttributeNames() {
				return Arrays.asList(new String[] { "OA11CD", "LSOA11CD", "MSOA11CD", "WD11NM_BF",
						"LSOA11NM", "MSOA11NM" });
			}

		};

		public abstract List<String> getAttributeNames();
	}

	public Map<Geometry, Map<String, String>> parse(String folder) throws IOException {
		File f = new File(folder);
		if (!f.exists() || !f.isDirectory())
			throw new IllegalArgumentException();

		Map<Geometry, Map<String, String>> $ = new HashMap<>();

		for (String shpFile : f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".shp"))
					return true;
				return false;
			}
		})) {
			BoundaryScale bs = BoundaryScale.valueOf(shpFile.substring(0, shpFile.indexOf("_")));

			ShapefileDataStore store = new ShapefileDataStore(new File(folder + "/" + shpFile)
					.toURI().toURL());
			addGeometriesFromFile($, bs, store);
		}
		return $;
	}

	private void addGeometriesFromFile(Map<Geometry, Map<String, String>> $, BoundaryScale bs,
			ShapefileDataStore store) throws IOException {
		FeatureReader reader = store.getFeatureReader();
		while (reader.hasNext()) {
			Feature feature = reader.next();
			// $.put(name, new HashSet<Geometry>());// "LSOA11NM"
			Geometry geom = (Geometry) ((SimpleFeature) feature).getDefaultGeometry();
			Map<String, String> attributes = new HashMap<>();
			for (String attr : bs.getAttributeNames())
				attributes.put(attr, ((SimpleFeature) feature).getAttribute(attr).toString());
			for (int i = 0; i < geom.getNumGeometries(); ++i) {
				Geometry geomN = geom.getGeometryN(i);
				$.put(geomN, attributes);
			}
		}
		reader.close();
	}

	public static void main(String[] args) throws IOException {
		Map<Geometry, Map<String, String>> parse = new ShapeParser()
				.parse("e:/Thesis/TrendDetection/Data/londonBoundaries/MSOA_2011_BFC_London/");// /LSOA_2011_BFC_City_of_London.shp
																								// resource
		System.out.println(parse.keySet().size());
	}
}
