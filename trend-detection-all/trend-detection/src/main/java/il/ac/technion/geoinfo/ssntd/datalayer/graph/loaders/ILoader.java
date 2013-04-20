package il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;

import java.util.Collection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ben
 * Date: 20/04/13
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public interface ILoader {

    public Collection<SpatialDatabaseRecord> loadToLevel(Layer layer, Geometry geom, Map<String,String> attributes) throws ParseException;

}
