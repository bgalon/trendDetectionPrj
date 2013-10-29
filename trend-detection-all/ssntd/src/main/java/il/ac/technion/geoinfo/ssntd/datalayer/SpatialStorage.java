package il.ac.technion.geoinfo.ssntd.datalayer;

import com.vividsolutions.jts.geom.Geometry;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;

import java.util.Collection;
import java.util.Map;

public interface SpatialStorage {
	Collection<ISpatialEntity> addSpatialEntity(Geometry geom, Map<String, String> attributes) throws Exception;
	int getLevel();
	int getNumOfLevels();
	void closeLevel();
    void shutdown();


	
}
