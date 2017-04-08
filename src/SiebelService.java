
import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    
    
    public static void main(String[] args){
        
        try {
            //1-LQ82
            //ss.getOrderItems("1-KS36");
            SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
            SiebelService ss = new SiebelService(sdb);
            ss.getQuoteItems("1-19ZD2");
            sdb.logoff();
        } catch (SiebelException ex) {
            MyLogging.log(Level.SEVERE, "In main method", ex);
        } catch (IOException ex) {
            Logger.getLogger(SiebelService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
