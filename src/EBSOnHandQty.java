/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.siebel.data.SiebelPropertySet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author SAP Training
 */



public class EBSOnHandQty {
    
   public String OnHandQuantity;
  public String Availabletoreserve;
  public String QuantityReserved;
  public String QuantitySuggested;
  public String AvailabletoTransact;
  public String AvailabletoReserve;
  public String Inv_Item_Id;
  public String Org_id;
  private static final Logger LOG = Logger.getLogger(EBSOnHandQty.class.getName());
  private String vLogFile;
  private File logfile;
  private static String logFile;
  private Handler fileHandler = null;
  private Handler consoleHandler = null;
  private Formatter simpleFormatter = null;
  StringWriter errors = new StringWriter();
  
  public EBSOnHandQty(){
      
  }
  public EBSOnHandQty(String Inv_Item_Id, String Org_id, String vLogFile)
  {
    MyLogging.log(Level.INFO, "=============EBSOnHandQty===============");
    
    this.Inv_Item_Id = Inv_Item_Id;
    this.Org_id = Org_id;
    this.vLogFile = vLogFile;
  }
      
  private void initLogging(String vLogFile)
  {
    try
    {
      this.logfile = new File(vLogFile);
      this.consoleHandler = new ConsoleHandler();
      this.fileHandler = new FileHandler(vLogFile);
      this.simpleFormatter = new SimpleFormatter();
      LOG.addHandler(this.consoleHandler);
      LOG.addHandler(this.fileHandler);
      this.fileHandler.setFormatter(this.simpleFormatter);
      this.consoleHandler.setLevel(Level.ALL);
      this.fileHandler.setLevel(Level.ALL);
      LOG.setLevel(Level.ALL);
    }
    catch (IOException ie)
    {
      MyLogging.log(Level.SEVERE, "EBSOnHandQty(initLogging): ERROR", ie);
    }
  }
  
  public void callViewQuery(String item_id, String item_number,String warehouse_id)
  {
    MyLogging.log(Level.INFO, "entering:" + getClass().getName() + "callViewQuery");
    ApplicationDatabaseConnection adc = new ApplicationDatabaseConnection();
    Connection conn = adc.connectToSiebelDatabase();
    String queryString = "SELECT\n      T1.TABLE_ID,\n      T1.CATEGORY,\n      T1.WAREHOUSE_ID,\n      T1.WAREHOUSE,\n      T1.ITEM_ID,\n      T1.ITEM,\n      T1.ITEM_DESCRIPTION,\n      T1.PRIMARY_QUANTITY,\n      T1.ONHAND_STATUS,\n      T1.PRIMARY_UOM,\n      T1.CONTAINERIZED_FLAG,\n      T1.SUBINVENTORY,\n      T1.REVISION,\n      T1.LPN,\n      T1.PARENT_LPN,\n      T1.SERIAL,\n      T1.LOT,\n      T1.SECONDARY_UOM,\n      T1.SECONDARY_QUANTITY,\n      T1.LOCATOR,\n      T1.SNAPSHOT_DATE\n   FROM \n       INV.MTL_ONHAND_SYNC_V2@SIEBEL_TO_EBS T1\n   WHERE \n      (T1.ITEM = '" + item_number + "' AND T1.WAREHOUSE_ID = '" + warehouse_id + "')";
    
    MyLogging.log(Level.INFO, "OnHandQuery is {0}:" + queryString);
    try
    {
      Statement statement = conn.createStatement();
      Statement deleteStatement = conn.createStatement();
      Statement insertStatement = conn.createStatement();
      ResultSet rs = statement.executeQuery(queryString);
      if (rs.isBeforeFirst())
      {
        MyLogging.log(Level.INFO, "DELETE QUERY IS ....DELETE FROM SIEBEL.EBS_CUST_ONHAND_QTY WHERE ITEM = " + item_number + ")");
        String deleteQuery = "DELETE FROM SIEBEL.EBS_CUST_ONHAND_QTY WHERE ITEM = '" + item_number + "'";
        int dltd_amnt = deleteStatement.executeUpdate(deleteQuery);
        conn.commit();
        MyLogging.log(Level.INFO, "Amount deleted is :" + dltd_amnt);
      }
      while (rs.next())
      {
        String tableId = rs.getString("TABLE_ID");
        MyLogging.log(Level.INFO, "TABLE_ID :{0}" + tableId);
        String category = rs.getString("CATEGORY");
        MyLogging.log(Level.INFO, "category :{0}" + category);
        String the_warehouse_id = rs.getString("WAREHOUSE_ID");
        MyLogging.log(Level.INFO, "warehouse_id :{0}" + the_warehouse_id);
        String warehouse = rs.getString("WAREHOUSE");
        MyLogging.log(Level.INFO, "warehouse :{0}" + warehouse);
        String v_item_id = rs.getString("ITEM_ID");
        MyLogging.log(Level.INFO, "item_id :{0}" + v_item_id);
        String item = rs.getString("ITEM");
        MyLogging.log(Level.INFO, "item :{0}" + item);
        String item_description = rs.getString("ITEM_DESCRIPTION");
        MyLogging.log(Level.INFO, "item_description :{0}" + item_description);
        String primary_quantity = rs.getString("PRIMARY_QUANTITY");
        MyLogging.log(Level.INFO, "primary_quantity :{0}" + primary_quantity);
        String onhand_status = rs.getString("ONHAND_STATUS");
        MyLogging.log(Level.INFO, "onhand_status :{0}" + onhand_status);
        
        String insertQuery = "INSERT INTO EBS_CUST_ONHAND_QTY (TABLE_ID, CATEGORY, WAREHOUSE_ID, WAREHOUSE,ITEM_ID,ITEM,ITEM_DESCRIPTION,PRIMARY_QUANTITY,ONHAND_STATUS) VALUES\n('" + tableId + "','" + category + "','" + the_warehouse_id + "','" + warehouse + "','" + v_item_id + "','" + item + "','" + item_description + "','" + primary_quantity + "','" + onhand_status + "')";
        
        MyLogging.log(Level.INFO, "insertQuery :{0}" + insertQuery);
        insertStatement.executeUpdate(insertQuery);
        conn.commit();
      }
      if (conn != null) {
        try
        {
          conn.close();
        }
        catch (SQLException ex)
        {
          MyLogging.log(Level.SEVERE, "Close connection error", ex);
        }
      }
      
      MyLogging.log(Level.INFO, "Exiting : " + getClass().getName() + ":callViewQuery");
    }
    catch (SQLException ex)
    {      
      ex.printStackTrace(new PrintWriter(errors));
      MyLogging.log(Level.SEVERE, "SQLEXCEPTION ERROR: " + errors.toString());
    }
    finally
    {
      if (conn != null) {
        try
        {
          conn.close();
        }
        catch (SQLException ex)
        {
          ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "SQLEXCEPTION ERROR: " + errors.toString());
        }
      }
    }
  }
  
  public SiebelPropertySet getItemLocator(String item_number)
  {
    MyLogging.log(Level.INFO, "entering:" + getClass().getName() + "getItemLocator");
    ApplicationDatabaseConnection adc = new ApplicationDatabaseConnection();
    Connection conn = adc.connectToSiebelDatabase();
    String queryString = "SELECT\n      T1.TABLE_ID,\n      T1.CATEGORY,\n      T1.WAREHOUSE_ID,\n      T1.WAREHOUSE,\n      T1.ITEM_ID,\n      T1.ITEM,\n      T1.ITEM_DESCRIPTION,\n      T1.PRIMARY_QUANTITY,\n      T1.ONHAND_STATUS,\n      T1.PRIMARY_UOM,\n      T1.CONTAINERIZED_FLAG,\n      T1.SUBINVENTORY,\n      T1.REVISION,\n      T1.LPN,\n      T1.PARENT_LPN,\n      T1.SERIAL,\n      T1.LOT,\n      T1.SECONDARY_UOM,\n      T1.SECONDARY_QUANTITY,\n      T1.LOCATOR,\n      T1.SNAPSHOT_DATE\n   FROM \n       INV.MTL_ONHAND_SYNC_V2@SIEBEL_TO_EBS T1\n   WHERE \n      (T1.ITEM = '" + item_number + "')";
    
    MyLogging.log(Level.INFO, "getItemLocator is {0}:" + queryString);
    SiebelPropertySet ps = new SiebelPropertySet();
    SiebelPropertySet psValues = new SiebelPropertySet();
    try
    {
      Statement statement = conn.createStatement();
     // Statement deleteStatement = conn.createStatement();
     // Statement insertStatement = conn.createStatement();
      ResultSet rs = statement.executeQuery(queryString);
      
      while (rs.next())
      {                
        ps.reset();
        String warehouse_id = rs.getString("WAREHOUSE_ID");
        MyLogging.log(Level.INFO, "warehouse_id :{0}" + warehouse_id);
        psValues.setProperty("warehouse_id", warehouse_id);                
        String item = rs.getString("ITEM");
        MyLogging.log(Level.INFO, "item :{0}" + item);
        psValues.setProperty("item", item);
        String item_description = rs.getString("ITEM_DESCRIPTION");
        MyLogging.log(Level.INFO, "item_description :{0}" + item_description);
        psValues.setProperty("item_description", item_description);
        String primary_quantity = rs.getString("PRIMARY_QUANTITY");
        MyLogging.log(Level.INFO, "primary_quantity :{0}" + primary_quantity);
        psValues.setProperty("primary_quantity", primary_quantity);
        String locator = rs.getString("LOCATOR");
        MyLogging.log(Level.INFO, "locator :{0}" + locator);
        psValues.setProperty("locator", locator);
        ps.addChild(psValues);        
      }
      if (conn != null) {
        try
        {
          conn.close();
        }
        catch (SQLException ex)
        {
          MyLogging.log(Level.SEVERE, "Close connection error", ex);
        }
      }
      
      MyLogging.log(Level.INFO, "Exiting : " + getClass().getName() + ":callViewQuery");
    }
    catch (SQLException ex)
    {      
      ex.printStackTrace(new PrintWriter(errors));
      MyLogging.log(Level.SEVERE, "SQLEXCEPTION ERROR: " + errors.toString());
    }
    finally
    {
      if (conn != null) {
        try
        {
          conn.close();
        }
        catch (SQLException ex)
        {
          ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "SQLEXCEPTION ERROR: " + errors.toString());
        }
      }
    }
    return ps;
  }
  
  
  public static void main(String[] args)
  {
    EBSOnHandQty ohv = new EBSOnHandQty("13002", "124", "C:\\temp\\intg\\log\\OnHandLog.log");
    //ohv.callViewQuery("14","131628", "123");
    SiebelPropertySet ps =  ohv.getItemLocator("131628");
    if(ps.getChildCount()>0){                
        for(int i = 0; i < ps.getChildCount(); i++){
            SiebelPropertySet sps =  ps.getChild(i);            
            MyLogging.log(Level.INFO, "warehouse_id :{0}" + sps.getProperty("warehouse_id")); 
            MyLogging.log(Level.INFO, "item :{0}" + sps.getProperty("item")); 
            MyLogging.log(Level.INFO, "item_description :{0}" + sps.getProperty("item_description")); 
            MyLogging.log(Level.INFO, "primary_quantity :{0}" + sps.getProperty("primary_quantity")); 
            MyLogging.log(Level.INFO, "locator :{0}" + sps.getProperty("locator"));             
        }
        
    }
  }
}
