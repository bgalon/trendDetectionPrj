package il.ac.technion.geoinfo.datalayer;

import il.ac.technion.geoinfo.domain.interfaces.IPlace;

public interface SpatialDataSource {
	IPlace addPlace();
	IPlace getPlace();
	
}
