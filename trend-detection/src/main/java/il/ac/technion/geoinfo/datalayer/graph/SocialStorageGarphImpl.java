package il.ac.technion.geoinfo.datalayer.graph;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;

import il.ac.technion.geoinfo.datalayer.SocialStorage;

public class SocialStorageGarphImpl implements SocialStorage {

	private final GraphDatabaseService gdb;
	
	public SocialStorageGarphImpl(GraphDatabaseService gdb){
		this.gdb = gdb;
	}
	
	@Override
	public void addUser(String userId, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}

}
