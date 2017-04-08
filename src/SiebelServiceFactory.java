/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */

import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SiebelServiceFactory {

    private static final Logger LOG = Logger.getLogger(SiebelServiceFactory.class.getName());
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private final String gateway_port = "2321";
    private String prop_file_path;
    private String entrpr_name = "";
    private String gateway_server = "";
    private String username = "";
    private String password = "";
    private String sieb_database = "";
    private String sieb_username = "";
    private String sieb_password = "";
    
    public SiebelServiceFactory()throws FileNotFoundException,IOException {  
        initializePropertyValues();
    }
    
    private void initializePropertyValues() throws FileNotFoundException,IOException{
        LOG.log(Level.INFO,"Initializing connection properties .... ");
        if (OS.contains("nix") || OS.contains("nux")) {
                prop_file_path = "/usr/app/siebel/intg/intg.properties";                                
        } else if (OS.contains("win")) {
                prop_file_path =  "C:\\temp\\intg\\intg.properties";                
        }
        Properties prop = new Properties();
        FileInputStream input;        
        input = new FileInputStream(prop_file_path);
        prop.load(input);
        this.entrpr_name = prop.getProperty("entrpr_name");
        this.gateway_server = prop.getProperty("gateway_server");
        this.username = prop.getProperty("username");
        this.password = prop.getProperty("password");
        this.sieb_database = prop.getProperty("sieb_database");
        this.sieb_username = prop.getProperty("sieb_dbuser");
        this.sieb_password = prop.getProperty("sieb_dbpassword");
        LOG.log(Level.INFO, "Values are entrpr_name:{0},gateway_server:{1},username:{2},password{3}", new String[]{entrpr_name, gateway_server,username,password});
        LOG.log(Level.INFO, "Values are sieb_database:{0},sieb_username:{1},sieb_password:{2}", new String[]{sieb_database,sieb_username,sieb_password});
    }
    
    public Connection connectToSiebelDB(){
        Connection connection = null;             
        try {                        
            Class.forName("oracle.jdbc.driver.OracleDriver");
            LOG.info("Siebel Database Connection begin ....");
            LOG.log(Level.INFO, "Values ..{0}:{1}:{2}", new Object[]{sieb_database, sieb_username, sieb_password});
            connection = DriverManager.getConnection(sieb_database, sieb_username, sieb_password);
            LOG.info("Connected");
            return connection;
        }
        catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "ERROR IN connectToSiebelDatabase Method:",e);
        } catch (SQLException ex) {
            Logger.getLogger(SiebelServiceFactory.class.getName()).log(Level.SEVERE, "connectToSiebelDatabase: SQLExeption", ex);
        }
        
        return connection;
    }
    
    public SiebelDataBean ConnectSiebelServer() throws FileNotFoundException, IOException{
        LOG.log(Level.INFO,"Connecting to Siebel .... ");
        SiebelDataBean dataBean = null;
        String connectString = String.format("Siebel://"+gateway_server+":"+gateway_port+"/"+entrpr_name+"/eautoObjMgr_enu");
        LOG.log(Level.INFO,"Connection string is connectString:{0} ",connectString);
        try {
            dataBean = new SiebelDataBean();
            dataBean.login(connectString, username, password, "enu");
            LOG.log(Level.INFO,"Connection SUCCESSFUL");
        }
        catch (SiebelException e) {
            LOG.log(Level.SEVERE, "ERROR IN ConnectSiebelServer Method:",e);
        }
        return dataBean;
    }
    
    public static void main (String[] args){
        try {
            SiebelServiceFactory ssf = new SiebelServiceFactory();
            Connection cn = ssf.connectToSiebelDB();
            //SiebelDataBean sdb = ssf.ConnectSiebelServer();
        }catch (FileNotFoundException fne){
            LOG.log(Level.SEVERE, "FileNotFoundException :", fne);
        }catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error in main Exception :", ex);
        } 
    }
}
