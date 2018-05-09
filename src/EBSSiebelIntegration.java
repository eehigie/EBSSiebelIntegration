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

import java.util.logging.Level;

import com.plexadasi.account.EBSAccount;
import com.plexadasi.ebs.model.BackOrder;
import com.plexadasi.ebs.model.Order;
import com.plexadasi.invoice.InvoiceObject;
import com.plexadasi.invoice.CreateInvoice;
import com.plexadasi.order.SalesOrderInventory;
import com.plexadasi.order.SalesOrder;
import com.plexadasi.order.PurchaseOrder;
import com.plexadasi.order.PurchaseOrderInventory;
import com.siebel.data.SiebelException;
//import item.Items;
import java.net.ConnectException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.List;
//import java.util.logging.Logger;



public class EBSSiebelIntegration extends SiebelBusinessService{        
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
   // private int customerTrxInvoiceNumber;
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
    //private String strx_id;
    //private static String main_strx_id;
    //private String invoiceId;
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
            
            ie.printStackTrace(new PrintWriter(this.errors));
            err_msg = this.errors.toString();
            MyLogging.log(Level.SEVERE, "Error in doInvoke"+ err_msg);
            
        }
        
        if (MethodName.equalsIgnoreCase("CallOnHandQty")) {
            MyLogging.log(Level.INFO, "============CallOnHandQty Start===============");            
            this.Item_Id = input.getProperty("item_id");
            this.item_number = input.getProperty("item_number");
            this.warehouse_id = input.getProperty("warehouse_id");
            MyLogging.log(Level.INFO, "Item_Id: {0}"+ this.Item_Id);
            MyLogging.log(Level.INFO, "item_number: {0}"+ this.item_number);            
            MyLogging.log(Level.INFO, "warehouse_id: {0}"+ this.warehouse_id);            
            try {
                //Connection ebs_conn = ApplicationsConnection.connectToEBSDatabase();
                Connection siebel_conn = ApplicationsConnection.connectToSiebelDatabase();
                MyLogging.log(Level.INFO, "Calling :EBSOnHandQty");
                EBSOnHandQty ehq = new EBSOnHandQty(this.Item_Id, this.Org_Id);
                MyLogging.log(Level.INFO, "Calling :callViewQuery");
                ehq.callViewQuery(this.Item_Id, this.item_number,this.warehouse_id,siebel_conn);
                if(siebel_conn != null){
                    try {
                        siebel_conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace(new PrintWriter(errors));   
                        MyLogging.log(Level.SEVERE, "ERROR IN Closing DB Connection Method:"+ errors.toString());
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace(new PrintWriter(errors));         
                MyLogging.log(Level.SEVERE, "ERROR IN CallOnHandQty Method:"+ errors.toString());                
            }           
            MyLogging.log(Level.INFO, "CallOnHandQty End");            
        }
        
        if (MethodName.equalsIgnoreCase("CheckInvoicePayments")) {
            MyLogging.log(Level.INFO, "============CheckInvoicePayments Start===============");            
            String ebsInvoiceNumber = input.getProperty("EBSInvoiceNumber");                                  
            try {                
                Connection siebel_conn = ApplicationsConnection.connectToSiebelDatabase();
                MyLogging.log(Level.INFO, "Calling :EBSOnHandQty");
                EBSOnHandQty ehq = new EBSOnHandQty(this.Item_Id, this.Org_Id);
                MyLogging.log(Level.INFO, "Calling :callViewQuery");
                ehq.callViewQuery(this.Item_Id, this.item_number,this.warehouse_id,siebel_conn);
                if(siebel_conn != null){
                    try {
                        siebel_conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace(new PrintWriter(errors));   
                        MyLogging.log(Level.SEVERE, "ERROR IN Closing DB Connection Method:"+ errors.toString());
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace(new PrintWriter(errors));         
                MyLogging.log(Level.SEVERE, "ERROR IN CallOnHandQty Method:"+ errors.toString());                
            }           
            MyLogging.log(Level.INFO, "CallOnHandQty End");            
        }
        
        if (MethodName.equalsIgnoreCase("GetQuotePayments")) {
            MyLogging.log(Level.INFO, "============GetQuotePayments Start===============");            
            String invoiceId = input.getProperty("InvoiceId"); 
            String quoteId = input.getProperty("QuoteId");
            MyLogging.log(Level.INFO, "invoiceId::"+invoiceId);
            MyLogging.log(Level.INFO, "quoteId::"+quoteId);
            try {                
                
                SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
                Connection ebs_conn = ApplicationsConnection.connectToEBSDatabase();
                MyLogging.log(Level.INFO, "Calling :GetQuotePayments");
                String RecProc = "{call plxGetReceiptsByInvId(?,?,?,?,?,?,?)}";            
                CallableStatement callablestatement = ebs_conn.prepareCall(RecProc);
                ReceiptsByINVID rcpts = new ReceiptsByINVID();                
                rcpts.setInvNumber(invoiceId);                
                rcpts.setQuoteId(quoteId);
                ResultSet rs = rcpts.getInvReceipts(callablestatement);
                rcpts.processReceipts(rs,sdb);
                if(ebs_conn != null){
                    try {
                        ebs_conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace(new PrintWriter(errors));   
                        MyLogging.log(Level.SEVERE, "ERROR IN Closing DB Connection Method:"+ errors.toString());
                    }
                }
                sdb.logoff();
            }
            catch (Exception e) {
                e.printStackTrace(new PrintWriter(errors));         
                MyLogging.log(Level.SEVERE, "ERROR IN GetQuotePayments Method:"+ errors.toString());                
            }           
            MyLogging.log(Level.INFO, "GetQuotePayments End");            
        }
        
        if (MethodName.equalsIgnoreCase("CreateInvoice")) {
            MyLogging.log(Level.INFO, "METHOD:=========CreateInvoice=========");                            
            try{
            
                Connection ebsDBConn = ApplicationsConnection.connectToEBSDatabase();
                SiebelDataBean ciSdbObj = ApplicationsConnection.connectSiebelServer();
                SiebelService ssv = new SiebelService(ciSdbObj);
                
                
                
                
                CallableStatement cs = null;            
                MyLogging.log(Level.INFO, "In CreateInvoice");
                MyLogging.log(Level.INFO, "ebsuserid:{0}", this.ebsuserid);
                MyLogging.log(Level.INFO, "ebsuserresp:{0}", this.ebsuserresp);
                MyLogging.log(Level.INFO, "ebsrespapplid:{0}", this.ebsrespapplid);
        
                String cust_trx_type_id = "";
                String custAcctSiteId = "";
                String custTermId = "";
                MyLogging.log(Level.INFO, "1..");
                String Sebsuserid = this.ebsuserid;
                String Sebsuserresp = this.ebsuserresp;
                
                String ebscustomerid = input.getProperty("ebs_customer_id");
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
                String salespersonid = input.getProperty("ebs_primary_salesperson");
                MyLogging.log(Level.INFO, "salespersonid:{0}" + salespersonid);
                String ebs_customer_term = input.getProperty("ebs_customer_term");
                MyLogging.log(Level.INFO, "ebs_customer_term:{0}" + ebs_customer_term);
                
                String account_id = input.getProperty("AccountId");
                MyLogging.log(Level.INFO, "account_id:{0}" + account_id);
                String contact_id = input.getProperty("ContactId");
                MyLogging.log(Level.INFO, "contact_id:{0}" + contact_id);
                String entity = input.getProperty("TheEntity");
                MyLogging.log(Level.INFO, "entity:{0}" + entity);
                
                               
                                
                
                String siebelRowId = ssv.getSiebelId(ebscustomerid);
                MyLogging.log(Level.INFO,"siebelRowId:"+siebelRowId);
                String the_entity = ssv.getEntity();
                MyLogging.log(Level.INFO,"the_entity:"+the_entity);
                
                
                
                if(!ssv.isThereShipToBillToId(siebelRowId, the_entity)){
                    ssv.addLocationToCustomer(siebelRowId,ebscustomerid,the_entity,ebsDBConn);
                }
                
                
                EBSData ed = new EBSData(ebsDBConn);
                MyLogging.log(Level.INFO, "3..");
                
                cust_trx_type_id = ed.getEBSCustTrxTypeId(custtrxtype);
                //custAcctSiteId = ed.getCustAccountSite(ebscustomerid);
                custTermId = ed.getEBSTermId(ebs_customer_term);
                MyLogging.log(Level.INFO, "Creating Invoice Object..");
                InvoiceObject invObj = new InvoiceObject();
                MyLogging.log(Level.INFO, "Invoice Object Created..");
                MyLogging.log(Level.INFO, "Setting invoice values..");
                invObj.setBillToId(ebscustomerid); 
                MyLogging.log(Level.INFO, "setBillToId done..");
                invObj.setTrxDate("sysdate");
                MyLogging.log(Level.INFO, "setTrxDate done..");
                invObj.setTrxCurrency(invoice_curr);
                MyLogging.log(Level.INFO, "setTrxCurrency done..");
                invObj.setTermId(custTermId);
                MyLogging.log(Level.INFO, "setTermId done..");
                invObj.setPrimarySalesId(salespersonid);
                MyLogging.log(Level.INFO, "setPrimarySalesId done..");
                invObj.setCustomerTrxTypeId(cust_trx_type_id);  
                MyLogging.log(Level.INFO, "setCustomerTrxTypeId done..");                
                MyLogging.log(Level.INFO, "Calling CreateInvoice.doInvoke ..");
                int invoiceNumber = CreateInvoice.doInvoke(quote_id, invObj, "quote",ciSdbObj,ebsDBConn);
                output.setProperty("customer_invoice_number", Integer.toString(invoiceNumber));
                MyLogging.log(Level.INFO, "invoiceNumber: {0}" + invoiceNumber);
                
                /*EBSInvoiceCreation eic = new EBSInvoiceCreation(Integer.parseInt(Sebsuserid), Integer.parseInt(Sebsuserresp), Integer.parseInt(this.ebsrespapplid),Integer.parseInt(this.ebspolicycontext),Integer.parseInt(this.ebsbatchsourceid),Integer.parseInt(this.ebstrxheaderid),ciSdbObj,ebsDBConn);
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
                output.setProperty("customer_invoice_number", Integer.toString(trx_id));*/
                ciSdbObj.logoff();
                if (cs != null) {
                    try{
                        cs.close();
                    }catch (SQLException ex){
                        ex.printStackTrace(new PrintWriter(errors));         
                        MyLogging.log(Level.SEVERE, "SQLException: ERROR"+ errors.toString());
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
            }catch (IOException ex){
                ex.printStackTrace(new PrintWriter(errors));  
                MyLogging.log(Level.SEVERE, "CreateInvoice:IOException: ERROR"+ errors.toString());
            }catch (SiebelBusinessServiceException ex){
                ex.printStackTrace(new PrintWriter(errors));  
                MyLogging.log(Level.SEVERE, "CreateInvoice:SiebelBusinessServiceException: ERROR"+ errors.toString());
            } catch (SiebelException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "CreateInvoice: SiebelException:ERROR"+ errors.toString());
            }
    }
        
    if(MethodName.equalsIgnoreCase("GetInvoiceReceipts")){
        MyLogging.log(Level.INFO, "METHOD:=====GetInvoiceReceipts========");
        String invoiceNumber  = input.getProperty("InvoiceNumber");
        String currInvoiceId  = input.getProperty("InvoiceId");
        String quoteId = input.getProperty("QuoteId");
        String RecProc = "{call plexGetReceiptsByInvId(?,?,?,?,?,?)}";        
        try {
            CallableStatement callablestatement = ApplicationsConnection.connectToEBSDatabase().prepareCall(RecProc);
            ReceiptsByINVID recpts = new ReceiptsByINVID();
            recpts.setInvNumber(invoiceNumber);
            recpts.setInvoiceId(currInvoiceId);
            recpts.setQuoteId(quoteId);
            ResultSet rs = recpts.getInvReceipts(callablestatement);
            recpts.processReceipts(rs,ApplicationsConnection.connectSiebelServer());
            if (callablestatement != null) {
                try{
                    callablestatement.close();
                }catch(SQLException ex){
                    ex.printStackTrace(new PrintWriter(errors));
                    MyLogging.log(Level.SEVERE,"Error in getInvReceipts. Error in closing SQL Connection: " + errors.toString());
                }    
            }
        } catch (IOException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE,"Error in processing"+errors.toString());
        }catch (ParseException ex){
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE,"Error in parsing date"+errors.toString());
        }catch(SQLException ex){
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE,"Error SQL callable statement"+errors.toString());
        }
    }
    
    if (MethodName.equalsIgnoreCase("GetCustomerEBSInfo")) {
        MyLogging.log(Level.INFO, "METHOD:==========GetCustomerEBSInfo==============");
        String ebs_id = input.getProperty("EBSId");
        MyLogging.log(Level.INFO, "ebs_id::"+ebs_id);
        //call the method to get the values
        
        //return values
        output.setProperty("CustomerCategoryCode", "Private");
        output.setProperty("CustomerClassification", "VEHICLE DEALERS");
        output.setProperty("CustomerProfileClass", "Customer");
        output.setProperty("CustomerReceivableAccount", "1010200");
        output.setProperty("CustomerRevenueAccount", "7021000");
        output.setProperty("CustomerTaxReference", "VAT 5%");
        output.setProperty("CustomerType", "Organisation");
    }
    
    
    
    
    
    
    if (MethodName.equalsIgnoreCase("CreateCustomer")) {
            MyLogging.log(Level.INFO, "METHOD:==========CreateCustomer==============");            
            String Sebsuserid = this.ebsuserid;
            String Sebsuserresp = this.ebsuserresp;
            String Sebrespapplid = this.ebsrespapplid;
            String Sebtrxheaderid = this.ebstrxheaderid;
       
            Connection conntn = ApplicationsConnection.connectToEBSDatabase();
            SiebelDataBean sdb = null;
            try {
                sdb = ApplicationsConnection.connectSiebelServer();
                
            } catch (IOException ex) {
               ex.printStackTrace(new PrintWriter(errors));                                                            
               MyLogging.log(Level.SEVERE, "SiebelDataBean ERROR:....."+ errors.toString());
            }
            SiebelService ssv = new SiebelService(sdb);
            
            CallableStatement cs = null;
            String siebelId = input.getProperty("customerId");
            String siebelAccountType = input.getProperty("entity");
            this.customerName = input.getProperty("customerName");
            this.customerNumber = input.getProperty("customerNumber");
            this.customerClassification = input.getProperty("customerClassification");
            String customerFirstName = input.getProperty("customerFirstName");
            String customerLastName = input.getProperty("customerLastName");
            MyLogging.log(Level.INFO, "customerName: {0}" + this.customerName);
            MyLogging.log(Level.INFO, "customerNumber: {0}" + this.customerNumber);
            MyLogging.log(Level.INFO, "customerClassification: {0}" + this.customerClassification);
            MyLogging.log(Level.INFO, "siebelAccountType: {0}" + siebelAccountType);
            String hdr = "";
            String bdy = "";
            if(siebelAccountType.equalsIgnoreCase("Account")){
                EBSOrganizationCustomerCreation ecc = new EBSOrganizationCustomerCreation(Integer.parseInt(Sebsuserid), Integer.parseInt(Sebsuserresp),Integer.parseInt(Sebrespapplid),Integer.parseInt(Sebtrxheaderid));
                hdr = ecc.createCustomerSQLHeader(this.customerName, this.customerNumber, this.customerClassification);
                bdy = ecc.createCustomerSQLBody();
            }else if(siebelAccountType.equalsIgnoreCase("Contact")){
                MyLogging.log(Level.INFO, "customerFirstName: {0}" + customerFirstName);
                MyLogging.log(Level.INFO, "customerLastName: {0}" + customerLastName);
                EBSPersonCustomerCreation ecc = new EBSPersonCustomerCreation(Integer.parseInt(Sebsuserid), Integer.parseInt(Sebsuserresp),Integer.parseInt(Sebrespapplid),Integer.parseInt(Sebtrxheaderid));
                hdr = ecc.createCustomerSQLHeader(customerFirstName,customerLastName, this.customerNumber, this.customerClassification);
                bdy = ecc.createCustomerSQLBody();
            }
            
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
                MyLogging.log(Level.INFO, "x_accounx_cust_account_idt_number: {0}" + x_account_number);
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
                    
                    //update Siebel record
                    MyLogging.log(Level.INFO, "updating siebel record with ebs id....");
                    try{
                        ssv.updateCustomerRecordWithEBSId(siebelAccountType, siebelId, Integer.toString(x_cust_account_id));
                    }catch(SiebelException ex){
                        ex.printStackTrace(new PrintWriter(errors));                                                            
                        MyLogging.log(Level.SEVERE, "CREATE CUSTOMER ERROR:....."+ errors.toString());
                    }
                    MyLogging.log(Level.INFO, "Siebel record updated....");
                    
                    
                    //Add Location to Customer
                    MyLogging.log(Level.INFO, "Adding Location to Customer:");
                    MyLogging.log(Level.INFO, "siebelId:"+siebelId);
                    MyLogging.log(Level.INFO, "x_cust_account_id:"+x_cust_account_id);
                    MyLogging.log(Level.INFO, "siebelAccountType:"+siebelAccountType);
                    
                    int return_val = 0;
                    int ship_to_id = 0;
                    int bill_to_id = 0;
                    
                    // sdb = ApplicationsConnection.connectSiebelServer();
                    EBSAccount ebsa = new EBSAccount();
                    ebsa.doInvoke(siebelId, Integer.toString(x_cust_account_id),siebelAccountType,sdb,conntn);
                    ship_to_id = ebsa.getShipToId();
                    bill_to_id = ebsa.getBillToId();
                    
                    
                    conntn.close();
                    MyLogging.log(Level.INFO, "Customer Location Value:" + return_val);
                    output.setProperty("ebsCustomerLocation", Integer.toString(return_val));
                    output.setProperty("ebsShipToId", Integer.toString(ship_to_id));
                    output.setProperty("ebsBillToId", Integer.toString(bill_to_id));
                    
                    //update Siebel record
                    MyLogging.log(Level.INFO, "updating siebel record with Ship To  and Bill To Id....");
                    try{                        
                        boolean upd_ret = ssv.updateCustomerRecordWithShipToBillToId(siebelAccountType, siebelId, Integer.toString(bill_to_id), Integer.toString(ship_to_id));
                        if(!upd_ret)
                            throw new SiebelException();
                    }catch(SiebelException ex){
                        //ex.printStackTrace(new PrintWriter(errors));                                                          
                        MyLogging.log(Level.SEVERE, "CREATE CUSTOMER ERROR::....."+ errors.toString());
                    }
                    MyLogging.log(Level.INFO, "Siebel record updated....");
                    
                    sdb.logoff();
                    
                } else if (x_return_status.equalsIgnoreCase("E")) {
                    output.setProperty("return_status", "Error");
                }
            }catch (SQLException ex) {
                ex.printStackTrace(new PrintWriter(errors));                                                            
                MyLogging.log(Level.SEVERE, "CREATE CUSTOMER ERROR: ....."+ errors.toString());
            } catch (SiebelException ex) {
                ex.printStackTrace(new PrintWriter(errors));                                                            
                MyLogging.log(Level.SEVERE, "ERROR LOGGING OUT OF SIEBEL: ....."+ errors.toString());
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
        
    
    if(MethodName.equalsIgnoreCase("CreateSalesOrder")){
        MyLogging.log(Level.INFO,"=================IN CreateSalesOrder=================");    
        try {
                SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
                Connection conn = ApplicationsConnection.connectToEBSDatabase();
                
                
                SalesOrderInventory soi = new SalesOrderInventory();
                MyLogging.log(Level.INFO,"order_number:"+input.getProperty("order_number")); 
                soi.setSiebelOrderId(input.getProperty("order_number"));                
                
                //soi.setOrderId(1001);
                
                MyLogging.log(Level.INFO,"ebs_customer_id:"+input.getProperty("ebs_customer_id"));
                soi.setSoldToOrgId(Integer.parseInt(input.getProperty("ebs_customer_id"))); 
                
                if(input.getProperty("ship_to_id").isEmpty()){
                    SiebelService ssv = new SiebelService(sdb);                
                    String ebscustomerid = input.getProperty("ebs_customer_id");
                    MyLogging.log(Level.INFO,"ebs_customer_id:"+input.getProperty("ebs_customer_id"));
                    String siebelRowId = ssv.getSiebelId(ebscustomerid);
                    MyLogging.log(Level.INFO,"siebelRowId:"+siebelRowId);
                    String entity = ssv.getEntity();
                    MyLogging.log(Level.INFO,"entity:"+entity);
                
                    if(!ssv.isThereShipToBillToId(siebelRowId, entity)){
                        ssv.addLocationToCustomer(siebelRowId,ebscustomerid,entity,conn);
                    }
                }
                
                MyLogging.log(Level.INFO,"ship_to_id:"+input.getProperty("ship_to_id"));
                if(!input.getProperty("ship_to_id").isEmpty()){
                    soi.setShipToOrgId(Integer.parseInt(input.getProperty("ship_to_id")));                
                }                
                MyLogging.log(Level.INFO,"bill_to_id:"+input.getProperty("bill_to_id"));
                if(!input.getProperty("bill_to_id").isEmpty()){
                    soi.setInvoiceId(Integer.parseInt(input.getProperty("bill_to_id")));
                }                
                MyLogging.log(Level.INFO,"warehouse_location_id:"+input.getProperty("warehouse_location_id"));
                if(!input.getProperty("warehouse_location_id").isEmpty()){
                    soi.setSoldFromId(Integer.parseInt(input.getProperty("warehouse_location_id")));
                }                
                MyLogging.log(Level.INFO,"sales_rep_id:"+input.getProperty("sales_rep_id"));
                if(!input.getProperty("sales_rep_id").isEmpty()){
                    soi.setSalesRepId(Integer.parseInt(input.getProperty("sales_rep_id")));
                }
                                
                //soi.setPriceId(Integer.parseInt(input.getProperty("price_id")));
                MyLogging.log(Level.INFO,"currency_code:"+input.getProperty("currency_code"));
                soi.setTransactionCode(input.getProperty("currency_code"));
                
                MyLogging.log(Level.INFO, "type is :"+input.getProperty("type"));
                soi.setType(input.getProperty("type"));
                
                soi.setStatusCode("ENTERED");
                soi.setPurchaseOrderNumber(input.getProperty("order_number"));
                soi.setSourceId(0);
                SalesOrder sou = new SalesOrder();
                MyLogging.log(Level.INFO,"Invokeing Sales Order Invoke Method");
                sou.doInvoke(soi,sdb,conn);
                MyLogging.log(Level.INFO,"Sales Order Invoke Method Finished");
                MyLogging.log(Level.INFO,"Getting return values .....");
                List<String> rm = sou.getReturnMessages();
                String rs = sou.getReturnStatus();
                MyLogging.log(Level.INFO,"Return Status: "+rs );
                String fsc = sou.getFlowStatusCode();
                MyLogging.log(Level.INFO,"Status Code: "+fsc );
                int order_number = sou.getOrderNumber();                
                MyLogging.log(Level.INFO,"order_number: "+order_number);
                String message = "";
                int num = 0;
                
                if(fsc.equalsIgnoreCase("ENTERED")){
                    for (String list1 : rm)
                    {
                        message += "{" + num + "} ";
                        message += list1;
                        message += " \n";
                    }
                }else if(fsc.equalsIgnoreCase("BOOKED")){
                    message += "Sales Order has been booked in EBS";
                    message += " \n";
                    message += " EBS Sales Order number is "+order_number;
                }
                MyLogging.log(Level.INFO,"message: "+message );
                output.setProperty("STATUS_CODE", fsc);
                output.setProperty("MESSAGE", message);
                output.setProperty("ORDER_NUM", String.valueOf(order_number));
                sdb.logoff();
            } catch (IOException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "ERROR:CreateSalesOrder:creating siebel server object:"+ errors.toString());
            } catch (SiebelException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "ERROR:CreateSalesOrder: siebel server logoff failed:"+ errors.toString());
            }finally{                              
                if(conn != null){
                    try{
                        conn.close();
                    }catch (SQLException ex) {
                        ex.printStackTrace(new PrintWriter(errors));
                        MyLogging.log(Level.SEVERE, "ERROR:CreateSalesOrder:Not able to close connection object:"+ errors.toString());
                    }
                } 
                
            }                        
    }
    
    if(MethodName.equalsIgnoreCase("CreatePurchaseOrder")){
        MyLogging.log(Level.INFO,"=================IN CreatePurchaseOrder=================");
        try{
            Connection ebs = ApplicationsConnection.connectToEBSDatabase();
            SiebelDataBean sb = ApplicationsConnection.connectSiebelServer();
            PurchaseOrder pOrder = new PurchaseOrder();
            PurchaseOrderInventory poInventory = new PurchaseOrderInventory();
            String accountType = input.getProperty("AccountType");
            MyLogging.log(Level.INFO, "accountType : "+accountType);
            poInventory.setAccountType(accountType);
            String orderId = input.getProperty("OrderId");
            MyLogging.log(Level.INFO, "orderId : "+orderId);
            poInventory.setSiebelOrderId(orderId);
            String accountId = input.getProperty("AccountId");
            MyLogging.log(Level.INFO, "accountId : "+accountId);
            poInventory.setSiebelAccountId(accountId);
            String sourceId = input.getProperty("SourceId");
            MyLogging.log(Level.INFO, "sourceId : "+sourceId);
            poInventory.setSourceId(Integer.parseInt(sourceId));
            MyLogging.log(Level.INFO, "Calling purchase order method");
            pOrder.doInvoke(poInventory,sb, ebs);
            MyLogging.log(Level.INFO, "Purchase order method done");
            sb.logoff();
            ebs.close();
        }catch(FileNotFoundException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CreatePurchaseOrder:filenotfoundexception:"+ errors.toString());
        }catch(IOException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CreatePurchaseOrder:ioexception:"+ errors.toString());
        }   catch (SiebelException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CreatePurchaseOrder:cannot close siebel connection:"+ errors.toString());
        } catch (SQLException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CreatePurchaseOrder:cannot close ebs db connection:"+ errors.toString());
        }
                
    }
        
    
    if(MethodName.equalsIgnoreCase("ReturnSalesOrder")){
        MyLogging.log(Level.INFO,"=================IN ReturnSalesOrder=================");    
        try {
                SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
                Connection conn = ApplicationsConnection.connectToEBSDatabase();
                                
                SalesOrderInventory soi = new SalesOrderInventory();
                MyLogging.log(Level.INFO,"order_number:"+input.getProperty("order_number")); 
                soi.setSiebelOrderId(input.getProperty("order_number"));                
                //soi.setOrderId(1041);
                MyLogging.log(Level.INFO,"ebs_customer_id:"+input.getProperty("ebs_customer_id"));
                soi.setSoldToOrgId(Integer.parseInt(input.getProperty("ebs_customer_id"))); 
                MyLogging.log(Level.INFO, "type is :"+input.getProperty("type"));
                soi.setType(input.getProperty("type"));                
                
                MyLogging.log(Level.INFO,"ship_to_id:"+input.getProperty("ship_to_id"));
                if(!input.getProperty("ship_to_id").isEmpty()){
                    soi.setShipToOrgId(Integer.parseInt(input.getProperty("ship_to_id")));                
                }     
                
                MyLogging.log(Level.INFO,"bill_to_id:"+input.getProperty("bill_to_id"));
                if(!input.getProperty("bill_to_id").isEmpty()){
                    soi.setInvoiceId(Integer.parseInt(input.getProperty("bill_to_id")));
                }            
                
                MyLogging.log(Level.INFO,"warehouse_location_id:"+input.getProperty("warehouse_location_id"));
                if(!input.getProperty("warehouse_location_id").isEmpty()){
                    soi.setSoldFromId(Integer.parseInt(input.getProperty("warehouse_location_id")));
                } 
                
                MyLogging.log(Level.INFO,"sales_rep_id:"+input.getProperty("sales_rep_id"));
                if(!input.getProperty("sales_rep_id").isEmpty()){
                    soi.setSalesRepId(Integer.parseInt(input.getProperty("sales_rep_id")));
                }
                                                
                MyLogging.log(Level.INFO,"currency_code:"+input.getProperty("currency_code"));
                soi.setTransactionCode(input.getProperty("currency_code"));
                
                soi.setStatusCode("ENTERED");
                soi.setPurchaseOrderNumber(input.getProperty("order_number"));
                soi.setSourceId(0);
                SalesOrder sou = new SalesOrder();
                MyLogging.log(Level.INFO,"Invoking Return Sales Order Invoke Method");
                sou.doInvoke(soi,sdb,conn);
                MyLogging.log(Level.INFO,"Return Sales Order Invoke Method Finished");
                MyLogging.log(Level.INFO,"Getting return values .....");
                List<String> rm = sou.getReturnMessages();
                String rs = sou.getReturnStatus();
                MyLogging.log(Level.INFO,"Return Status: "+rs );
                String fsc = sou.getFlowStatusCode();
                MyLogging.log(Level.INFO,"Status Code: "+fsc );
                int order_number = sou.getOrderNumber();                
                MyLogging.log(Level.INFO,"order_number: "+order_number);
                String message = "";
                String RMA = "";
                //int num = 0;
                
                if(fsc.equalsIgnoreCase("BOOKED")){                                        
                    message = " EBS Order number is "+order_number;
                    RMA  = "RMA";
                }
                MyLogging.log(Level.INFO,"message: "+message );
                output.setProperty("STATUS_CODE", RMA);
                output.setProperty("MESSAGE", message);
                output.setProperty("ORDER_NUM", String.valueOf(order_number));
                sdb.logoff();
            } catch (IOException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "ERROR:ReturnSalesOrder:creating siebel server object:"+ errors.toString());
            } catch (SiebelException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "ERROR:ReturnSalesOrder: siebel server logoff failed:"+ errors.toString());
            }finally{                              
                if(conn != null){
                    try{
                        conn.close();
                    }catch (SQLException ex) {
                        ex.printStackTrace(new PrintWriter(errors));
                        MyLogging.log(Level.SEVERE, "ERROR:ReturnSalesOrder:Not able to close connection object:"+ errors.toString());
                    }
                } 
                
            }                        
    }

    
    if(MethodName.equalsIgnoreCase("GetItemLocatorOld")){
        MyLogging.log(Level.INFO, "In GetItemLocator");
        MyLogging.log(Level.INFO, "Item Number is: "+input.getProperty("item_number"));
        MyLogging.log(Level.INFO, "quote_line_item_id is: "+input.getProperty("quote_line_item_id"));
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
    
    if(MethodName.equalsIgnoreCase("GetItemLocator")){
        MyLogging.log(Level.INFO, "In GetItemLocator");
        MyLogging.log(Level.INFO, "Item Number is: "+input.getProperty("item_number"));
        MyLogging.log(Level.INFO, "Item Id is: "+input.getProperty("item_id"));
        MyLogging.log(Level.INFO, "quote_line_item_id is: "+input.getProperty("quote_line_item_id"));
        //Connection conn = ApplicationsConnection.connectToEBSDatabase();
        Connection conn = ApplicationsConnection.connectToSiebelDatabase();
        EBSOnHandQty ehq = new EBSOnHandQty(conn);
        SiebelPropertySet itemLocatorPS = new SiebelPropertySet();
        try{
            itemLocatorPS = ehq.getItemLocator(input.getProperty("item_id"));        
            output.addChild(itemLocatorPS.copy());
            output.setProperty("RETURN_STATUS", "Success");
            if(conn != null){
                conn.close();
            }
        }catch(Exception e){
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.INFO, "Error in GetItemLocator: "+errors.toString());
            output.setProperty("MSG_DATA", errors.toString());
            output.setProperty("RETURN_STATUS", "Failure");
        }
    }
    
    if(MethodName.equalsIgnoreCase("CheckSalesOrderStatus")){        
        MyLogging.log(Level.INFO,"=================IN CheckSalesOrderStatus=================");
        try{
            Connection ebs = ApplicationsConnection.connectToEBSDatabase();            
            if(ebs == null){
                throw new ConnectException();
            }
            String order_num = input.getProperty("order_num");
            MyLogging.log(Level.INFO,"Sales Order Number:"+order_num);
            SalesOrder so = new SalesOrder();
            String booking_status = so.getSalesOrderBookingStatus(ebs,order_num);
            booking_status = toTitleCase(booking_status);
            output.setProperty("BOOKING_STATUS", booking_status);
            MyLogging.log(Level.INFO,"BOOKING_STATUS:"+booking_status);
            if(ebs != null){
                ebs.close();
            }
        }catch (SiebelBusinessServiceException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CheckSalesOrderStatus:"+ errors.toString());
        }catch (SQLException ex) {         
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CheckSalesOrderStatus:"+ errors.toString());
        }catch (ConnectException ex) {  
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CheckSalesOrderStatus:cannot connect to EBS DB"+ errors.toString());
        }  
        
    }
    
    if(MethodName.equalsIgnoreCase("CheckPurchaseOrderStatus")){
        try{
            Connection ebs = ApplicationsConnection.connectToEBSDatabase(); 
            if(ebs == null){
                throw new ConnectException();
            }
            MyLogging.log(Level.INFO,"=================IN CheckPurchaseOrderStatus=================");        
            PurchaseOrder purchOrder = new PurchaseOrder();
            String purch_order_num = input.getProperty("order_num");
            MyLogging.log(Level.INFO,"Purchase Order Number:"+purch_order_num);
            String booking_status = purchOrder.getPurchaseOrderBookingStatus(ebs,purch_order_num);
            //String po_number = purchOrder.getPurchaseOrderNumber(ebs, purch_order_num);
            String po_number = purchOrder.getPONumber(ebs, purch_order_num);
            output.setProperty("BOOKING_STATUS", booking_status);
            output.setProperty("PO_ORDER_NUM", po_number);
            MyLogging.log(Level.INFO,"BOOKING_STATUS:"+booking_status);
            MyLogging.log(Level.INFO,"PO_ORDER_NUM:"+po_number);
            if(ebs != null){
                ebs.close();
            }
        }catch (SiebelBusinessServiceException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CheckPurchaseOrderStatus:SiebelBusinessServiceException:"+ errors.toString());
        }   catch (SQLException ex) { 
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CheckPurchaseOrderStatus:SQLException:"+ errors.toString());
            }catch (ConnectException ex) {
               ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "ERROR:CheckPurchaseOrderStatus:SQLException:cannot connect to EBS DB:"+ errors.toString());
            }
    }
    
    if(MethodName.equalsIgnoreCase("CancelSalesOrder")){        
        MyLogging.log(Level.INFO, "================In CancelSalesOrder===================");
        String order_number = input.getProperty("ordernumber");
        MyLogging.log(Level.INFO, "order_number is: "+order_number);
        Connection ebs_conn = ApplicationsConnection.connectToEBSDatabase();
        SalesOrder ea = new SalesOrder();
        try{
            MyLogging.log(Level.INFO, "Calling Cancel Sales Order=========");
            ea.cancelOrder(ebs_conn, Integer.parseInt(order_number));     
            MyLogging.log(Level.INFO, "Cancel Sales Order Done=========");
            if(ebs_conn != null){
                ebs_conn.close();
            }
        }catch(SQLException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:CancelSalesOrder:cannot connecting to EBS DB"+ errors.toString());
        }                
    }
    
    
    if(MethodName.equalsIgnoreCase("SalesOrderItemStatus")){        
        MyLogging.log(Level.INFO, "================In SalesOrderItemStatus===================");
       // int order_number = 0;
        
        String sOrder_number = input.getProperty("ordernumber");
        //String sInventory_number = input.getProperty("inventorynumber");
        /*if(!sOrder_number.isEmpty()){
            order_number = Integer.parseInt(sOrder_number);
        }*/
        String lotId= input.getProperty("lotid");
        MyLogging.log(Level.INFO, "order_number is: "+sOrder_number);
        MyLogging.log(Level.INFO, "partnumber is: "+input.getProperty("partnumber"));
        MyLogging.log(Level.INFO, "lotid is: "+lotId);
        
        
        try{
            Connection ebs_conn = ApplicationsConnection.connectToEBSDatabase();
            //SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
            SalesOrder salesOrder = new SalesOrder();
            Order order = new Order();
            order.setPartNumber(input.getProperty("partnumber"));
            order.setOrderNumber(sOrder_number);
            if(!lotId.isEmpty()){
                int numLotId = Integer.parseInt(lotId);
                order.setWarehouseId(numLotId);
            }
            
            BackOrder itemInfo = salesOrder.getSalesOrderLineItemStatus(ebs_conn,order );
            MyLogging.log(Level.INFO, "==========Calling Sales Order Item Status Check=========");
            //String onHand = ea.onHandStatus(ebs_conn, order_number, inventory_number);
            //output.setProperty("ITEM_STATUS", onHand);
            //ea.cancelOrder(ebs_conn, Integer.parseInt(sOrder_number));  
            String sBack_order_quantity = "";
            String release_status = itemInfo.getReleaseStatus();// output.getProperty("release_status");
            String pick_meaning = itemInfo.getPickMeaning();//output.getProperty("pick_meaning");
            int back_order_quantity = itemInfo.getQuantity();// output.getProperty("back_order_quantity");
            if(back_order_quantity >= 0){
                sBack_order_quantity = String.valueOf(back_order_quantity);
            }
            String item_status = itemInfo.getItemStatus(); //output.getProperty("item_status");
            
            MyLogging.log(Level.INFO, "release_status is: "+release_status);
            MyLogging.log(Level.INFO, "pick_meaning is: "+pick_meaning);
            MyLogging.log(Level.INFO, "back_order_quantity is: "+sBack_order_quantity);
            MyLogging.log(Level.INFO, "item_status is: "+item_status);
            
            output.setProperty("ITEM_STATUS", item_status);
            output.setProperty("PICK_MEANING", pick_meaning);
            output.setProperty("RELEASE_STATUS", release_status);
            output.setProperty("BACK_ORDER_QUANITIY", sBack_order_quantity);
            
            MyLogging.log(Level.INFO, "==========Sales Order Item Status Check Done=========");
            if(ebs_conn != null){
                ebs_conn.close();
            }
           // sdb.logoff();
        }catch(SQLException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:SalesOrderItemStatus:cannot connecting to EBS DB:"+ errors.toString());
        }/*catch(SiebelException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:SalesOrderItemStatus:SiebelError::"+ errors.toString());
        }catch(FileNotFoundException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:SalesOrderItemStatus:FIleNotFound"+ errors.toString());
        }catch(IOException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:SalesOrderItemStatus:"+ errors.toString());
        }*/catch(Exception ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "ERROR:SalesOrderItemStatus::Geneic Error"+ errors.toString());
        }                   
    }
    
    
    if (MethodName.equalsIgnoreCase("GetCustomerEBSPayments")) {
        MyLogging.log(Level.INFO, "METHOD:==========GetCustomerEBSPayments==============");
        String ebs_id = input.getProperty("EBSId");
        MyLogging.log(Level.INFO, "ebs_id::"+ebs_id);
        //call the method to get the values
        EBSReceipts ebsRcpts = new EBSReceipts();
        ebsRcpts.customerId = Integer.parseInt(ebs_id);
        try {
            ebsRcpts.getCustReceipts();
            SiebelPropertySet customerReceipts = ebsRcpts.getReceipts();
            output.addChild(customerReceipts.copy());
        } catch (SQLException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE, "SQLException in GetCustomerEBSPayments :: "+errors);
        }
        
           
        
    }
    
}
    public String toTitleCase(String s){
        if (s.isEmpty()){
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
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
        MyLogging.log(Level.INFO, "dbase is {0}"+ dbase);
        MyLogging.log(Level.INFO, "dbuser is {0}"+ dbuser);
        MyLogging.log(Level.INFO, "dbpwd is {0}"+ dbpwd);
        MyLogging.log(Level.INFO, "logFile is {0}"+ logFile);
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
            
            
            
            //input.setProperty("EBSId","1066");
            //ns.doInvokeMethod("GetCustomerEBSPayments", input, output);
                    
            //input.setProperty("order_num","203");            
            //ns.doInvokeMethod("CheckSalesOrderStatus", input, output);
            
            //input.setProperty("order_num","1-2KBRL");            
            //ns.doInvokeMethod("CheckPurchaseOrderStatus", input, output);
            
            /*input.setProperty("customerId","1-24VZ9");
            input.setProperty("entity","Account");
            input.setProperty("customerName","EZAXWEST");
            input.setProperty("customerNumber","WST-NG-1-4475401");
            input.setProperty("customerClassification","VEHICLE NONDEALERS");
            ns.doInvokeMethod("CreateCustomer", input, output);*/
            
           /* input.setProperty("customerId","1-1CKQU");
            input.setProperty("entity","Contact");
            input.setProperty("customerName","RECARDO");
            input.setProperty("customerNumber","WST-NG-1-3472401");
            input.setProperty("customerClassification","VEHICLE NONDEALERS");
            input.setProperty("customerFirstName","Mustapha");
            input.setProperty("customerLastName","Isaiku");
            ns.doInvokeMethod("CreateCustomer", input, output);*/
            
            /*input.setProperty("order_number","1-35951270");
            input.setProperty("ebs_customer_id","54093");
            //input.setProperty("ship_to_id","17134");
            //input.setProperty("bill_to_id","17133");
            input.setProperty("ship_to_id","");
            input.setProperty("bill_to_id","");
            input.setProperty("warehouse_location_id","123");
            input.setProperty("sales_rep_id","100000040");
            input.setProperty("currency_code","NGN");
            ns.doInvokeMethod("CreateSalesOrder", input, output);*/
                        
            /*input.setProperty("AccountType","organization");            
            input.setProperty("OrderId","1-2KBRL");            
            input.setProperty("AccountId","1-22FBL");            
            input.setProperty("SourceId","97");
            ns.doInvokeMethod("CreatePurchaseOrder", input, output);*/
                        
            /*input.setProperty("invoice_currency","NGN");                
            input.setProperty("order_id","");
            input.setProperty("quote_id","1-KXYLD");                
            input.setProperty("ebs_customer_term","IMMEDIATE");            
            input.setProperty("ebs_customer_trx_type","VEHICLE NONDEALERS");                
            input.setProperty("ebs_primary_salesperson","100000040");
            input.setProperty("ebs_customer_id","54094");
            ns.doInvokeMethod("CreateInvoice", input, output);
            MyLogging.log(Level.INFO, System.getenv("intg_property"));*/
            
            /*input.setProperty("InvoiceId", "1-150NH");            
            input.setProperty("QuoteId","1-150NH");
            ns.doInvokeMethod("GetQuotePayments", input, output);*/
            /*input.setProperty("item_id", "14");
            input.setProperty("item_number","131628");
            input.setProperty("quote_line_item_id","131628");
            input.setProperty("warehouse_id", "123");
            //ns.doInvokeMethod("GetItemLocator", input, output);*/
            //ns.doInvokeMethod("CallOnHandQty", input, output);
            //input.setProperty("order_num","1-4861761");
            //ns.doInvokeMethod("CheckPurchaseOrderStatus", input, output);
            /*input.setProperty("partnumber", "A2044700294");            
            input.setProperty("lotid","123");
            input.setProperty("ordernumber","1-42776238");            
            ns.doInvokeMethod("SalesOrderItemStatus", input, output);*/
            
            input.setProperty("order_number","1-58402856");
            input.setProperty("ebs_customer_id","54093");
            input.setProperty("ship_to_id","17134");
            input.setProperty("bill_to_id","17133");
            input.setProperty("type","return_order");
            //input.setProperty("ship_to_id","");
            //input.setProperty("bill_to_id","");
            input.setProperty("warehouse_location_id","123");
            input.setProperty("sales_rep_id","100000040");
            input.setProperty("currency_code","NGN");
            ns.doInvokeMethod("ReturnSalesOrder", input, output);
        }
        catch (Exception ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "In main: "+ errors.toString());
        }
    }
}