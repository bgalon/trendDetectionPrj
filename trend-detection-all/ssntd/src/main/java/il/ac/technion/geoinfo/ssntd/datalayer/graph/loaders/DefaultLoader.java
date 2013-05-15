package il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders;

import com.vividsolutions.jts.geom.Geometry;
import il.ac.technion.geoinfo.ssntd.domain.graphImpl.SpatialNode;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.IConstants;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ben
 * Date: 20/04/13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public class DefaultLoader extends AbstractLoader implements IConstants {

    @Override
    public Collection<ISpatialEntity> loadToLevel(EditableLayer layer, Geometry geom, Map<String,String> attributes) {
        //1. create a spatial record
        ISpatialEntity sn = addSR((EditableLayer)layer, geom, attributes);
        //2. find and update sibling
        updateSibling(sn, geom, layer);

        List<ISpatialEntity> resultList = new ArrayList<ISpatialEntity>();
        resultList.add(sn);
        return resultList;
    }


    protected void updateSibling(ISpatialEntity sn, Geometry geom, Layer layer) {
        Geometry bufferedGeom = geom.buffer(METER * 3);
        GeoPipeline siblingPipeline = GeoPipeline.startIntersectSearch(layer, bufferedGeom);
        for (SpatialDatabaseRecord result:siblingPipeline.toSpatialDatabaseRecordList()){

            sn.addSibling(new SpatialNode(result));
        }
    }

}
