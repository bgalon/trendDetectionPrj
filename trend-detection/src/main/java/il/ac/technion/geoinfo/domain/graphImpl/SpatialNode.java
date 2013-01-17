package il.ac.technion.geoinfo.domain.graphImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import il.ac.technion.geoinfo.domain.interfaces.ISpatialEntity;

public class SpatialNode extends NodeWrapper implements ISpatialEntity {

	public SpatialNode(Node theUnderlaying) {
		super(theUnderlaying);
		// TODO Auto-generated constructor stub
	}
	
	public void addParent(Node parent){
		parent.createRelationshipTo(underlayingNode, SpatialRealtion.within);
	}
	
	public void addSibling(Node sibling){
		sibling.createRelationshipTo(underlayingNode, SpatialRealtion.siblings);
	}

	@Override
	public Geometry GetGeometry() throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLayer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<ISpatialEntity> getParants() {
		return nodeCollToISEColl(SpatialRealtion.within, Direction.INCOMING);
	}

	@Override
	public Collection<ISpatialEntity> getSiblings() {
		return nodeCollToISEColl(SpatialRealtion.siblings, Direction.BOTH);
	}

	@Override
	public Collection<ISpatialEntity> getChildren() {
		return nodeCollToISEColl(SpatialRealtion.within, Direction.OUTGOING);
	}
	
	private Collection<ISpatialEntity> nodeCollToISEColl(RelationshipType type, Direction dir){
		List<ISpatialEntity> returnedColl = new ArrayList<ISpatialEntity>();
		Iterable<Relationship> result = underlayingNode.getRelationships(type, dir);
		for (Relationship relation:result){
			returnedColl.add(new SpatialNode(relation.getOtherNode(underlayingNode)));
		}
		return returnedColl;
	}
}
