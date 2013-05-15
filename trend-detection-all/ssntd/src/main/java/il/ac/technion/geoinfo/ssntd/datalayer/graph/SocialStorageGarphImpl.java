package il.ac.technion.geoinfo.ssntd.datalayer.graph;

import il.ac.technion.geoinfo.ssntd.datalayer.SocialStorage;
import org.neo4j.graphdb.GraphDatabaseService;

import java.util.Map;

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
