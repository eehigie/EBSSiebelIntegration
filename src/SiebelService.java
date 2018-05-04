
import com.plexadasi.account.EBSAccount;
import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import com.siebel.eai.SiebelBusinessServiceException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SiebelService {    
    private SiebelDataBean sdb;
    private SiebelServiceFactory ssf;
    private StringWriter errors = new StringWriter();
    private String theEntity;
    
    public SiebelService(SiebelDataBean sdb) {
        this.sdb = sdb;        
    }
    
    public List<Map> getOrderRecord(String order_id) throws SiebelException{
        /*try {
            //sdb = ssf.ConnectSiebelServer();
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "In getOrderRecord method. Error in connecting to Siebel"+ errors.toString());
        }*/
        Map order = new HashMap();
        List<Map> orderList = new ArrayList<Map>();
        SiebelBusObject orderBusObject = sdb.getBusObject("Order Entry (Sales)");
        SiebelBusComp orderBusComp = orderBusObject.getBusComp("Order Entry - Orders");
        orderBusComp.setViewMode(3);
        orderBusComp.clearToQuery();
        orderBusComp.activateField("Id");
        orderBusComp.activateField("Order Number");
        orderBusComp.activateField("Currency Code");
        orderBusComp.setSearchSpec("Id", order_id);
        orderBusComp.executeQuery2(true,true);
        if (orderBusComp.firstRecord()) {
            order.put("Order Number", orderBusComp.getFieldValue("Order Number"));
            order.put("Currency Code", orderBusComp.getFieldValue("Currency Code"));
            orderList.add(order);
            MyLogging.log(Level.INFO,"Order Number is: {0}"+orderBusComp.getFieldValue("Order Number"));                     
            MyLogging.log(Level.INFO,"Currency Code is: {0}"+orderBusComp.getFieldValue("Currency Code"));
        }
        orderBusComp.release();        
        orderBusObject.release();
        //sdb.logoff();
        
        return orderList;
    }
    
    public List<Map> getOrderItems(String order_id)throws SiebelException{
        /*try {
            sdb = ssf.ConnectSiebelServer();
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));            
            MyLogging.log(Level.SEVERE, "In getItems method. Error in connecting to Siebel"+ errors.toString());
        }*/
        MyLogging.log(Level.INFO,"Creating siebel objects");
        Map orderItems = new HashMap();
        List<Map> orderItemsList = new ArrayList<Map>();
        SiebelBusObject orderBusObject = sdb.getBusObject("Order Entry (Sales)");
        SiebelBusComp orderBusComp = orderBusObject.getBusComp("Order Entry - Orders");
        SiebelBusComp lineItemsBusComp = orderBusObject.getBusComp("Order Entry - Line Items"); 
        boolean isRecord;
        int cnt = 0;
        orderBusComp.setViewMode(3);
        orderBusComp.clearToQuery();
        orderBusComp.activateField("Id");
        orderBusComp.activateField("Order Number");
        orderBusComp.setSearchSpec("Id", order_id);
        orderBusComp.executeQuery2(true,true);
        if (orderBusComp.firstRecord()) {            
            lineItemsBusComp.setViewMode(3);
            lineItemsBusComp.clearToQuery();
            lineItemsBusComp.activateField("Product");
            lineItemsBusComp.activateField("Quantity");
            lineItemsBusComp.activateField("Item Price - Display");
            lineItemsBusComp.activateField("Product Inventory Item Id");
            lineItemsBusComp.activateField("Order Header Id");
            lineItemsBusComp.setSearchSpec("Order Header Id", order_id);
            lineItemsBusComp.executeQuery2(true,true);
            isRecord = lineItemsBusComp.firstRecord();
            while(isRecord){
                cnt++;
                MyLogging.log(Level.INFO,"Record:{0}"+cnt);                
                orderItems.put("Product", lineItemsBusComp.getFieldValue("Product"));
                MyLogging.log(Level.INFO,"Product:{0}"+lineItemsBusComp.getFieldValue("Product")); 
                orderItems.put("Quantity",lineItemsBusComp.getFieldValue("Quantity"));
                MyLogging.log(Level.INFO,"Quantity:{0}"+lineItemsBusComp.getFieldValue("Quantity")); 
                orderItems.put("Item Price",lineItemsBusComp.getFieldValue("Item Price - Display"));
                MyLogging.log(Level.INFO,"Item Price:{0}"+lineItemsBusComp.getFieldValue("Item Price")); 
                orderItems.put("Inventory Id",lineItemsBusComp.getFieldValue("Product Inventory Item Id"));
                MyLogging.log(Level.INFO,"Inventory Id:{0}"+lineItemsBusComp.getFieldValue("Product Inventory Item Id")); 
                orderItemsList.add(orderItems);
                isRecord = lineItemsBusComp.nextRecord();
            }
            
        }
        lineItemsBusComp.release();
        orderBusComp.release();
        orderBusObject.release();
        //sdb.logoff();
        
        return orderItemsList;
    }
    
    public List<Map> getQuoteItems(String quote_id)throws SiebelException{
        /*try {
            sdb = ssf.ConnectSiebelServer();
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));            
            MyLogging.log(Level.SEVERE, "In getQuoteItems method. Error in connecting to Siebel"+ errors.toString());
        }*/
        MyLogging.log(Level.INFO,"Creating siebel objects");
        
        List<Map> quoteItemsList = new ArrayList<Map>();
        SiebelBusObject quoteBusObject = sdb.getBusObject("Quote");
        SiebelBusComp quoteBusComp = quoteBusObject.getBusComp("Quote");
        SiebelBusComp lineItemsBusComp = quoteBusObject.getBusComp("Quote Item"); 
        boolean isRecord;
        int cnt = 0;
        quoteBusComp.setViewMode(3);
        quoteBusComp.clearToQuery();
        quoteBusComp.activateField("Id");
        quoteBusComp.activateField("Order Number");
        quoteBusComp.setSearchSpec("Id", quote_id);
        quoteBusComp.executeQuery2(true,true);
        if (quoteBusComp.firstRecord()) {            
            lineItemsBusComp.setViewMode(3);
            lineItemsBusComp.clearToQuery();
            lineItemsBusComp.activateField("Product");
            lineItemsBusComp.activateField("Quantity Requested");
            lineItemsBusComp.activateField("Item Price - Display");
            lineItemsBusComp.activateField("Net Price");            
            lineItemsBusComp.activateField("Product Inventory Item Id");
            lineItemsBusComp.activateField("Extended Line Total - Display");
            lineItemsBusComp.activateField("Quote Id");
            lineItemsBusComp.setSearchSpec("Quote Id", quote_id);
            lineItemsBusComp.executeQuery2(true,true);
            isRecord = lineItemsBusComp.firstRecord();
            while(isRecord){
                cnt++;
                Map quoteItems = new HashMap();
                MyLogging.log(Level.INFO,"Record:{0}"+cnt);                
                quoteItems.put("Product", lineItemsBusComp.getFieldValue("Product"));
                MyLogging.log(Level.INFO,"Product:{0}"+lineItemsBusComp.getFieldValue("Product")); 
                quoteItems.put("Quantity",lineItemsBusComp.getFieldValue("Quantity Requested"));
                MyLogging.log(Level.INFO,"Quantity:{0}"+lineItemsBusComp.getFieldValue("Quantity Requested")); 
                //orderItems.put("Item Price",lineItemsBusComp.getFieldValue("Item Price - Display"));
                //LOG.log(Level.INFO,"Item Price:{0}",lineItemsBusComp.getFieldValue("Item Price")); 
                quoteItems.put("Item Price",lineItemsBusComp.getFieldValue("Item Price"));
                MyLogging.log(Level.INFO,"Item Price:{0}"+lineItemsBusComp.getFieldValue("Item Price")); 
                quoteItems.put("Inventory Id",lineItemsBusComp.getFieldValue("Product Inventory Item Id"));
                MyLogging.log(Level.INFO,"Inventory Id:{0}"+lineItemsBusComp.getFieldValue("Product Inventory Item Id")); 
                quoteItemsList.add(quoteItems);
                isRecord = lineItemsBusComp.nextRecord();
            }
            
        }
        lineItemsBusComp.release();
        quoteBusComp.release();
        quoteBusObject.release();
        //sdb.logoff();
        
        return quoteItemsList;
    }
    
    public boolean updateCustomerRecordWithEBSId(String entity,String siebelId,String ebsId) throws SiebelException{
        MyLogging.log(Level.INFO,"updateCustomerRecordWithEBSId::||entity::"+entity+"||siebelId::"+siebelId+"||ebsId::"+ebsId); 
        SiebelBusObject objBusObject = this.sdb.getBusObject(entity);
        SiebelBusComp objBusComp = objBusObject.getBusComp(entity);
        objBusComp.setViewMode(3);
        objBusComp.clearToQuery();
        objBusComp.activateField("Id");        
        objBusComp.setSearchSpec("Id", siebelId);
        objBusComp.executeQuery2(true,true);
        if (objBusComp.firstRecord()) {  
            MyLogging.log(Level.INFO,"record found, updating ....");
            objBusComp.setFieldValue("EBS Id",ebsId);
            objBusComp.writeRecord();
        }else{
            MyLogging.log(Level.INFO,"RECORD NOT FOUND!! ....");
            return false;
        }
        objBusComp.release();
        objBusObject.release();        
        return true;
    }
    
    public boolean updateCustomerRecordWithShipToBillToId(String entity,String siebelId,String ebsBillToId,String ebsShipToId) throws SiebelException{
        MyLogging.log(Level.INFO,"updateCustomerRecordWithEBSId::||entity::"+entity+"||siebelId::"+siebelId+"||ebsBillToId::"+ebsBillToId+"||ebsShipToId::"+ebsShipToId); 
        SiebelBusObject objBusObject = this.sdb.getBusObject(entity);
        SiebelBusComp objBusComp = objBusObject.getBusComp(entity);
        objBusComp.setViewMode(3);
        objBusComp.clearToQuery();
        objBusComp.activateField("Id");        
        objBusComp.setSearchSpec("Id", siebelId);
        objBusComp.executeQuery2(true,true);
        if (objBusComp.firstRecord()) {      
            MyLogging.log(Level.INFO,"record found, updating ....");
            objBusComp.setFieldValue("PLX EBS Bill To Id",ebsBillToId);
            objBusComp.setFieldValue("PLX EBS Ship To Id",ebsShipToId);
            objBusComp.writeRecord();
        }else{
            MyLogging.log(Level.INFO,"RECORD NOT FOUND!! ....");
            return false;
        }
        objBusComp.release();
        objBusObject.release();        
        return true;
    }
    
    public boolean isThereShipToBillToId(String siebelId,String entity) throws SiebelException{
        MyLogging.log(Level.INFO,"isThereShipToBillToId::||siebelId::"+siebelId+"::||entity::"+entity);
        String shipTo = "";
        String billTo = "";
        SiebelBusObject objBusObject = this.sdb.getBusObject(entity);
        SiebelBusComp objBusComp = objBusObject.getBusComp(entity);
        objBusComp.setViewMode(3);
        objBusComp.clearToQuery();
        objBusComp.activateField("Id");        
        objBusComp.setSearchSpec("Id", siebelId);
        objBusComp.executeQuery2(true,true);
        if (objBusComp.firstRecord()) {      
            MyLogging.log(Level.INFO,"record found, updating ....");
            billTo = objBusComp.getFieldValue("PLX EBS Bill To Id");
            shipTo = objBusComp.getFieldValue("PLX EBS Ship To Id");
            MyLogging.log(Level.INFO, "billTo::" + billTo);
            MyLogging.log(Level.INFO, "shipTo::" + shipTo);
            if(billTo.isEmpty() || shipTo.isEmpty() )
                return false;
        }else{
            return false;
        }
        objBusComp.release();
        objBusObject.release();
        
        return true;
    }
    
    public void addLocationToCustomer(String siebelId,String x_cust_account_id,String siebelAccountType,Connection conntn) throws SiebelBusinessServiceException{
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
        ebsa.doInvoke(siebelId, x_cust_account_id,siebelAccountType,sdb,conntn);
        ship_to_id = ebsa.getShipToId();
        bill_to_id = ebsa.getBillToId();
                                              
        //update Siebel record
        MyLogging.log(Level.INFO, "updating siebel record with Ship To  and Bill To Id....");
        try{                        
            updateCustomerRecordWithShipToBillToId(siebelAccountType, siebelId, Integer.toString(bill_to_id), Integer.toString(ship_to_id));
        }catch(SiebelException ex){
            ex.printStackTrace(new PrintWriter(errors));                                                            
            MyLogging.log(Level.SEVERE, "CREATE CUSTOMER ERROR::....."+ errors.toString());
        }
        MyLogging.log(Level.INFO, "Siebel record updated....");
    }
    
    public String getSiebelId(String ebsId) throws SiebelException{
        MyLogging.log(Level.INFO,"getSiebelId::||ebsId::"+ebsId);       
        SiebelBusObject accountBusObject = this.sdb.getBusObject("Account");
        SiebelBusComp accountBusComp = accountBusObject.getBusComp("Account");
        accountBusComp.setViewMode(3);
        accountBusComp.clearToQuery();
        accountBusComp.activateField("Id");        
        accountBusComp.setSearchSpec("EBS Id", ebsId);
        accountBusComp.executeQuery2(true,true);
        if (accountBusComp.firstRecord()) {      
            String row_id = accountBusComp.getFieldValue("Id");
            MyLogging.log(Level.INFO,"Row Id...."+row_id);
            this.theEntity = "Account";
            accountBusComp.release();
            accountBusObject.release();
            return row_id;
        }else{
            SiebelBusObject contactBusObject = this.sdb.getBusObject("Contact");
            SiebelBusComp contactBusComp = contactBusObject.getBusComp("Contact");
            contactBusComp.setViewMode(3);
            contactBusComp.clearToQuery();
            contactBusComp.activateField("Id");        
            contactBusComp.setSearchSpec("EBS Id", ebsId);
            contactBusComp.executeQuery2(true,true);
            if (contactBusComp.firstRecord()) {      
                MyLogging.log(Level.INFO,"Row Id...."+contactBusComp.getFieldValue("Id"));
                this.theEntity = "Contact";
                String con_row_id = contactBusComp.getFieldValue("Id");
                contactBusComp.release();
                contactBusObject.release();
                return con_row_id;
            }
        }
        
        return "";
    }
    
    public String getEntity(){
        return this.theEntity;
    }
    
    public static void main(String[] args){
        
        try {
            //1-LQ82
            //ss.getOrderItems("1-KS36");
            SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
            SiebelService ss = new SiebelService(sdb);
            //ss.getQuoteItems("1-19ZD2");
            boolean foo = ss.isThereShipToBillToId("1-L3J2Q", "Account");
            MyLogging.log(Level.INFO, "foo::" + foo);
            MyLogging.log(Level.INFO, "row_id::" + ss.getSiebelId("54093"));
            MyLogging.log(Level.INFO, "getEntity::" + ss.getEntity());
            sdb.logoff();
        } catch (SiebelException ex) {
            MyLogging.log(Level.SEVERE, "In main method", ex);
        } catch (IOException ex) {
            Logger.getLogger(SiebelService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
