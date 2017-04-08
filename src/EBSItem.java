/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */

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


public class EBSItem {
  private static final Logger LOG = Logger.getLogger(EBSItem.class.getName());
  private String vLogFile;
  private File logfile;
  private static String logFile;
  private Handler fileHandler = null;
  private Handler consoleHandler = null;
  private Formatter simpleFormatter = null;
  private StringWriter errors = new StringWriter();
  public EBSItem(String vLogFile)
  {
    this.vLogFile = vLogFile;
    //initLogging(this.vLogFile);
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
        ie.printStackTrace(new PrintWriter(errors));
        MyLogging.log(Level.SEVERE, "EBSItem(initLogging): ERROR", errors.toString());
    }
  }
  
  public boolean itemExists(int itemNumber, int itemOrgId)
    throws SQLException
  {
    ApplicationDatabaseConnection adc = new ApplicationDatabaseConnection();
    Connection conn = adc.connectToEBSDatabase();
    MyLogging.log(Level.INFO, "Connected to EBS Database");
    String sql_query = "SELECT count(INVENTORY_ITEM_ID) AS ITEM_COUNT FROM MTL_SYSTEM_ITEMS_B WHERE ORGANIZATION_ID ='" + itemOrgId + "' AND  INVENTORY_ITEM_ID = '" + itemNumber + "'";
    MyLogging.log(Level.INFO, "sql_query:{0}"+ sql_query);
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql_query);
    
    int nItem_count = 0;
    while (rs.next())
    {
      nItem_count = rs.getInt("ITEM_COUNT");
      MyLogging.log(Level.INFO, "ITEM_COUNT:{0}"+ Integer.valueOf(nItem_count));
    }
    if (!conn.isClosed()) {
      conn.close();
    }
    return nItem_count > 0;
  }
  
  public static void main(String[] args)
  {
    EBSItem ei = new EBSItem("C:\\temp\\intg\\log\\ebsitem.log");
    try
    {
      boolean itm = ei.itemExists(11, 102);
      if (itm) {
        LOG.log(Level.INFO, "Item exist");
      }
    }
    catch (SQLException ex)
    {
      Logger.getLogger(EBSItem.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
