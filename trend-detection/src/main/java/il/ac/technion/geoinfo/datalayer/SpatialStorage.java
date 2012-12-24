package il.ac.technion.geoinfo.datalayer;

import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

public interface SpatialStorage {
	void addSpatialEntity(Geometry geom, Map<String,String> attributes) throws Exception;
	int getLevel();
	int getNumOfLevels();
	void closeLevel();
	
	
}
