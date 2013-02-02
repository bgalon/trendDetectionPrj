package il.ac.technion.geoinfo.datalayer.graph;

import static org.junit.Assert.*;

import java.util.HashMap;

import il.ac.technion.geoinfo.datalayer.SSN;
import il.ac.technion.geoinfo.datalayer.SpatialStorage;
import il.ac.technion.geoinfo.domain.graphImpl.SpatialNode;
import il.ac.technion.geoinfo.domain.graphImpl.SpatialRealtion;
import il.ac.technion.geoinfo.domain.interfaces.ISpatialEntity;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.test.ImpermanentGraphDatabase;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
 
public class SpatialStorageGraphImplTest {
	
	static final String POLY_1 = "POLYGON((0 0, 0 5, 5 5 , 5 0, 0 0 ))";
	static final String POLY_2 = "POLYGON ((0 5, 5 5, 5 10, 0 10, 0 5))";
	
	static final String LINESTRING_1 = "LINESTRING(-1 0, 7 11)";
	static final String LINESTRING_2 = "LINESTRING(-1 -1, 7 15)";
	
	private GraphDatabaseService testDb;
	
	@Before
	public void createTestDb(){
//		testDb = new ImpermanentGraphDatabase();
		testDb = new RestGraphDatabase("http://ec2-176-34-83-186.eu-west-1.compute.amazonaws.com:7474/db/data");
	}
	
	@After
	public void deleteTestDb(){
		testDb.shutdown();
	}
	
	@Test
	public void test1(){
		Transaction tx = testDb.beginTx();
		try{
			Node root = testDb.getReferenceNode();
			Node n1 = testDb.createNode();
			Node n2 = testDb.createNode();
			root.createRelationshipTo(n1, SpatialRealtion.within);
			n1.createRelationshipTo(n2, SpatialRealtion.within);
			tx.success();
		}finally{
			tx.finish();
		}
		
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
		
		ISpatialEntity l1 = ss.addSpatialEntity(line1, new HashMap<String,String>());
		ISpatialEntity l2 = ss.addSpatialEntity(line2, new HashMap<String,String>());
		ss.closeLevel();
		ISpatialEntity p1 = ss.addSpatialEntity(poly1, new HashMap<String,String>());
		ISpatialEntity p2 = ss.addSpatialEntity(poly2, new HashMap<String,String>());

		boolean hadParant = false;
		for (ISpatialEntity parnt:l1.getParants()){
			if (parnt.equals(p1)) hadParant = true;
		}
//		Assert.assertTrue(hadParant);
		
		
	}

}
