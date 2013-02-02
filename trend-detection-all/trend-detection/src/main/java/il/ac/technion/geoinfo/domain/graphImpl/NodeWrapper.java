package il.ac.technion.geoinfo.domain.graphImpl;

import org.neo4j.graphdb.Node;

public class NodeWrapper {
	protected final Node underlayingNode;
	
	public NodeWrapper(Node theUnderlaying)
	{
		underlayingNode = theUnderlaying;
	}
	
	@Override
	public int hashCode()
	{
	    return underlayingNode.hashCode();
	}
	 
	@Override
	public boolean equals( Object o )
	{
	    return o instanceof NodeWrapper &&
	    		underlayingNode.equals( ( (NodeWrapper)o ).underlayingNode);
	}

}
