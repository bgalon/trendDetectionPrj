package il.ac.technion.geoinfo.ssntd.datalayer.graph.loaders;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;
import org.neo4j.gis.spatial.EditableLayer;

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

    public Collection<ISpatialEntity> loadToLevel(EditableLayer layer, Geometry geom, Map<String,String> attributes) throws ParseException;

}
