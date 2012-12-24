package il.ac.technion.geoinfo.domain.interfaces;

import java.util.Collection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public interface ISpatialEntity {
	Geometry GetGeometry() throws ParseException;
	
	int getLayer();
	
	Collection<ISpatialEntity> getParants();
	Collection<ISpatialEntity> getSiblings();
	Collection<ISpatialEntity> getChildren();
}
