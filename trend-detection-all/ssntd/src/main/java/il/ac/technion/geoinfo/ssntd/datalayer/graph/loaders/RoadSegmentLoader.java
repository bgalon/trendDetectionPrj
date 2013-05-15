package il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import il.ac.technion.geoinfo.ssntd.domain.graphImpl.SpatialNode;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.Node;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ben
 * Date: 20/04/13
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public class RoadSegmentLoader extends DefaultLoader {

    public static final String ONEWAY_PROPERTY = "osm_oneway";

    @Override
    public Collection<ISpatialEntity> loadToLevel(EditableLayer layer, Geometry geom, Map<String,String> attributes) {

        List<GeoEntity> addToDB = new ArrayList<GeoEntity>();

        //1.find all the road segments in the database
        List<SpatialDatabaseRecord> intersects = GeoPipeline.startIntersectSearch(layer, geom).toSpatialDatabaseRecordList();


        if(intersects.size() == 0 ){
            //in case there are no intersection all there is to do is insert the new geom
            addToDB.add(new GeoEntity(geom, attributes));
        }

        //in case that geometries in the database cuts due to the new geometry
        for(SpatialDatabaseRecord tempSpatialRecord:intersects)
        {
            Geometry otherDiffGeom = tempSpatialRecord.getGeometry().difference(geom);
            Map<String,String> attMap = new HashMap<String,String>();
            Node tempGeoNode = tempSpatialRecord.getGeomNode();
            for (String key : tempGeoNode.getPropertyKeys()){
                //these key are created and manage by the neo4j spatial
                if (key != "gtype" && key != "wkt" && key != "bbox") {
                    attMap.put(key,(String)tempGeoNode.getProperty(key));
                }
            }

            //TODO: I've skip this step when moving the code to the new platform, need to check if this has any meaning
            //if(otherDiffGeom.getNumGeometries() > 1){
            for (int i = 0; i < otherDiffGeom.getNumGeometries(); i++){
                GeoEntity tempGeoEntity = new GeoEntity();
                tempGeoEntity.geom = otherDiffGeom.getGeometryN(i);
                tempGeoEntity.tagsMap = attMap;
                addToDB.add(tempGeoEntity);
            }
            removeSpatialRec(layer, tempSpatialRecord);

            Geometry geomDiffOther = geom.difference(tempSpatialRecord.getGeometry());
            for (int i = 0; i < geomDiffOther.getNumGeometries(); i++){
                addToDB.add(new GeoEntity(geomDiffOther.getGeometryN(i), attributes));
            }
        }
        List<ISpatialEntity> results = new LinkedList<ISpatialEntity>();
        for (GeoEntity geoEntity: addToDB){
            results.add(addSingleRoad(layer,geoEntity));
        }
        return results;
    }

    private ISpatialEntity addSingleRoad(EditableLayer layer, GeoEntity geoEntity){
        ISpatialEntity se = addSR(layer,geoEntity.geom ,geoEntity.tagsMap);

        List<SpatialDatabaseRecord> touches = GeoPipeline.startTouchSearch(layer, geoEntity.geom).toSpatialDatabaseRecordList();
        for(SpatialDatabaseRecord tempSdbr:touches){
            ISpatialEntity otherSe = new SpatialNode(tempSdbr);
            if(CheckRSConnectivity(se, otherSe)){
                se.addConnection(otherSe);
            }
            if(CheckRSConnectivity(otherSe, se)){
                otherSe.addConnection(se);
            }
        }
        return se;
    }

    private boolean CheckRSConnectivity(ISpatialEntity sdbrFrom, ISpatialEntity sdbrTo)
    {
        //in this function we assume that the road segments are touched at the ends of each segments
        if (!sdbrTo.getGeometry().getGeometryType().equals("LineString") && !sdbrFrom.getGeometry().getGeometryType().equals("LineString"))
            return false;

        LineString lsTo = (LineString)sdbrTo.getGeometry();
        LineString lsFrom = (LineString)sdbrFrom.getGeometry();
        if(IsOneway(sdbrFrom))
        {
            if(IsOneway(sdbrTo))
            {
                return lsFrom.getEndPoint().equals(lsTo.getStartPoint());
            }
            else
            {
                return lsTo.touches(lsFrom.getEndPoint());
            }
        }
        else
        {
            if(IsOneway(sdbrTo))
            {
                return lsFrom.touches(lsTo.getStartPoint());
            }
            else
            {
                return lsFrom.touches(lsTo);
            }
        }
    }

    private boolean IsOneway(ISpatialEntity se)
    {
        if(se.hasProperty(ONEWAY_PROPERTY) && ((String)se.getProperty(ONEWAY_PROPERTY)).equals("yes"))
            return true;
        return false;
    }


    private void removeSpatialRec(EditableLayer layer, SpatialDatabaseRecord sdbr){
        layer.delete(sdbr.getNodeId());
    }


    private class GeoEntity{
        public Geometry geom;
        public Map<String, String> tagsMap;

        private GeoEntity() {
        }

        private GeoEntity(Geometry geom, Map<String, String> tagsMap) {
            this.geom = geom;
            this.tagsMap = tagsMap;
        }
    }


}
