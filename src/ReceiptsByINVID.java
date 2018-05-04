/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */
import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleTypes;

public class ReceiptsByINVID {
    
    private String invNumber;
    private String invoiceId;
     private String quoteId;
    private StringWriter errors = new StringWriter();
    Map paymentStatusMap = new HashMap();
    
    public ReceiptsByINVID() {        
        paymentStatusMap.put("APP", "Applied");
        paymentStatusMap.put("REV", "Reverse Payment");
        paymentStatusMap.put("STOP", "Stop Payment");
        paymentStatusMap.put("UNAPP", "Unapplied");
        paymentStatusMap.put("UNID", "Unidentified");                
    }
    
    
    
    
    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }
   
  
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    
    public String getInvNumber() {
        return invNumber;
    }

    public void setInvNumber(String invNumber) {
        this.invNumber = invNumber;
    }
    
    public ResultSet getInvReceipts(CallableStatement callablestatement){        
        //CallableStatement callablestatement = null;
        String RecProc = "{call plexGetReceiptsByInvId(?,?,?,?,?,?,?)}";
        ResultSet R = null;
        try {
            //log.debug("Calling Strored Procedure...");
            
            //callablestatement = ApplicationsConnection.connectToEBSDatabase().prepareCall(RecProc);
            callablestatement.registerOutParameter(1, OracleTypes.CURSOR);
            callablestatement.setString(2, invNumber);
            callablestatement.registerOutParameter(3, java.sql.Types.NUMERIC);
            callablestatement.registerOutParameter(4, java.sql.Types.NUMERIC);
            callablestatement.registerOutParameter(5, java.sql.Types.DATE);            
            callablestatement.registerOutParameter(6, java.sql.Types.VARCHAR);
            callablestatement.registerOutParameter(7, java.sql.Types.VARCHAR);
            callablestatement.execute();
            R = (ResultSet)callablestatement.getObject(1);                                           
            
        } catch (SQLException e) {
            //log.error("Error in calling Stored Procedure "+e);
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.INFO,"Error in getInvReceipts. Error in SQL: " + errors.toString());            
        }
       return R;    
    }

    public void insertReceiptDataIntoSiebel(ResultSet R) {
        
        try{            
            SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
            MyLogging.log(Level.INFO,"Printing Receipts.....");                     
           String paymentNuumber;         
           SiebelBusObject fsInvBusObj = sdb.getBusObject("FS Invoice");
           SiebelBusComp fsInvBusComp = fsInvBusObj.getBusComp("FS Invoice");
           fsInvBusComp.activateField("Payment #");
           fsInvBusComp.setViewMode(3);
           fsInvBusComp.clearToQuery();
           fsInvBusComp.setSearchSpec("Invoice Id", this.invoiceId);
           boolean iRec = fsInvBusComp.firstRecord();           
           while(iRec){
               paymentNuumber = fsInvBusComp.getFieldValue("Payment #");
               
               iRec = fsInvBusComp.nextRecord();
           }
           
           sdb.logoff();
        }catch (IOException io){
            io.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.INFO,"Error in getInvReceipts. Error in connection to Siebel: " + errors.toString());        
        }catch (SiebelException e) {
            //log.error("Error in calling Stored Procedure "+e);
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE,"Error in getInvReceipts. Error in siebel connection: " + errors.toString());            
        }
    }
    
    private void processReceipts(String paymentNuumber, SiebelBusComp fsInvBusComp,ResultSet R) throws SQLException{
        boolean recExist = false;
        while (R.next()){
            if(R.getString(1).equalsIgnoreCase(paymentNuumber))
                recExist = true;                        
        }
        if(!recExist){
            try {
                fsInvBusComp.newRecord(0);
                
            } catch (SiebelException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE,"Error in processReceipt. Error in siebel new record: " + errors.toString());            
            }
           
        }            
    }
    
    public void processReceipts(ResultSet R,SiebelDataBean sdb) throws SQLException, IOException, ParseException{        
        MyLogging.log(Level.INFO,"In processReceipts");
        System.out.println("RECEIPT NO " + "|" + " AMOUNT PAID" + "|" + "      RECEIPT DATE" + "|" + " AMOUNT APPLIED" );
        //System.out.println(R.getString(1) + "        |" + R.getString(2) + "       |" + R.getString(3) + "         |" + R.getString(4));
        //sdb = ApplicationsConnection.connectSiebelServer();
        while (R.next()){
            Receipts receiptObj = new Receipts(R.getString(1),R.getString(2),R.getString(3),R.getString(4),R.getString(5));           
            //insertReceiptDataIntoSiebel(sdb, receiptObj);
            createQuotePaymentRecord(sdb,receiptObj,quoteId);
        }        
    }
    
    private void pickRecord(String row_id,SiebelDataBean sdb) throws SiebelException{
        SiebelBusObject boFSInvoice = sdb.getBusObject("FS Invoice");
        SiebelBusComp bcFSInvoicePayment = boFSInvoice.getBusComp("FS Invoice Payments");
        SiebelBusComp bcPayments = bcFSInvoicePayment.getPicklistBusComp("Payment #");
        bcPayments.setViewMode(3);
        bcPayments.clearToQuery();
        bcPayments.setSearchSpec("Id", row_id);        
        bcPayments.executeQuery2(true, true);
        boolean isRecord = bcPayments.firstRecord();
        try{
            if (isRecord){
                MyLogging.log(Level.INFO,"Record found and will pick");
                
                MyLogging.log(Level.INFO,"Ref # :"+bcPayments.getFieldValue("Ref #"));
                MyLogging.log(Level.INFO,"Payment type :"+bcPayments.getFieldValue("Payment Type"));                    
                bcPayments.pick();
            }
            bcPayments.writeRecord();
        }catch (SiebelException e){
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.INFO,"Error in Picking Record : "+ errors.toString());
        }
    }
    
    public void insertReceiptDataIntoSiebelS(SiebelDataBean sdb,Receipts receiptObj) {
        MyLogging.log(Level.INFO,"In insertReceiptDataIntoSiebel");
        try{                                                      
           String curr_row_id;
           String paymentNuumber;
           String reciptNum = receiptObj.getReciptNum();
           boolean recExist = false;
           SiebelBusObject fsInvBusObj = sdb.getBusObject("FS Invoice");
           SiebelBusComp fsInvBusComp = fsInvBusObj.getBusComp("FS Invoice");
           SiebelBusComp fsInvPymntBusComp = fsInvBusObj.getBusComp("FS Invoice Payments");                      
           fsInvBusComp.activateField("Payment #");
           fsInvBusComp.setViewMode(3);
           fsInvBusComp.clearToQuery();
           fsInvBusComp.setSearchSpec("Id", this.invoiceId);
           fsInvBusComp.executeQuery2(true, true);
           if(fsInvBusComp.firstRecord()){
               fsInvPymntBusComp.activateField("Payment #");
               fsInvPymntBusComp.setViewMode(3);
               fsInvPymntBusComp.clearToQuery();
               fsInvPymntBusComp.setSearchSpec("Invoice Id", this.invoiceId);
               fsInvPymntBusComp.executeQuery2(true, true);
               boolean iRec = fsInvPymntBusComp.firstRecord();           
               while(iRec){
                   paymentNuumber = fsInvPymntBusComp.getFieldValue("Payment #");
                   if(reciptNum.equalsIgnoreCase(paymentNuumber))
                        recExist = true;
                   iRec = fsInvPymntBusComp.nextRecord();
               }
               MyLogging.log(Level.INFO,"Record Exists? :"+recExist);
               
               if(!recExist){                
                    fsInvPymntBusComp.newRecord(0);
                    fsInvPymntBusComp.setFieldValue("Payment #", reciptNum);
                    fsInvPymntBusComp.setFieldValue("Amount", receiptObj.getAmountPaid());
                    fsInvPymntBusComp.setFieldValue("Payment Date", receiptObj.getReciptDate());
                    //fsInvBusComp.setFieldValue(reciptNum, receiptObj);
                    fsInvPymntBusComp.writeRecord();
                    curr_row_id = fsInvPymntBusComp.getFieldValue("Id");
                }
           }                                          
        }catch (SiebelException e) {
            //log.error("Error in calling Stored Procedure "+e);
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE,"Error in getInvReceipts. Error in siebel connection: " + errors.toString());            
        }
    }
    
    public void insertReceiptDataIntoSiebel(SiebelDataBean sdb,Receipts receiptObj) throws ParseException{
        MyLogging.log(Level.INFO,"In insertReceiptDataIntoSiebel");
        try{                                                      
           String curr_row_id;
           String paymentNuumber;
           String reciptNum = receiptObj.getReciptNum();
           MyLogging.log(Level.INFO,"reciptNum :"+reciptNum);
           boolean recExist = false;
           SiebelBusObject fsInvBusObj = sdb.getBusObject("FS Invoice");
           SiebelBusComp fsInvBusComp = fsInvBusObj.getBusComp("FS Invoice");
           //SiebelBusComp fsPymntBusComp = fsInvBusObj.getBusComp("FS Payments");  
           SiebelBusComp fsPymntBusComp = fsInvBusObj.getBusComp("FS Invoice Payments");             
           fsInvBusComp.activateField("Payment #");
           fsInvBusComp.setViewMode(3);
           fsInvBusComp.clearToQuery();
           fsInvBusComp.setSearchSpec("Id", this.invoiceId);
           fsInvBusComp.executeQuery2(true, true);
           if(fsInvBusComp.firstRecord()){
               fsPymntBusComp.activateField("Ref #");
               fsPymntBusComp.setViewMode(3);
               fsPymntBusComp.clearToQuery();
               fsPymntBusComp.setSearchSpec("Invoice Id", this.invoiceId);
               fsPymntBusComp.executeQuery2(true, true);
               boolean iRec = fsPymntBusComp.firstRecord();           
               while(iRec){
                   paymentNuumber = fsPymntBusComp.getFieldValue("Ref #");
                   MyLogging.log(Level.INFO,"paymentNuumber :"+paymentNuumber);
                   if(reciptNum.equalsIgnoreCase(paymentNuumber))
                        recExist = true;
                   iRec = fsPymntBusComp.nextRecord();
               }
               MyLogging.log(Level.INFO,"Record Exists? :"+recExist);  
               if(!recExist){   
                    MyLogging.log(Level.INFO,"Creating new record :");
                    fsPymntBusComp.newRecord(0);                    
                    fsPymntBusComp.setFieldValue("Ref #", reciptNum);
                    MyLogging.log(Level.INFO,"Ref # :"+reciptNum);
                    fsPymntBusComp.setFieldValue("Payment Type", "Payment");
                    fsPymntBusComp.setFieldValue("Payment Method", "Cash");
                    fsPymntBusComp.setFieldValue("Invoice Id", this.invoiceId);
                    fsPymntBusComp.setFieldValue("Amount", receiptObj.getAmountPaid());
                    MyLogging.log(Level.INFO,"Amount :"+receiptObj.getAmountPaid());
                    MyLogging.log(Level.INFO,"Payment Date :"+receiptObj.getReciptDate()); 
                    MyLogging.log(Level.INFO,"Payment Date :"+convertDate(receiptObj.getReciptDate())); 
                    fsPymntBusComp.setFieldValue("Payment Date", convertDate(receiptObj.getReciptDate()));
                    //fsInvBusComp.setFieldValue(reciptNum, receiptObj);
                    fsPymntBusComp.writeRecord();
                    curr_row_id = fsPymntBusComp.getFieldValue("Id");
                    MyLogging.log(Level.INFO,"curr_row_id :"+curr_row_id);
                    pickRecord(curr_row_id,sdb);
                }
           }                                          
        }catch (SiebelException e) {
            //log.error("Error in calling Stored Procedure "+e);
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE,"Error in getInvReceipts. Error in siebel connection: " + errors.toString());            
        }
    }
    
    public void createQuotePaymentRecord(SiebelDataBean sdb,Receipts receiptObj,String quoteId) throws ParseException{
        MyLogging.log(Level.INFO,"In insertReceiptDataIntoSiebel");
        try{                                                      
           String curr_row_id;
           String paymentNuumber;
           String reciptNum = receiptObj.getReciptNum();
           String transactionAmnt = receiptObj.getAmountPaid();
           String paymentMethod = receiptObj.getPaymentMethod();           
           String paymentStatus = receiptObj.getPaymentStatus();
           paymentMethod = (String)paymentStatusMap.get(paymentMethod);
           String paymentDate = receiptObj.getReciptDate();
           String paymentOrclDate = convertDate(paymentDate);
           /*Date paymentOrclDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(paymentDate);
           DateFormat fdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");                                 
           String inPayDate = fdf.format(paymentOrclDate);*/
                      
           MyLogging.log(Level.INFO,"reciptNum :"+reciptNum);
           MyLogging.log(Level.INFO,"transactionAmnt :"+transactionAmnt);
           MyLogging.log(Level.INFO,"paymentMethod :"+paymentMethod);
           MyLogging.log(Level.INFO,"paymentStatus :"+paymentStatus);
           MyLogging.log(Level.INFO,"paymentDate :"+paymentDate);
           MyLogging.log(Level.INFO,"paymentOrclDate :"+paymentOrclDate);
           //MyLogging.log(Level.INFO,"inPayDate :"+inPayDate);
           boolean recExist = false;
           SiebelBusObject quoteBusObj = sdb.getBusObject("Quote");
           SiebelBusComp quoteBusComp = quoteBusObj.getBusComp("Quote");
           SiebelBusComp paymentBusComp = quoteBusObj.getBusComp("Payments");
           //find Quote
           quoteBusComp.activateField("Quote Total");
           quoteBusComp.setViewMode(3);
           quoteBusComp.clearToQuery();
           quoteBusComp.setSearchSpec("Id", quoteId);
           quoteBusComp.executeQuery2(true, true);
           if(quoteBusComp.firstRecord()){
               String quoteTotal  = quoteBusComp.getFieldValue("Quote Total");
               MyLogging.log(Level.INFO,"quoteTotal :"+quoteTotal);
               //Add payments but check if record exists first
               paymentBusComp.setViewMode(3);
               paymentBusComp.clearToQuery();
               paymentBusComp.setSearchSpec("Transaction Id", reciptNum);
               paymentBusComp.executeQuery2(true, true);
               if(!paymentBusComp.firstRecord()){
                   paymentBusComp.newRecord(0);
                   paymentBusComp.setFieldValue("Transaction Id", reciptNum);
                   paymentBusComp.setFieldValue("Transaction Amount", transactionAmnt);
                   paymentBusComp.setFieldValue("Payment Method", paymentStatus);
                   paymentBusComp.setFieldValue("Payment Status", paymentMethod);
                   paymentBusComp.setFieldValue("Payment Date", paymentOrclDate);
                   paymentBusComp.writeRecord();
               }else{
                   //paymentBusComp.setFieldValue("Transaction Id", reciptNum);
                   paymentBusComp.setFieldValue("Transaction Amount", transactionAmnt);
                   paymentBusComp.setFieldValue("Payment Method", paymentStatus );
                   paymentBusComp.setFieldValue("Payment Status", paymentMethod);
                   paymentBusComp.setFieldValue("Payment Date", paymentDate);
                   paymentBusComp.writeRecord();
               }               
           }                                                              
        }catch (SiebelException e) {            
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE,"Error in getInvReceipts. Error in siebel connection: " + errors.toString());            
        }
    }
    
    
    private static String convertDate(String dt) throws ParseException{
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);               
        Date date = format.parse(dt);        
        SimpleDateFormat dt1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String formattedDate = dt1.format(date);          
        return formattedDate;
    }
    
    public static void main(String[] args) throws SQLException, IOException {            
            /*try{
               convertDate("2017-01-23 00:00:00.0"); 
            }catch(ParseException ex){
                StringWriter errors = new StringWriter();
                 ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE,"Error in processing"+errors.toString());
            }*/                    
            /*StringWriter errors = new StringWriter();
            String RecProc = "{call plxGetReceiptsByInvId(?,?,?,?,?,?,?)}";            
            CallableStatement callablestatement = ApplicationsConnection.connectToEBSDatabase().prepareCall(RecProc);;
            ReceiptsByINVID test = new ReceiptsByINVID();
            //test.setInvNumber("10779");
            test.setInvNumber("1-150NH");
            test.setInvoiceId("1-4NNO5");
            test.setQuoteId("1-150NH");
            ResultSet rs = test.getInvReceipts(callablestatement);
            try {
            test.processReceipts(rs,ApplicationsConnection.connectSiebelServer());
            } catch (IOException ex) {
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE,"Error in processing"+errors.toString());
            }catch (ParseException ex){
                ex.printStackTrace(new PrintWriter(errors));
                MyLogging.log(Level.SEVERE,"Error in parsing date"+errors.toString());
            }finally {
            if (callablestatement != null) {
            try{
            callablestatement.close();
            }catch(SQLException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE,"Error in getInvReceipts. Error in closing SQL Connection: " + errors.toString());
            }

            }
            }*/
            
            
           
        try {
            SiebelBusObject quoteBusObj = ApplicationsConnection.connectSiebelServer().getBusObject("Contact");
           SiebelBusComp quoteBusComp;
            quoteBusComp = quoteBusObj.getBusComp("Contact");
            //find Quote
           //quoteBusComp.activateField("Quote Total");
           quoteBusComp.setViewMode(3);
           quoteBusComp.clearToQuery();
           quoteBusComp.setSearchSpec("Id", "1-LFIYX");
           quoteBusComp.executeQuery2(true, true);
           if(quoteBusComp.firstRecord()){
               MyLogging.log(Level.INFO, "record found");
               quoteBusComp.activateField("Street Address");
               String prAddrId = quoteBusComp.getFieldValue("Primary Personal Address Id");
               String strt = quoteBusComp.getFieldValue("Street Address");
               MyLogging.log(Level.INFO, "Street addrress::" +strt );
               MyLogging.log(Level.INFO, "prAddrId::" +prAddrId );
               SiebelBusComp bcMVG = quoteBusComp.getMVGBusComp("Personal Street Address");
               bcMVG.setViewMode(3);
               bcMVG.clearToQuery();
               bcMVG.setSearchSpec("Id", prAddrId);
               bcMVG.executeQuery2(true, true);
               if(bcMVG.firstRecord()){
                   MyLogging.log(Level.INFO, "record found2");
                   MyLogging.log(Level.INFO, bcMVG.getFieldValue("Street Address"));
                   
               }
           }
        } catch (SiebelException ex) {
            Logger.getLogger(ReceiptsByINVID.class.getName()).log(Level.SEVERE, null, ex);
        }
           
           
        
    }
    
}
