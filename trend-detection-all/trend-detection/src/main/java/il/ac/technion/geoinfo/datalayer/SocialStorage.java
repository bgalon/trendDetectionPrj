package il.ac.technion.geoinfo.datalayer;

import java.util.Map;

public interface SocialStorage {
	void addUser(String userId, Map<String,String> attributes);

}
