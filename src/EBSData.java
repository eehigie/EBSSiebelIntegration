


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
//import oracle.jdbc.OracleDriver;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */
public class EBSData {

    //private static final Logger LOGG = Logger.getLogger(EBSData.class.getName());
    //private static Logger LOGG;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private String database = "";
    private String username = "";
    private String password = "";
    private String prop_file_path = "";
    //private Handler fileHandler;
    //private Handler consoleHandler;
    //private String logFile = "";
    private String vlogFile = "";
    //private Formatter simpleFormatter = null;
    private StringWriter errors = new StringWriter();
    private Connection ebsConn;
    
    public EBSData(Connection conn) throws IOException {
        //LOGG = LOG;        
        /*initializePropertyValues();                
        consoleHandler = new ConsoleHandler();
        fileHandler  = new FileHandler(logFile);
        simpleFormatter = new SimpleFormatter();         
        LOGG.addHandler(fileHandler);
        LOGG.addHandler(consoleHandler);
        fileHandler.setFormatter(simpleFormatter);        
        fileHandler.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.ALL);
        LOGG.setLevel(Level.ALL);*/
        this.ebsConn = conn;
    }
    
   
    
   /* private void initializePropertyValues() throws FileNotFoundException,IOException{        
        MyLogging.log(Level.INFO,"Initializing connection properties .... ");
        if (OS.contains("nix") || OS.contains("nux")) {
                prop_file_path = "/usr/app/siebel/intg/intg.properties";
                vlogFile = "ebsdata_nix_logfile";
        } else if (OS.contains("win")) {
                prop_file_path =  "C:\\temp\\intg\\intg.properties";
                vlogFile = "ebsdata_win_logfile";
        }
        Properties prop = new Properties();
        FileInputStream input;        
        input = new FileInputStream(prop_file_path);
        prop.load(input);        
        logFile = prop.getProperty(vlogFile);        
    }*/
           
    public String getEBSTermId(String term_name){
        //Connection conn =null;
        String term_id = "";
        try {
            //ApplicationDatabaseConnection adc = new ApplicationDatabaseConnection();
            //conn = adc.connectToEBSDatabase();                  
            String selectTableSQL = "SELECT TERM_ID FROM APPS.RA_TERMS WHERE NAME = '"+term_name+"'";
            MyLogging.log(Level.INFO, "SELECT STATEMENT:{0}"+ selectTableSQL);
            Statement statement = ebsConn.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                term_id = rs.getString("TERM_ID"); 
                MyLogging.log(Level.INFO, "TERM_ID:{0}"+ term_id);
            }            
        } catch (Exception ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR IN connectToDatabase Method:"+errors.toString());
        } /*finally{
            try {
                if(conn != null ){
                    conn.close();
                }                
                MyLogging.log(Level.INFO, "Connection Closed:getEBSTermId");
            } catch (SQLException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "Error Connection close:getEBSTermId", errors.toString());
            }
        }*/
        //return Integer.parseInt(term_id);
        return term_id;
    }
    
    
    public String getEBSCustTrxTypeId(String trx_type_name){
        MyLogging.log(Level.INFO, "getEBSCustTrxTypeId method ..");
        //Connection conn = null;
        String trxTypeName = "";
        try {
            //ApplicationDatabaseConnection adc = new ApplicationDatabaseConnection();
            //conn = adc.connectToEBSDatabase();
            String selectTableSQL = "SELECT CUST_TRX_TYPE_ID FROM RA_CUST_TRX_TYPES_ALL WHERE NAME = '"+trx_type_name+"'";
            MyLogging.log(Level.INFO, "SELECT STATEMENT:{0}"+ selectTableSQL);
            Statement statement = ebsConn.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                trxTypeName = rs.getString("CUST_TRX_TYPE_ID"); 
                MyLogging.log(Level.INFO, "CUST_TRX_TYPE_ID:{0}"+ trxTypeName);
            }
        } catch (Exception ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR IN connectToDatabase Method:"+errors.toString());
        } /*finally{
            try {
                conn.close();
                MyLogging.log(Level.INFO, "Connection Closed");
            } catch (SQLException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                LOGG.log(Level.SEVERE, "Error Connection close"+ errors.toString());
            }
        }*/
        //return Integer.parseInt(trxTypeName);
        return trxTypeName;
    }
    
    public String getCustAccountSite(int cust_account_id)
    {
        String selectTableSQL = "SELECT cust_acct_site_id, cust_account_id, party_site_id "
                + "from  hz_cust_acct_sites where cust_account_id = '"+cust_account_id+"';";
        String custAcctSiteId = "";
        try{
            Statement statement = ebsConn.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                custAcctSiteId = rs.getString("cust_acct_site_id"); 
                MyLogging.log(Level.INFO, "custAcctSiteId:"+ custAcctSiteId);
            }
        }catch(Exception ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR IN getCustAccountSite Method:"+errors.toString());
        }
        
        return custAcctSiteId;
    }
    
    public static void main(String[] args){
        try {
            //Logger dLOGS = Logger.getLogger(EBSData.class.getName());
            Connection conn = ApplicationsConnection.connectToEBSDatabase();
            EBSData ed = new EBSData(conn);
            String term_id = ed.getEBSTermId("NET 30");
            MyLogging.log(Level.INFO, "TERM_ID:{0}"+ term_id);
            String cust_trx_type_id = ed.getEBSCustTrxTypeId("SPAREPART NONDEALERS");
            MyLogging.log(Level.INFO, "cust_trx_type_id:{0}"+ cust_trx_type_id);
            if(conn!=null){
                conn.close();
            }
        } catch (IOException ex) {
            MyLogging.log(Level.SEVERE, "Error"+ ex);
        } catch (SQLException ex) {
            MyLogging.log(Level.SEVERE, "Error"+ ex);
        } finally{
            
        }
        
    }
}
