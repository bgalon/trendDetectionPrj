package il.ac.technion.geoinfo.datalayer.graph;

import il.ac.technion.geoinfo.datalayer.SSN;
import il.ac.technion.geoinfo.datalayer.SocialStorage;
import il.ac.technion.geoinfo.datalayer.SpatialStorage;

import org.neo4j.collections.graphdb.impl.EmbeddedGraphDatabase;
import org.neo4j.graphdb.GraphDatabaseService;

public class SSNgraphImpl implements SSN{
	
	private final String gdbPath;
	private final GraphDatabaseService gdb;
	private final SocialStorage social;
	private final SpatialStorage spatial;
	
	
	public SSNgraphImpl(String path){
		this.gdbPath = path;
		this.gdb = new EmbeddedGraphDatabase(gdbPath);
		this.social = new SocialStorageGarphImpl(gdb);
		this.spatial = new SpatialStorageGraphImpl(gdb);
	}
	
	//this constructor use for testing
	public SSNgraphImpl(GraphDatabaseService gdb){
		this.gdbPath = null;
		this.gdb = gdb;
		this.social = new SocialStorageGarphImpl(gdb);
		this.spatial = new SpatialStorageGraphImpl(gdb);
	}


	@Override
	public SocialStorage getSocail() {
		return social;
	}


	@Override
	public SpatialStorage getSpatial() {
		return spatial;
	}
	
	public void Close(){
		gdb.shutdown();
	}
	
	@Override
	public void finalize() throws Throwable{
		Close();
	}
}
