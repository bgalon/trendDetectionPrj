package il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders;

import com.vividsolutions.jts.geom.Geometry;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ben
 * Date: 20/04/13
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractLoader implements ILoader {


    protected SpatialDatabaseRecord addSR(EditableLayer layer, Geometry geom, Map<String, String> attributes){
        SpatialDatabaseRecord spatialRecord = ((EditableLayer)layer).add(geom);
            for(Map.Entry<String, String> entry:attributes.entrySet()){
                spatialRecord.setProperty(entry.getKey(), entry.getValue());
            }
        return  spatialRecord;
    }
}
