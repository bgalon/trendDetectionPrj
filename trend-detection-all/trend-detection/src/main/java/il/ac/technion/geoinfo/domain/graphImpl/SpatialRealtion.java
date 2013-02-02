package il.ac.technion.geoinfo.domain.graphImpl;

import org.neo4j.graphdb.RelationshipType;

public enum SpatialRealtion implements RelationshipType {
	within, siblings
}
