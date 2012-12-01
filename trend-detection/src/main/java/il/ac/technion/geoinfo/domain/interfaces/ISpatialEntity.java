package il.ac.technion.geoinfo.domain.interfaces;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public interface ISpatialEntity {
	Geometry GetGeometry() throws ParseException;
	
}
