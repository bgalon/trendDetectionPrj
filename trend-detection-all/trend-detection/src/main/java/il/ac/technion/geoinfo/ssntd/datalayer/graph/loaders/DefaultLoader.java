package il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import il.ac.technion.geoinfo.ssntd.domain.graphImpl.SpatialNode;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.IConstants;
import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.Node;

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
    public Collection<SpatialDatabaseRecord> loadToLevel(Layer layer, Geometry geom, Map<String,String> attributes) throws ParseException {
        //1. create a spatial record
        SpatialDatabaseRecord sdr = addSR((EditableLayer)layer, geom, attributes);
        SpatialNode sn = new SpatialNode(sdr.getGeomNode());
        //2. find and update sibling
        updateSibling(sn, geom, layer);

        List<SpatialDatabaseRecord> resultList = new ArrayList<SpatialDatabaseRecord>();
        resultList.add(sdr);
        return resultList;
    }


    protected void updateSibling(SpatialNode sn, Geometry geom, Layer layer) throws ParseException {
        Geometry bufferedGeom = geom.buffer(METER * 3);
        GeoPipeline siblingPipeline = GeoPipeline.startIntersectSearch(layer, bufferedGeom);
        for (Node result:siblingPipeline.toNodeList()){
            sn.addSibling(result);
        }
    }
}
