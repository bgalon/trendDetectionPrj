package il.ac.technion.geoinfo.datalayer;

import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

public interface SpatialStorage {
	void addSpatialEntity(Geometry geom, Map<String,String> attributes);
	int getLevel();
	int getNumOfLevels();
	void closeLevel();
	
	
}
