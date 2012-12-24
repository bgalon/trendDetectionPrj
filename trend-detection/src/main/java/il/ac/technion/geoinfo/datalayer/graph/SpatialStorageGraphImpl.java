package il.ac.technion.geoinfo.datalayer.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.SpatialRecord;
import org.neo4j.gis.spatial.WKTGeometryEncoder;
import org.neo4j.gis.spatial.encoders.SimplePointEncoder;
import org.neo4j.gis.spatial.encoders.SimplePropertyEncoder;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import scala.remote;

import com.vividsolutions.jts.geom.Geometry;

import il.ac.technion.geoinfo.datalayer.SpatialStorage;
import il.ac.technion.geoinfo.domain.graphImpl.SpatialNode;
import il.ac.technion.geoinfo.domain.interfaces.ISpatialEntity;

public class SpatialStorageGraphImpl implements SpatialStorage {

	private static final String LEVELS_NAME = "level";
	private static final double METER = 8.98315E-06;
	
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
	public void addSpatialEntity(Geometry geom, Map<String, String> attributes) throws Exception {
		if(currentLayer >= levels.size()){
			levels.add(sdb.createLayer(levelName(currentLayer), WKTGeometryEncoder.class, EditableLayer.class));
		}
		Layer layerToadd = levels.get(currentLayer);
		addSE(geom, attributes, levels.get(currentLayer));
	}
	
	private ISpatialEntity addSE(Geometry geom, Map<String, String> attributes, Layer layer) throws Exception{
		//1.Check if the geometry already exist in the layer
		GeoPipeline pipeline = GeoPipeline.startEqualExactSearch(layer, geom, 0.001);
		if (pipeline.count() > 0){
			throw new Exception("Geometry " + geom.toString() + " alrady exsist in layer" + layer.getName());
		}
		//we assume that the layer is implementation of EditableLayer  
		if (!(layer instanceof EditableLayer)){
			throw new Exception("Layer " + layer.getName() + " is not intace of EditableLayer");
		}
		//2.build the spatial records
		//TODO:think about do it in transaction - it will be nested transaction! 
		SpatialDatabaseRecord spatialRecord = ((EditableLayer)layer).add(geom);
		for(Map.Entry<String, String> entry:attributes.entrySet()){
			spatialRecord.setProperty(entry.getKey(), entry.getValue());
		}
		//3.find and update fathers
		SpatialNode newNode = new SpatialNode(spatialRecord.getGeomNode());
		if (currentLayer > 0){
			Layer parantLyaer = levels.get(currentLayer - 1);
			GeoPipeline parantPipeline = GeoPipeline.startWithinSearch(parantLyaer, geom);
			for (Node result:parantPipeline.toNodeList()){
				newNode.addParent(result);
			}
		}
		
		//4.find and update siblings
		Geometry bufferedGeom = geom.buffer(METER * 3);
		GeoPipeline sibilingPipeline = GeoPipeline.startIntersectSearch(layer, bufferedGeom);
		for (Node result:sibilingPipeline.toNodeList()){
			newNode.addSibling(result);
		}
		
		return newNode; 
	}
	
	 

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
