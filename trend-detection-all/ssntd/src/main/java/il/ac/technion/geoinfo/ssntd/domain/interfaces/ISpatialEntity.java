package il.ac.technion.geoinfo.ssntd.domain.interfaces;

import com.vividsolutions.jts.geom.Geometry;

import java.util.Collection;

public interface ISpatialEntity extends IEntity{
	Geometry getGeometry();
	
	int getLayer();

    void addParent(ISpatialEntity parent);
    void addChild(ISpatialEntity child);
    void addSibling(ISpatialEntity sibling);
    void addConnection(ISpatialEntity connected);

	Collection<ISpatialEntity> getParants();
	Collection<ISpatialEntity> getSiblings();
	Collection<ISpatialEntity> getChildren();
    Collection<ISpatialEntity> getConnected();
}
