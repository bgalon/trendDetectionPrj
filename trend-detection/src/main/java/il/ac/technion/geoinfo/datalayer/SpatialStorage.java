package il.ac.technion.geoinfo.datalayer;

import il.ac.technion.geoinfo.domain.interfaces.ISpatialEntity;

import java.util.Collection;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

public interface SpatialStorage {
	ISpatialEntity addSpatialEntity(Geometry geom, Map<String,String> attributes) throws Exception;
	int getLevel();
	int getNumOfLevels();
	void closeLevel();
	
}
