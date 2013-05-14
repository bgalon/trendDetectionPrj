package main;

import com.vividsolutions.jts.geom.Geometry;
import filters.MinimalObjectsCreator;
import il.ac.technion.geoinfo.ssntd.datalayer.SpatialStorage;
import il.ac.technion.geoinfo.ssntd.datalayer.graph.SpatialStorageGraphImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ben
 * Date: 07/05/13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class MainTest {

    private final String dbPath = "./test/db/";
    private SpatialStorage ss;

    private static final String TEST_FILE = "C:\\Users\\Ben\\git\\trendDetectionPrj\\trend-detection-all\\geoEntitiesCreator\\src\\main\\resources\\osmFiles\\test.osm";

    @Before
    public void setUp() throws Exception {
        ss = new SpatialStorageGraphImpl(dbPath);
        System.out.println("test graph path is: " + ((SpatialStorageGraphImpl)ss).getGraphPath());
    }

    @After
    public void tearDown() throws Exception {
        ss.shutdown();
    }

    @Test
    public void readOsmTest() throws Exception {

        Assert.assertTrue("file " + TEST_FILE + " dose not exists", new File(TEST_FILE).exists());
        Map<Geometry, Map<String, String>> mos = new MinimalObjectsCreator().extractMO(new File(
                TEST_FILE));
        for (Map.Entry<Geometry, Map<String, String>> spatialEntity : mos.entrySet()) {
            ss.addSpatialEntity(spatialEntity.getKey(), spatialEntity.getValue());
        }
        ss.closeLevel();
    }
}
