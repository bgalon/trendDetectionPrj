package il.ac.technion.geoinfo.ssntd.datalayer.graph;

import com.vividsolutions.jts.geom.Geometry;
import il.ac.technion.geoinfo.ssntd.datalayer.SpatialStorage;
import il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders.DefaultLoader;
import il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders.RoadSegmentLoader;
import il.ac.technion.geoinfo.ssntd.domain.graphImpl.SpatialNode;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.IConstants;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;
import org.neo4j.gis.spatial.*;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SpatialStorageGraphImpl implements SpatialStorage, IConstants {

	private static final String LEVELS_NAME = "level";

	private final SpatialDatabaseService sdb;
    private final GraphDatabaseService gdb;

	private List<Layer> levels; //levels starts at 0
	private int currentLayer;

//    @Autowired
    private RoadSegmentLoader roadLoader= new RoadSegmentLoader();

    //TODO: fix this @Autowired
    private DefaultLoader defaultLoader = new DefaultLoader();

    public SpatialStorageGraphImpl(SpatialDatabaseService sdb){
        this.sdb = sdb;
        this.gdb = sdb.getDatabase();
        levelsInit();
    }

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
	public Collection<ISpatialEntity> addSpatialEntity(Geometry geom, Map<String, String> attributes) throws Exception {
		if(currentLayer >= levels.size()){
			levels.add(sdb.createLayer(levelName(currentLayer), WKTGeometryEncoder.class, EditableLayerImpl.class));
		}
		Layer layerToAdd = levels.get(currentLayer);
		return addSE(geom, attributes, layerToAdd);
	}
	
	private Collection<ISpatialEntity> addSE(Geometry geom, Map<String, String> attributes, Layer layer) throws Exception{
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
            EditableLayer editableLayerlayer = (EditableLayer)layer;
			//2. split the entity by a given set of roles and of course update the database
            //this stage is update this level and build the sibling relationships
            //and 3.build the spatial records
            //TODO: add strategy change here
            Collection<ISpatialEntity> toAddCol = null;
            if (geom.getGeometryType().equalsIgnoreCase("LineString")) {
                toAddCol = roadLoader.loadToLevel(editableLayerlayer, geom, attributes);
            }else{
                toAddCol = defaultLoader.loadToLevel(editableLayerlayer, geom, attributes);
            }
            if (toAddCol == null){
                return null;
            }

            //4.create spatial entity, find and update children
            List<ISpatialEntity> resultList = new ArrayList<ISpatialEntity>();
            for(ISpatialEntity se : toAddCol) {

                if (currentLayer > 0){
                    Layer childernLayer = levels.get(currentLayer - 1);
                    GeoPipeline parantPipeline = GeoPipeline.startContainSearch(childernLayer, geom);
                    //TODO: test for case there are no results
                    for (SpatialDatabaseRecord child:parantPipeline.toSpatialDatabaseRecordList()){
                        se.addChild(new SpatialNode(child));
                    }
                }
                resultList.add(se);
            }

			tx.success();
			return resultList;
		}catch(Exception ex){
			tx.failure();
			throw ex;
		}finally{
			tx.finish();
		}
	}
	
//	//**************************************************************************
//	//TODO:this section should be written in a new class (profiler class maybe)
//	//*************************************************************************
//
//	//insert a new road segment including splitting of this segment and its neighborhoods
//	//Also add the touch relationships that connect the road segments
//	public Collection<SpatialRecord> insertRoads(LineString newRoad){
//
//	}
//
//	//insert a new entity without as is without cutting in
//	//include finds the siblings
//	public Collection<SpatialRecord> insertEntity(Geometry newGeom){
//
//	}
//
//	//same as insertEntity but also finds the children in the lower level
//	public Collection<SpatialRecord> insertGroup(Polygon newGroup){
//
//	}
//
//
//	//***************************************************************************

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
