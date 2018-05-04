/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.siebel.data.SiebelDataBean;
import java.sql.Connection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author SAP Training
 */
public class ApplicationsConnectionTest {
    
    public ApplicationsConnectionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of connectToEBSDatabase method, of class ApplicationsConnection.
     */
    @Test
    public void testConnectToEBSDatabase() {
        System.out.println("connectToEBSDatabase");
        Connection expResult = null;
        Connection result = ApplicationsConnection.connectToEBSDatabase();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of connectToSiebelDatabase method, of class ApplicationsConnection.
     */
    @Test
    public void testConnectToSiebelDatabase() {
        System.out.println("connectToSiebelDatabase");
        Connection expResult = null;
        Connection result = ApplicationsConnection.connectToSiebelDatabase();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of connectSiebelServer method, of class ApplicationsConnection.
     */
    @Test
    public void testConnectSiebelServer() throws Exception {
        System.out.println("connectSiebelServer");
        SiebelDataBean expResult = null;
        SiebelDataBean result = ApplicationsConnection.connectSiebelServer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class ApplicationsConnection.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        ApplicationsConnection.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    
    
}
