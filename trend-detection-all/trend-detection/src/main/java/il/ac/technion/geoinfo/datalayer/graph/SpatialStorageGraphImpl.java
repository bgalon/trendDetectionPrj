package il.ac.technion.geoinfo.datalayer.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.EditableLayerImpl;
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
import org.neo4j.graphdb.Transaction;

import scala.remote;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

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
	public ISpatialEntity addSpatialEntity(Geometry geom, Map<String, String> attributes) throws Exception {
		if(currentLayer >= levels.size()){
			levels.add(sdb.createLayer(levelName(currentLayer), WKTGeometryEncoder.class, EditableLayerImpl.class));
		}
		Layer layerToadd = levels.get(currentLayer);
		return addSE(geom, attributes, levels.get(currentLayer));
	}
	
	private ISpatialEntity addSE(Geometry geom, Map<String, String> attributes, Layer layer) throws Exception{
		//TODO:run all this in transaction, we should think a bit more about the transactions.
		Transaction tx = gdb.beginTx();
		try{
			//1.Check if the geometry already exist in the layer
			GeoPipeline pipeline = GeoPipeline.startEqualExactSearch(layer, geom, 0.001);
			if (pipeline.count() > 0){
				throw new Exception("Geometry " + geom.toString() + " alrady exsist in layer" + layer.getName());
			}
			//we assume that the layer is implementation of EditableLayer  
			if (!(layer instanceof EditableLayer)){
				throw new Exception("Layer " + layer.getName() + " is not intace of EditableLayer");
			}
			//2. split the entity by a given set of roles and of course update the database
			
			
			
			
			//3.build the spatial records
			SpatialDatabaseRecord spatialRecord = ((EditableLayer)layer).add(geom);
			for(Map.Entry<String, String> entry:attributes.entrySet()){
				spatialRecord.setProperty(entry.getKey(), entry.getValue());
			}
			
			
			//4.find and update children 
			SpatialNode newNode = new SpatialNode(spatialRecord.getGeomNode());
			if (currentLayer > 0){
				Layer childernLayer = levels.get(currentLayer - 1);
//				GeoPipeline parantPipeline = GeoPipeline.startContainSearch(childernLayer, geom);
				GeoPipeline parantPipeline = GeoPipeline.startIntersectSearch(childernLayer, geom);
				//TODO: test for case there are no results
				for (Node child:parantPipeline.toNodeList()){
					newNode.addChild(child);
				}
			}
			
			//5.find and update siblings
			Geometry bufferedGeom = geom.buffer(METER * 3);
			GeoPipeline sibilingPipeline = GeoPipeline.startIntersectSearch(layer, bufferedGeom);
			for (Node result:sibilingPipeline.toNodeList()){
				newNode.addSibling(result);
			}
			tx.success();
			return newNode; 
		}catch(Exception ex){
			tx.failure();
			throw ex;
		}finally{
			tx.finish();
		}
	}
	
	//**************************************************************************
	//TODO:this section should be written in a new class (profiler class maybe)
	//*************************************************************************
	
	//insert a new road segment including splitting of this segment and its neighborhoods
	//Also add the touch relationships that connect the road segments
	public void insertRoads(LineString newRoad){
		
	}
	
	//insert a new entity without as is without cutting in
	//include finds the siblings  
	public void insertEntity(Geometry newGeom){
		
	}
	
	//same as insertEntity but also finds the children in the lower level
	public void insertGroup(Polygon newGroup){
		
	}
	
	
//	public Collection<SpatialNode> splitLineStrings(Geometry newGeom){
//		
//	}
//	
	
	
	//***************************************************************************

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
