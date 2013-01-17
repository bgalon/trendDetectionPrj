package il.ac.technion.geoinfo.datalayer.graph;

import static org.junit.Assert.*;

import java.util.HashMap;

import il.ac.technion.geoinfo.datalayer.SSN;
import il.ac.technion.geoinfo.datalayer.SpatialStorage;
import il.ac.technion.geoinfo.domain.interfaces.ISpatialEntity;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.test.ImpermanentGraphDatabase;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
 
public class SpatialStorageGraphImplTest {
	
	static final String POLY_1 = "POLYGON((0 0, 0 5, 5 5 , 5 0, 0 0 ))";
	static final String POLY_2 = "POLYGON ((0 5, 5 5, 5 10, 0 10, 0 5))";
	
	static final String LINESTRING_1 = "LINESTRING(-1 0, 7 11)";
	static final String LINESTRING_2 = "LINESTRING(-1 -1, 7 15)";
	

	@Test
	public void testAddSpatialEntity() throws Exception {
		WKTReader wktReader = new WKTReader();
		Geometry poly1 = wktReader.read(POLY_1);
		Geometry poly2 = wktReader.read(POLY_2);
		Geometry line1 = wktReader.read(LINESTRING_1);
		Geometry line2 = wktReader.read(LINESTRING_2);
		
		SSN ssn = new SSNgraphImpl(new ImpermanentGraphDatabase());
		
		SpatialStorage ss = ssn.getSpatial();
		
		ISpatialEntity p1 = ss.addSpatialEntity(poly1, new HashMap<String,String>());
		ISpatialEntity p2 = ss.addSpatialEntity(poly2, new HashMap<String,String>());
		ss.closeLevel();
		ISpatialEntity l1 = ss.addSpatialEntity(line1, new HashMap<String,String>());
		ISpatialEntity l2 = ss.addSpatialEntity(line2, new HashMap<String,String>());
		boolean hadParant = false;
		for (ISpatialEntity parnt:l1.getParants()){
			if (parnt == p1) hadParant = true;
		}
		Assert.assertTrue(hadParant);
		
		
	}

}
