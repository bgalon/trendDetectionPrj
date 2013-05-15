package il.ac.technion.geoinfo.ssntd.domain.graphImpl;

import com.vividsolutions.jts.geom.Geometry;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpatialNode extends NodeWrapper implements ISpatialEntity {

    private final SpatialDatabaseRecord underlayingSDBR;

    public SpatialNode(Node theUnderlaying) {
        super(theUnderlaying);
        underlayingSDBR = null;
    }

    public SpatialNode(SpatialDatabaseRecord theUnderlaying) {
		super(theUnderlaying.getGeomNode());
        underlayingSDBR = theUnderlaying;
	}

    @Override
	public void addParent(ISpatialEntity parent){
        Node node = interfaceToNode(parent);
        node.createRelationshipTo(underlayingNode, SpatialRealtion.within);
	}

    @Override
	public void addChild(ISpatialEntity child){
        Node node = interfaceToNode(child);
		underlayingNode.createRelationshipTo(node, SpatialRealtion.within);
	}

    @Override
	public void addSibling(ISpatialEntity sibling){
        Node node = interfaceToNode(sibling);
        node.createRelationshipTo(underlayingNode, SpatialRealtion.siblings);
	}

    @Override
    public void addConnection(ISpatialEntity connected){
        Node node = interfaceToNode(connected);
        underlayingNode.createRelationshipTo(node,SpatialRealtion.connection);
    }

    private Node interfaceToNode(ISpatialEntity iSpatialEntity){
        //we assume that int the only implementation for ISpatialEntity is SpatialNose
        //in the graph environment
        return ((SpatialNode)iSpatialEntity).underlayingNode;
    }

	@Override
	public Geometry getGeometry(){
		if(underlayingSDBR != null){
            return underlayingSDBR.getGeometry();
        }
        else{
            //TODO: another solution need to be implemented here
            return null;
        }
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

    @Override
    public Collection<ISpatialEntity> getConnected() {
        return nodeCollToISEColl(SpatialRealtion.connection, Direction.OUTGOING);
    }


    @Override
    public boolean hasProperty(String key) {
        return underlayingNode.hasProperty(key);
    }

    @Override
    public Object getProperty(String key) {
        return underlayingNode.getProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        underlayingNode.setProperty(key, value);
    }

    private Collection<ISpatialEntity> nodeCollToISEColl(RelationshipType type, Direction dir){
		List<ISpatialEntity> returnedColl = new ArrayList<ISpatialEntity>();
		Iterable<Relationship> result = underlayingNode.getRelationships(type, dir);
		for (Relationship relation:result){
			returnedColl.add(new SpatialNode(relation.getOtherNode(underlayingNode)));
		}
		return returnedColl;
	}
	
	@Override
	public boolean equals( Object o )
	{
	    return o instanceof SpatialNode &&
	    		underlayingNode.equals( ( (SpatialNode)o ).underlayingNode);
	}

}
