import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */

public class ApplicationDatabaseConnection {

    private static final Logger LOG = Logger.getLogger(ApplicationDatabaseConnection.class.getName());
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static String prop_file_path = "";
    private static String ebs_database = "";
    private static String ebs_dbuser = "";
    private static String ebs_dbpassword = "";
    public static String propfilepath = "";
    private static String logFile = "";
    private static String vlogFile = "";
    private Handler fileHandler  = null;
    private Handler consoleHandler = null;
    private Formatter simpleFormatter = null;
    private static String entrpr_name = "";
    private static String gateway_server = "";
    private static String username = "";
    private static String password = "";
    private static String sieb_database = "";
    private static String sieb_username = "";
    private static String sieb_password = "";
    //private static Logger LOG;
    
    public ApplicationDatabaseConnection() {
        initializePropertyValues();
        //LOG = vLOG;
    }
    
    
    private void initializePropertyValues(){        
        LOG.log(Level.INFO,"Initializing connection properties .... ");
        if (OS.contains("nix") || OS.contains("nux")) {
                prop_file_path = "/usr/app/siebel/intg/intg.properties";
                vlogFile = "nix_connect_logfile";
        } else if (OS.contains("win")) {
                prop_file_path =  "C:\\temp\\intg\\intg.properties";
                vlogFile = "win_connect_logfile";
        }
        Properties prop = new Properties();
        FileInputStream input;        
        try {
            input = new FileInputStream(prop_file_path);
            prop.load(input);
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "initializePropertyValues:FileNotFoundException", ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "initializePropertyValues:IOException", ex);
        }
        
        ebs_database = prop.getProperty("ebs_database");
        ebs_dbuser = prop.getProperty("ebs_dbuser");
        ebs_dbpassword = prop.getProperty("ebs_dbpassword");
        entrpr_name = prop.getProperty("entrpr_name");
        this.gateway_server = prop.getProperty("gateway_server");
        this.username = prop.getProperty("username");
        this.password = prop.getProperty("password");
        sieb_database = prop.getProperty("sieb_database");
        sieb_username = prop.getProperty("sieb_dbuser");
        sieb_password = prop.getProperty("sieb_dbpassword");
        LOG.log(Level.INFO, "Values are entrpr_name:{0},gateway_server:{1},username:{2},password{3}", new String[]{entrpr_name, gateway_server,username,password});
        LOG.log(Level.INFO, "Siebel database:{0},sieb_username:{1},sieb_password:{2}", new String[]{sieb_database,sieb_username,sieb_password});
        logFile = prop.getProperty(vlogFile);
        LOG.log(Level.INFO, "EBS database:{0},username:{1},password{2}", new String[]{ebs_database, ebs_dbuser,ebs_dbpassword});
    }
    
    public Connection connectToEBSDatabase(){        
        Connection connection = null;             
        try {                
            LOG.info("Connection to EBS begin ....");
            try{
                Class.forName("oracle.jdbc.driver.OracleDriver"); 
            }catch (ClassNotFoundException e) {
		LOG.log(Level.SEVERE,"Error in driver class:",e);
            }            
            LOG.info("Driver found ....");
            LOG.log(Level.INFO, "Values ..{0}:{1}:{2}", new Object[]{ebs_database, ebs_dbuser, ebs_dbpassword});
            connection = DriverManager.getConnection(ebs_database, ebs_dbuser, ebs_dbpassword);
            LOG.info("Connected");
            return connection;
        }catch (SQLException e) {
            LOG.log(Level.SEVERE, "ERROR IN connecting To EBS Database Method:",e);
        }
        
        return connection;
    }
    
    public Connection connectToSiebelDatabase(){
        Connection connection = null;             
        try {                
            LOG.info("Connection to Siebel begin ....");
            try{
                Class.forName("oracle.jdbc.driver.OracleDriver"); 
            }catch (ClassNotFoundException e) {
		LOG.log(Level.SEVERE,"Error in driver class:",e);
            }            
            LOG.info("Driver found ....");
            LOG.log(Level.INFO, "Values ..{0}:{1}:{2}", new Object[]{sieb_database, sieb_username, sieb_password});
            connection = DriverManager.getConnection(sieb_database, sieb_username, sieb_password);
            LOG.info("Connected");
            return connection;
        }catch (SQLException e) {
            LOG.log(Level.SEVERE, "ERROR IN connecting To Siebel Database Method:",e);
        }
        
        return connection;
    }
    
    public static void main(String[] args) throws SQLException{
        Logger LOG = Logger.getLogger(ApplicationDatabaseConnection.class.getName());
        ApplicationDatabaseConnection adc = new ApplicationDatabaseConnection();
        Connection con = adc.connectToSiebelDatabase();
        con.close();
    }
}
