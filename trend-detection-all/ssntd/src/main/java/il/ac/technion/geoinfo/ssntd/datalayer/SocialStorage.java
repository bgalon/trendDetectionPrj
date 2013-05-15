package il.ac.technion.geoinfo.ssntd.datalayer;

import java.util.Map;

public interface SocialStorage {
	void addUser(String userId, Map<String,String> attributes);

}
