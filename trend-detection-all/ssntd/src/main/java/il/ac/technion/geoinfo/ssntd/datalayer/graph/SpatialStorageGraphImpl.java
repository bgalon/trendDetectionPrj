package il.ac.technion.geoinfo.ssntd.datalayer.graph;

import com.vividsolutions.jts.geom.Geometry;
import il.ac.technion.geoinfo.ssntd.datalayer.SpatialStorage;
import il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders.DefaultLoader;
import il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders.RoadSegmentLoader;
import il.ac.technion.geoinfo.ssntd.domain.graphImpl.SpatialNode;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.IConstants;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.gis.spatial.*;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SpatialStorageGraphImpl implements SpatialStorage, IConstants {

    private final Logger logger = LogManager.getLogger(SpatialStorageGraphImpl.class);

    private static final String LEVELS_NAME = "level";

	private final SpatialDatabaseService sdb;
    private final GraphDatabaseService gdb;

	private List<Layer> levels; //levels starts at 0
	private int currentLayer;

//    @Autowired
    private RoadSegmentLoader roadLoader= new RoadSegmentLoader();

    //TODO: fix this @Autowired
    private DefaultLoader defaultLoader = new DefaultLoader();

    public SpatialStorageGraphImpl(String graphDbPath){
        this.gdb = new EmbeddedGraphDatabase(graphDbPath);
        this.sdb = new SpatialDatabaseService(gdb);
        levelsInit();
    }

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
            Layer tempLayer = sdb.getLayer(levelName(levelNum));
			result.add(tempLayer);
		    levelNum++;
		}
		levels = result;
		currentLayer = levelNum;
        logger.info("level has initialized to " + currentLayer);
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
            logger.debug("start inserting " + geom);
			//1.Check if the geometry already exist in the layer
			GeoPipeline pipeline = GeoPipeline.startEqualExactSearch(layer, geom, 0.001);
			if (pipeline.count() > 0){
				//throw new Exception("Geometry " + geom.toString() + " alrady exsist in layer" + layer.getName());
                logger.warn("geometry (" + geom + ") already exist, skipping");
                return null;
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
                logger.debug("adding linestring");
                toAddCol = roadLoader.loadToLevel(editableLayerlayer, geom, attributes);
            }else{
                toAddCol = defaultLoader.loadToLevel(editableLayerlayer, geom, attributes);
            }
            if (toAddCol == null){
                return null;
            }
            logger.debug("loader found " + toAddCol.size() + " entities");
            //4.create spatial entity, find and update children
            List<ISpatialEntity> resultList = new ArrayList<ISpatialEntity>();
            for(ISpatialEntity se : toAddCol) {
                if (currentLayer > 0){
                    Layer childernLayer = levels.get(currentLayer - 1);
                    GeoPipeline parantPipeline = GeoPipeline.startIntersectSearch(childernLayer, geom);
                    if (parantPipeline.size() > 0){
                        for (SpatialDatabaseRecord child:parantPipeline.toSpatialDatabaseRecordList()){
                            se.addChild(new SpatialNode(child));
                        }
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
	
    public String getGraphPath(){
        if(gdb instanceof EmbeddedGraphDatabase)
            return ((EmbeddedGraphDatabase)gdb).getStoreDir();
        return null;
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


    @Override
    public void shutdown() {
        gdb.shutdown();
    }
}
