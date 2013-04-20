package il.ac.technion.geoinfo.ssntd.datalayer.graph;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import il.ac.technion.geoinfo.ssntd.datalayer.SSN;
import il.ac.technion.geoinfo.ssntd.datalayer.SpatialStorage;
import il.ac.technion.geoinfo.ssntd.domain.interfaces.ISpatialEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class SpatialStorageGraphImplTest {
	
	static final String POLY_1 = "POLYGON((0 0, 0 5, 5 5 , 5 0, 0 0 ))";
	static final String POLY_2 = "POLYGON ((0 5, 5 5, 5 10, 0 10, 0 5))";
	
	static final String LINESTRING_1 = "LINESTRING(-1 0, 7 11)";
	static final String LINESTRING_2 = "LINESTRING(-1 -1, 7 15)";
	
	private GraphDatabaseService testDb;
	
	@Before
	public void createTestDb(){
		testDb = new ImpermanentGraphDatabase();
	}
	
	@After
	public void deleteTestDb(){
		testDb.shutdown();
	}

	@Test
	public void testAddSpatialEntity() throws Exception {
		WKTReader wktReader = new WKTReader();
		Geometry poly1 = wktReader.read(POLY_1);
		Geometry poly2 = wktReader.read(POLY_2);
		Geometry line1 = wktReader.read(LINESTRING_1);
		Geometry line2 = wktReader.read(LINESTRING_2);

		SSN ssn = new SSNgraphImpl(testDb);

		SpatialStorage ss = ssn.getSpatial();

		Collection<ISpatialEntity> l1 = ss.addSpatialEntity(line1, new HashMap<String,String>());
        Collection<ISpatialEntity> l2 = ss.addSpatialEntity(line2, new HashMap<String,String>());
		ss.closeLevel();
        Collection<ISpatialEntity> p1 = ss.addSpatialEntity(poly1, new HashMap<String,String>());
        Collection<ISpatialEntity> p2 = ss.addSpatialEntity(poly2, new HashMap<String,String>());

		boolean hadParant = false;
		for (ISpatialEntity parnt:(l1.iterator().next().getParants())){
			if (parnt.equals(p1)) hadParant = true;
		}
//		Assert.assertTrue(hadParant);


	}

}
