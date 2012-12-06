package il.ac.technion.geoinfo.datalayer.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.WKTGeometryEncoder;
import org.neo4j.gis.spatial.encoders.SimplePointEncoder;
import org.neo4j.gis.spatial.encoders.SimplePropertyEncoder;
import org.neo4j.graphdb.GraphDatabaseService;

import com.vividsolutions.jts.geom.Geometry;

import il.ac.technion.geoinfo.datalayer.SpatialStorage;

public class SpatialStorageGraphImpl implements SpatialStorage {

	private static final String LEVELS_NAME = "level";
	
	private final GraphDatabaseService gdb;
	private final SpatialDatabaseService sdb;
	private List<Layer> levels; //levels starts at 0
	private int currentLayer;
	
	public SpatialStorageGraphImpl(GraphDatabaseService gdb){
		this.gdb = gdb;
		this.sdb = new SpatialDatabaseService(gdb);
		levelsInit();
	}
	
	private void levelsInit(){
		List<Layer> result = new ArrayList<Layer>();
		int levelNum = 0;
		while(sdb.containsLayer(levelName(levelNum))){
			result.add(sdb.getDynamicLayer(levelName(levelNum)));
		    levelNum++;
		}
		levels = result;
		currentLayer = levelNum;
	}
	
	
	@Override
	public void addSpatialEntity(Geometry geom, Map<String, String> attributes) {
		if(currentLayer >= levels.size()){
			levels.add(sdb.createLayer(levelName(currentLayer), WKTGeometryEncoder.class, EditableLayer.class));
		}
		Layer layerToadd = levels.get(currentLayer);
		
	}
	
//	private List<> addSE(Geometry geom, Map<String, String> attributes, Layer layer){
//		
//	}

	@Override
	public int getLevel() {
		return currentLayer;
	}

	@Override
	public int getNumOfLevels() {
		return levels.size();
	}

	@Override
	public void closeLevel() {
		currentLayer++;
	}
	
	private String levelName(int level){
		return LEVELS_NAME + level;
	}

}
