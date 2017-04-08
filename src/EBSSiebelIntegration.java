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
import com.siebel.data.SiebelPropertySet;
import com.siebel.eai.SiebelBusinessService;
import com.siebel.eai.SiebelBusinessServiceException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EBSSiebelIntegration extends SiebelBusinessService{    
  private static final Logger LOG = Logger.getLogger(EBSOnHandQty.class.getName());
    private static final Properties propFile = new Properties();
    private static InputStream inputObj = null;
    private String ECSCode = null;
    private String Item_Id = null;
    private String Org_Id = null;
    private String warehouse_id = null;
    private String item_number = null;
    private BufferedWriter bw = null;
    private String vProcedureWithParameters = null;
    private Connection conn = null;
    private static String return_status = "";
    private static String msg_data = "";
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static String prop_file_path = "";
    private static String dbase = "";
    private static String dbuser = "";
    private static String dbpwd = "";
    public static String propfilepath = "";
    private static InetAddress ip = null;
    private static String hIP = "";
    private static String logFile = "";
    private static String vlogFile = "";
    private Handler fileHandler = null;
    private Handler consoleHandler = null;
    private Formatter simpleFormatter = null;
    private int customerTrxInvoiceNumber;
    private String ebsuserid;
    private String ebsuserresp;
    private String ebsrespapplid;
    private String ebspolicycontext;
    private String ebsbatchsourceid;
    private String ebstrxheaderid;
    private FunctionLogging fl = new FunctionLogging();
    private String custLogFile;
    private String itemLogFile;
    private String onHandLogFile;
    private StringWriter errors = new StringWriter();
    private File logfile;
    private String strx_id;
    private static String main_strx_id;
    private String invoiceId;
    private String customerNumber;
    private String customerName;
    private String customerClassification;
    
    
@Override
public void doInvokeMethod(String MethodName, SiebelPropertySet input, SiebelPropertySet output) throws SiebelBusinessServiceException {
        String err_msg;
        try {
            ip = InetAddress.getLocalHost();
            hIP = ip.getHostAddress();
            if (OS.contains("nix") || OS.contains("nux")) {
                propfilepath = EBSSiebelIntegration.prop_file_path = "/usr/app/siebel/intg/intg.properties";
                vlogFile = "nix_logfile";
                this.custLogFile = "/usr/app/siebel/intg/log/doInvoke";
                this.itemLogFile = "/usr/app/siebel/intg/log/ebsitem";
                this.onHandLogFile = "/usr/app/siebel/intg/log/onHand";
            } else if (OS.contains("win")) {
                propfilepath = "C:\\temp\\intg\\intg.properties";
                vlogFile = "win_logfile";
                this.custLogFile = "C:\\temp\\intg\\log\\doInvoke";
                this.itemLogFile = "C:\\temp\\intg\\log\\ebsitem";
                this.onHandLogFile = "C:\\temp\\intg\\log\\onHand";
            }
            Date date = new Date();
            MyLogging.log(Level.INFO, "==============EBSSiebelIntegration===================");
            SimpleDateFormat app = new SimpleDateFormat("dd-MM-yyyy");
            String dateApp = app.format(date);
            this.custLogFile = this.custLogFile + dateApp + ".log";
            this.itemLogFile = this.itemLogFile + dateApp + ".log";
            this.onHandLogFile = this.onHandLogFile + dateApp + ".log";
            this.logfile = new File(this.custLogFile);
            this.getProperties();
            MyLogging.log(Level.INFO, "Properties retreived....");
        }
        catch (IOException ie) {
            MyLogging.log(Level.SEVERE, "Error in doInvoke", ie);
            ie.printStackTrace(new PrintWriter(this.errors));
            err_msg = this.errors.toString();
            this.fl.writeToLog(this.logfile, this.custLogFile, "Error in doInvoke:" + err_msg);
        }
        
        if (MethodName.equalsIgnoreCase("CallOnHandQty")) {
            MyLogging.log(Level.INFO, "CallOnHandQty Start");
            LOG.info("CallOnHandQty Start");
            MyLogging.log(Level.INFO, "custLogFile:{0}" + this.custLogFile);
            this.fl.writeToLog(this.logfile, this.custLogFile, "CallOnHandQty Start");
            this.Item_Id = input.getProperty("item_id");
            this.item_number = input.getProperty("item_number");
            this.warehouse_id = input.getProperty("warehouse_id");
            MyLogging.log(Level.INFO, "Item_Id: {0}"+ this.Item_Id);
            MyLogging.log(Level.INFO, "item_number: {0}"+ this.item_number);
            this.fl.writeToLog(this.logfile, this.custLogFile, "Item_Id:" + this.Item_Id);
            MyLogging.log(Level.INFO, "warehouse_id: {0}"+ this.warehouse_id);
            this.fl.writeToLog(this.logfile, this.custLogFile, "Org_Id:" + this.Org_Id);
            try {
                this.fl.writeToLog(this.logfile, this.custLogFile, "Calling :EBSOnHandQty");
                EBSOnHandQty ehq = new EBSOnHandQty(this.Item_Id, this.Org_Id, this.onHandLogFile);
                this.fl.writeToLog(this.logfile, this.custLogFile, "Calling :callViewQuery");
                ehq.callViewQuery(this.Item_Id, this.item_number,this.warehouse_id);
            }
            catch (Exception e) {
                MyLogging.log(Level.SEVERE, "ERROR IN CallOnHandQty Method:", new RuntimeException("Error"));
                e.printStackTrace(new PrintWriter(this.errors));
                err_msg = this.errors.toString();
                this.fl.writeToLog(this.logfile, this.custLogFile, "Error in doInvoke:" + err_msg);
            }            
            MyLogging.log(Level.INFO, "CallOnHandQty End");
            this.fl.writeToLog(this.logfile, this.custLogFile, "CallOnHandQty End");
        }
        
        if (MethodName.equalsIgnoreCase("CreateInvoice")) {
            MyLogging.log(Level.INFO, "METHOD:==CreateInvoice=");                            
            try{
            Connection ebsDBConn = ApplicationsConnection.connectToEBSDatabase();
            SiebelDataBean ciSdbObj = ApplicationsConnection.connectSiebelServer();
            
            CallableStatement cs = null;
            
                MyLogging.log(Level.INFO, "In CreateInvoice");
                MyLogging.log(Level.INFO, "ebsuserid:{0}", this.ebsuserid);
                MyLogging.log(Level.INFO, "ebsuserresp:{0}", this.ebsuserresp);
                MyLogging.log(Level.INFO, "ebsrespapplid:{0}", this.ebsrespapplid);
        
                String cust_trx_type_id = "";
                String custAcctSiteId = "";
                MyLogging.log(Level.INFO, "1..");
                String Sebsuserid = this.ebsuserid;
                String Sebsuserresp = this.ebsuserresp;
                
                int ebscustomerid = Integer.parseInt(input.getProperty("ebs_customer_id"));
                MyLogging.log(Level.INFO, "2..");
                MyLogging.log(Level.INFO, "ebscustomerid:{0}" + ebscustomerid);
                String invoice_curr = input.getProperty("invoice_currency");
                MyLogging.log(Level.INFO, "invoice_curr:{0}" + invoice_curr);
                String order_id = input.getProperty("order_id");
                MyLogging.log(Level.INFO, "order_id:{0}" + order_id);
                String quote_id = input.getProperty("quote_id");
                MyLogging.log(Level.INFO, "quote_id:{0}" + quote_id);
                String custterm = input.getProperty("ebs_customer_term");
                MyLogging.log(Level.INFO, "custterm:{0}" + custterm);
                String custtrxtype = input.getProperty("ebs_customer_trx_type");
                MyLogging.log(Level.INFO, "custtrxtype:{0}" + custtrxtype);
                String salespersonid = input.getProperty("sales_person_id");
                MyLogging.log(Level.INFO, "salespersonid:{0}" + salespersonid);
                EBSData ed = new EBSData(ebsDBConn);
                MyLogging.log(Level.INFO, "3..");
                
                cust_trx_type_id = ed.getEBSCustTrxTypeId(custtrxtype);
                custAcctSiteId = ed.getCustAccountSite(ebscustomerid);
                MyLogging.log(Level.INFO, "4..");
        
                EBSInvoiceCreation eic = new EBSInvoiceCreation(Integer.parseInt(Sebsuserid), Integer.parseInt(Sebsuserresp), Integer.parseInt(this.ebsrespapplid),Integer.parseInt(this.ebspolicycontext),Integer.parseInt(this.ebsbatchsourceid),Integer.parseInt(this.ebstrxheaderid),ciSdbObj,ebsDBConn);
                String hdr = eic.createInvoiceSQLScriptHeader();
                String invhdr = eic.createInvoiceHeader(ebscustomerid, Integer.parseInt(cust_trx_type_id), Integer.parseInt(salespersonid),Integer.parseInt(custAcctSiteId),invoice_curr);
        
                String bdy = "";
                if (!quote_id.isEmpty()){
                    MyLogging.log(Level.INFO, "Quote");
                    bdy = eic.createInvoiceQuoteItemsBody(quote_id, custterm);
                }else if (!order_id.isEmpty()){
                    MyLogging.log(Level.INFO, "Order");
                    bdy = eic.createInvoiceOrderItemsBody(order_id, custterm);
                }
                
                String ftr = eic.createInvoiceSQLScriptFooter();
                String sqlSCript = hdr + "\n" + invhdr + "\n" + bdy + "\n" + ftr;
                MyLogging.log(Level.INFO, "Script: {0}" + sqlSCript);
                MyLogging.log(Level.INFO, "Calling sql script statement .....");
        
                cs = ebsDBConn.prepareCall(sqlSCript);
                cs.registerOutParameter(1, 4);
                cs.execute();
                MyLogging.log(Level.INFO, "Call Done");
                int trx_id = cs.getInt(1);
                MyLogging.log(Level.INFO, "Int trx_id: {0}" + trx_id);
                MyLogging.log(Level.INFO, "trx_id: {0}" + Integer.toString(trx_id));
                output.setProperty("customer_invoice_number", Integer.toString(trx_id));
                ciSdbObj.logoff();
                if (cs != null) {
                    try{
                        cs.close();
                    }catch (SQLException ex){
                        ex.printStackTrace(new PrintWriter(errors));         
                        MyLogging.log(Level.SEVERE, "SQLException: ERROR", errors.toString());
                    }
                }
                if (ebsDBConn != null) {
                    try{
                        ebsDBConn.close();
                    }catch (SQLException ex){
                        ex.printStackTrace(new PrintWriter(errors));         
                        MyLogging.log(Level.SEVERE, "SQLException: ERROR"+ errors.toString());
                    }
                }     
            }catch (SQLException ex){ 
                ex.printStackTrace(new PrintWriter(errors));         
                MyLogging.log(Level.SEVERE, "CreateInvoice: ERROR"+ errors.toString());
            }catch (IOException ex){
                ex.printStackTrace(new PrintWriter(errors));  
                MyLogging.log(Level.SEVERE, "CreateInvoice: ERROR"+ errors.toString());
            }catch (Exception ex){
                ex.printStackTrace(new PrintWriter(errors));  
                MyLogging.log(Level.SEVERE, "CreateInvoice: ERROR"+ errors.toString());
            }
    }
        
    if(MethodName.equalsIgnoreCase("GetInvoiceReceipts")){
        MyLogging.log(Level.INFO, "METHOD:==GetInvoiceReceipts=");
        String invoiceNumber  = input.getProperty("invoiceNumber");
        
    }
     
    if (MethodName.equalsIgnoreCase("CreateCustomer")) {
            MyLogging.log(Level.INFO, "METHOD:==CreateCustomer=");
            String Sebsuserid = this.ebsuserid;
            String Sebsuserresp = this.ebsuserresp;
            String Sebrespapplid = this.ebsrespapplid;
            String Sebtrxheaderid = this.ebstrxheaderid;
            ApplicationDatabaseConnection adc = new ApplicationDatabaseConnection();
            Connection conntn = adc.connectToEBSDatabase();
            CallableStatement cs = null;
            this.customerName = input.getProperty("customerName");
            this.customerNumber = input.getProperty("customerNumber");
            this.customerClassification = input.getProperty("customerClassification");
            MyLogging.log(Level.INFO, "customerName: {0}" + this.customerName);
            MyLogging.log(Level.INFO, "customerNumber: {0}" + this.customerNumber);
            MyLogging.log(Level.INFO, "customerClassification: {0}" + this.customerClassification);
            EBSCustomerCreation ecc = new EBSCustomerCreation(Integer.parseInt(Sebsuserid), Integer.parseInt(Sebsuserresp),Integer.parseInt(Sebrespapplid),Integer.parseInt(Sebtrxheaderid));
            String hdr = ecc.createCustomerSQLHeader(this.customerName, this.customerNumber, this.customerClassification);
            String bdy = ecc.createCustomerSQLBody();
            String sqlSCript = hdr + "\n" + bdy;
            MyLogging.log(Level.INFO, "Script: {0}" + sqlSCript);
            MyLogging.log(Level.INFO, "Calling sql script statement .....");
            try {
                cs = conntn.prepareCall(sqlSCript);
                cs.registerOutParameter(1, 4);
                cs.registerOutParameter(2, 12);
                cs.registerOutParameter(3, 4);
                cs.registerOutParameter(4, 12);
                cs.registerOutParameter(5, 4);
                cs.registerOutParameter(6, 12);
                cs.execute();
                MyLogging.log(Level.INFO, "Call Done");
                int x_cust_account_id = cs.getInt(1);
                String x_account_number = cs.getString(2);
                int x_party_id = cs.getInt(3);
                String x_party_number = cs.getString(4);
                int x_profile_id = cs.getInt(5);
                String x_return_status = cs.getString(6);
                MyLogging.log(Level.INFO, "x_cust_account_id: {0}" + x_cust_account_id);
                MyLogging.log(Level.INFO, "x_account_number: {0}" + x_account_number);
                MyLogging.log(Level.INFO, "x_party_id: {0}" + x_party_id);
                MyLogging.log(Level.INFO, "x_party_number: {0}" + x_party_number);
                MyLogging.log(Level.INFO, "x_profile_id: {0}" + x_profile_id);
                MyLogging.log(Level.INFO, "x_return_status: {0}" + x_return_status);
                if (x_return_status.equalsIgnoreCase("S")) {
                    output.setProperty("ebsCustomerId", Integer.toString(x_cust_account_id));
                    output.setProperty("ebsCustomerNumber", x_account_number);
                    output.setProperty("ebsPartyId", Integer.toString(x_party_id));
                    output.setProperty("ebsPartyNumber", x_party_number);
                    output.setProperty("ebsProfileId", Integer.toString(x_profile_id));
                    output.setProperty("return_status", "Success");
                } else if (x_return_status.equalsIgnoreCase("E")) {
                    output.setProperty("return_status", "Error");
                }
            }
            catch (SQLException ex) {
                ex.printStackTrace(new PrintWriter(errors));                                                            
                MyLogging.log(Level.SEVERE, "CREATE CUSTOMER ERROR: .....", errors.toString());
            }
            finally {
                if (cs != null) {
                    try {
                        cs.close();
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace(new PrintWriter(errors));
                        MyLogging.log(Level.SEVERE, "Error in closing connection cs", errors.toString());                                                
                    }
                }
                if (conntn != null) {
                    try {
                        conntn.close();
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace(new PrintWriter(errors));
                        MyLogging.log(Level.SEVERE, "Error in closing connection conn", errors.toString());                                                
                    }
                }
            }
        }
        
    if (MethodName.equalsIgnoreCase("ItemExists")) {
            MyLogging.log(Level.INFO, "IN ITEM EXIST CONDITION");
            String item_id = input.getProperty("itemId");
            String item_org_id = input.getProperty("itemOrgId");
            EBSItem ei = new EBSItem(this.itemLogFile);
            try {
                boolean itm = ei.itemExists(Integer.parseInt(item_id), Integer.parseInt(item_org_id));
                if (itm) {
                    MyLogging.log(Level.INFO, "Item exist");
                    output.setProperty("ITEM_EXIST", "Y");
                }
            }
            catch (SQLException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "SQLEXCEPTION ERROR: " + errors.toString());
            }
        }
        output.setProperty("MSG_DATA", msg_data);
        output.setProperty("RETURN_STATUS", "Success");
        for (Handler h : LOG.getHandlers()) {
            h.close();
    }
        
    if(MethodName.equalsIgnoreCase("GetItemLocator")){
        MyLogging.log(Level.INFO, "In GetItemLocator");
        MyLogging.log(Level.INFO, "Item Number is: "+input.getProperty("item_number"));
        EBSOnHandQty ehq = new EBSOnHandQty();
        SiebelPropertySet itemLocatorPS = new SiebelPropertySet();
        try{
            itemLocatorPS = ehq.getItemLocator(input.getProperty("item_number"));        
            output.addChild(itemLocatorPS.copy());
            output.setProperty("RETURN_STATUS", "Success");
        }catch(Exception e){
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.INFO, "Error in GetItemLocator: "+errors.toString());
            output.setProperty("MSG_DATA", errors.toString());
            output.setProperty("RETURN_STATUS", "Failure");
        }
    }        
}
    
    

    
    private void setInvoiceId(String inoviceId) {
        this.invoiceId = inoviceId;
        
    }

    private String getInvoiceId() {
        return this.invoiceId;
    }

    public String getThePropFilePath() {
        String pf = "";
        try {
            ip = InetAddress.getLocalHost();
        }
        catch (UnknownHostException ex) {
            MyLogging.log(Level.WARNING, "Error in getting IP", ex);
        }
        hIP = ip.getHostAddress();
        if (OS.contains("nix") || OS.contains("nux")) {
            pf = "/usr/apps/siebel/intg/intg.properties";
        } else if (OS.contains("win")) {
            pf = "C:\\temp\\intg\\intg.properties";
        }
        return pf;
    }

    private void getProperties() throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        MyLogging.log(Level.INFO, "propfilepath is {0}", propfilepath);
        FileInputStream input = new FileInputStream(propfilepath);
        prop.load(input);
        dbase = prop.getProperty("database");
        dbuser = prop.getProperty("dbuser");
        dbpwd = prop.getProperty("dbpassword");
        logFile = prop.getProperty(vlogFile);
        MyLogging.log(Level.INFO, "dbase is {0}", dbase);
        MyLogging.log(Level.INFO, "dbuser is {0}", dbuser);
        MyLogging.log(Level.INFO, "dbpwd is {0}", dbpwd);
        MyLogging.log(Level.INFO, "logFile is {0}", logFile);
        this.ebsuserid = prop.getProperty("ebsuserid");
        this.ebsuserresp = prop.getProperty("ebsuserresp");
        this.ebsrespapplid = prop.getProperty("ebsrespapplid");
        this.ebspolicycontext = prop.getProperty("ebspolicycontext");
        this.ebsbatchsourceid = prop.getProperty("ebsbatchsourceid");
        this.ebstrxheaderid = prop.getProperty("ebstrxheaderid");
        MyLogging.log(Level.INFO, "ebsuserid is:", this.ebsuserid);
        MyLogging.log(Level.INFO, "ebsuserresp is:", this.ebsuserresp);
        MyLogging.log(Level.INFO, "ebsrespapplid is:", this.ebsrespapplid);
        MyLogging.log(Level.INFO, "ebspolicycontext is:", this.ebspolicycontext);
        MyLogging.log(Level.INFO, "ebsbatchsourceid is:", this.ebsbatchsourceid);
        MyLogging.log(Level.INFO, "ebstrxheaderid is:", this.ebstrxheaderid);        
    }

    public static void main(String[] args) {
        try {
            SiebelPropertySet input = new SiebelPropertySet();
            SiebelPropertySet output = new SiebelPropertySet();
            EBSSiebelIntegration ns = new EBSSiebelIntegration();
            
            input.setProperty("invoice_currency","NGN");                
            input.setProperty("order_id","");            
            input.setProperty("quote_id","1-150NH");                
            input.setProperty("ebs_customer_term","NET 30");            
            input.setProperty("ebs_customer_trx_type","VEHICLE NONDEALERS");                
            input.setProperty("sales_person_id","224");
            input.setProperty("ebs_customer_id","20107");
            ns.doInvokeMethod("CreateInvoice", input, output);
            /*input.setProperty("item_id", "14");
            input.setProperty("item_number","131628");
            input.setProperty("warehouse_id", "123");
            ns.doInvokeMethod("GetItemLocator", input, output);*/
            //ns.doInvokeMethod("CallOnHandQty", input, output);
        }
        catch (Exception ex) {
            MyLogging.log(Level.WARNING, "In main", ex);
        }
    }
}