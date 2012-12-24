package il.ac.technion.geoinfo.domain.graphImpl;

import org.neo4j.graphdb.Node;

public class NodeWrapper {
	protected final Node underlayingNode;
	
	public NodeWrapper(Node theUnderlaying)
	{
		underlayingNode = theUnderlaying;
	}
	
	

}
