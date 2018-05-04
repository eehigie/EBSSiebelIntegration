
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
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
public class EBSInvoiceCreation {
    
    private static int user_id ;
    private static int resp_id;
    private int trx_header_id;
    private int policycontext;
    private int respapplid;
    private int batchsourceid;
    private int custAcctSiteId;
    private StringWriter errors = new StringWriter();
    private SiebelDataBean sdbObj;
    private Connection ebsDBConn;
            
    public EBSInvoiceCreation(SiebelDataBean sdb,Connection conn) {      
            this.sdbObj = sdb;
            this.ebsDBConn = conn;
        
    }
    
    public EBSInvoiceCreation(int user_id,int resp_id, int respapplid, int policycontext, int batchsourceid,int trx_header_id, SiebelDataBean sdb,Connection conn)  {
        this.resp_id = resp_id;
        this.user_id = user_id;
        this.respapplid = respapplid;
        this.trx_header_id = trx_header_id;
        this.policycontext = policycontext;
        this.batchsourceid = batchsourceid;
        this.sdbObj = sdb;
        this.ebsDBConn = conn;
    }
    
    public String createInvoiceSQLScriptHeader(){
        MyLogging.log(Level.INFO, "createInvoiceSQLScriptHeader...");
        String sqlscriptHeader = "DECLARE\n" +
        "l_customer_trx_id number;\n" +
        "l_return_status     varchar2(1);\n" +
        "l_msg_count         number;\n" +
        "l_msg_data          varchar2(2000);\n" +
        "l_batch_id          number;  \n" +
        "l_cnt               number := 0;\n" +
        "l_batch_source_rec  ar_invoice_api_pub.batch_source_rec_type;\n" +
        "l_trx_header_tbl    ar_invoice_api_pub.trx_header_tbl_type;\n" +
        "l_trx_lines_tbl     ar_invoice_api_pub.trx_line_tbl_type;\n" +
        "l_trx_dist_tbl      ar_invoice_api_pub.trx_dist_tbl_type;\n" +
        "l_trx_salescredits_tbl  ar_invoice_api_pub.trx_salescredits_tbl_type;\n" +        
        "cnt number; \n" +
        "v_context varchar2(100);\n" +
        "\n" +
        "BEGIN";
        
        return sqlscriptHeader;
    }
    
    
    public String createInvoiceHeader(int bill_to_customer_id,int cust_trx_type_id,int primary_salesrep_id, int custAcctSiteId, String trx_currency){        
        MyLogging.log(Level.INFO, "createInvoiceHeader...");
        String invoiceHeader = "fnd_global.apps_initialize("+user_id+", "+resp_id+","+respapplid+",0);        \n" +
        "      mo_global.init ('AR');   \n" +
        "      mo_global.set_policy_context ('S',"+policycontext+"); \n"+
        "      l_trx_header_tbl(1).trx_header_id := "+trx_header_id+";     \n" +
        "      l_trx_header_tbl(1).bill_to_customer_id := "+bill_to_customer_id+";\n" +
        "      l_trx_header_tbl(1).cust_trx_type_id := "+cust_trx_type_id+";\n" +        
        "      l_trx_header_tbl(1).primary_salesrep_id := "+primary_salesrep_id+";\n" +
        "      l_trx_header_tbl (1).trx_currency := '"+trx_currency+"';            \n" +
        "      l_batch_source_rec.batch_source_id := "+batchsourceid+";";
        MyLogging.log(Level.INFO, "InvoiceHeader..."+invoiceHeader);
        return invoiceHeader;
    }
    
    public String createInvoiceOrderItemsBody(String order_id, String term_name){
        
        String Product;
        String Quantity;
        String ItemPriceDisplay;
        String invoiceItemsBody = "";
        String finvoiceItemsBody = "";
        int InventoryId = 13002;
        int trx_line_id = trx_header_id;
        int line_number = 1;        
        String term_id;        
        try {
            EBSData ed = new EBSData(ebsDBConn);
            term_id = ed.getEBSTermId(term_name);            
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "InvoiceCreateion: createInvoiceOrderItemsBody"+ errors.toString());
        }        
        SiebelService ss = new SiebelService(sdbObj);
        try {
            List<Map> itemsList = ss.getOrderItems(order_id);
            for (int i = 0; i < itemsList.size(); i++) {
		Map tmpMap = itemsList.get(i);    
                Product = (String)tmpMap.get("Product");
                Quantity = (String)tmpMap.get("Quantity");
                ItemPriceDisplay = (String)tmpMap.get("Item Price");
                InventoryId = Integer.valueOf((String)tmpMap.get("Inventory Id"));
                invoiceItemsBody = "l_trx_lines_tbl(1).trx_header_id := "+trx_header_id+";\n" +
                "      l_trx_lines_tbl(1).trx_line_id := "+trx_line_id+";\n" +
                "      l_trx_lines_tbl(1).line_number := "+line_number+";    \n" +
                "      l_trx_lines_tbl(1).inventory_item_id := "+InventoryId+";\n" +
                "      l_trx_lines_tbl(1).quantity_invoiced := "+Quantity+";\n" +
                "      l_trx_lines_tbl(1).quantity_ordered := "+Quantity+";\n" +
                "      l_trx_lines_tbl(1).unit_selling_price := "+ItemPriceDisplay+";\n" +
                "      l_trx_lines_tbl(1).line_type := 'LINE';" ;
                trx_line_id++;
                line_number++;
                if(i == 0){
                    finvoiceItemsBody = invoiceItemsBody;                   
                }else{
                    finvoiceItemsBody = finvoiceItemsBody.concat(invoiceItemsBody);
                }
            }
        } catch (SiebelException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "InvoiceCreateion: createInvoiceOrderItemsBody"+ errors.toString());
        }
        return finvoiceItemsBody;
    }
    
    
    public String createInvoiceQuoteItemsBody(String quote_id, String term_name){
        MyLogging.log(Level.INFO, "createInvoiceQuoteItemsBody...");
        String Product;
        String Quantity;
        String ItemPriceDisplay;
        String invoiceItemsBody = "";
        String finvoiceItemsBody = "";
        int InventoryId = 0;// = 13002;
        String sInventoryId;
        int trx_line_id = trx_header_id;
        int line_number = 1;        
        String term_id;        
        try {
            EBSData ed = new EBSData(ebsDBConn);
            term_id = ed.getEBSTermId(term_name);
            MyLogging.log(Level.INFO," term_id: " + term_id);
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "InvoiceCreateion: getEBSTermId"+ errors.toString());            
        }
        
        SiebelService ss = new SiebelService(sdbObj);
        try {
            MyLogging.log(Level.INFO,"Quote Id: " + quote_id);
            int j= 1;
            List<Map> itemsList = ss.getQuoteItems(quote_id);
            for (int i = 0; i < itemsList.size(); i++) {
		Map tmpMap = itemsList.get(i);    
                Product = (String)tmpMap.get("Product");
                Quantity = (String)tmpMap.get("Quantity");
                ItemPriceDisplay = (String)tmpMap.get("Item Price");
                sInventoryId = (String)tmpMap.get("Inventory Id");
                if(!sInventoryId.isEmpty()){
                    InventoryId = Integer.valueOf((String)tmpMap.get("Inventory Id"));
                }               
                invoiceItemsBody = "l_trx_lines_tbl("+j+").trx_header_id := "+trx_header_id+";\n" +
                "      l_trx_lines_tbl("+j+").trx_line_id := "+trx_line_id+";\n" +
                "      l_trx_lines_tbl("+j+").line_number := "+line_number+";    \n" +
                "      l_trx_lines_tbl("+j+").inventory_item_id := "+InventoryId+";\n" +
                "      l_trx_lines_tbl("+j+").quantity_invoiced := "+Quantity+";\n" +
                "      l_trx_lines_tbl("+j+").quantity_ordered := "+Quantity+";\n" +
                "      l_trx_lines_tbl("+j+").unit_selling_price := "+ItemPriceDisplay+";\n" +
                "      l_trx_lines_tbl("+j+").line_type := 'LINE';" ;
                trx_line_id++;
                line_number++;
                j++;
                if(i == 0){
                    finvoiceItemsBody = invoiceItemsBody;                   
                }else{
                    finvoiceItemsBody = finvoiceItemsBody.concat(invoiceItemsBody);
                }
            }            
        } catch (SiebelException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "InvoiceCreateion: getEBSTermId"+ errors.toString());            
        }
        MyLogging.log(Level.INFO," InvoiceQuoteItemsBody: " + finvoiceItemsBody);
        return finvoiceItemsBody;
    }
    
    public String createInvoiceSQLScriptFooter(){
        MyLogging.log(Level.INFO, "createInvoiceSQLScriptFooter...");
        String sqlScriptFooter = "AR_INVOICE_API_PUB.create_single_invoice(\n" +
        "        p_api_version           => 1.0,\n" +
        "        p_batch_source_rec      => l_batch_source_rec,\n" +
        "        p_trx_header_tbl        => l_trx_header_tbl,\n" +
        "        p_trx_lines_tbl         => l_trx_lines_tbl,\n" +
        "        p_trx_dist_tbl          => l_trx_dist_tbl,\n" +
        "        p_trx_salescredits_tbl  => l_trx_salescredits_tbl,\n" +
        "        x_customer_trx_id       => l_customer_trx_id,\n" +
        "        x_return_status         => l_return_status,\n" +
        "        x_msg_count             => l_msg_count,\n" +
        "        x_msg_data              => l_msg_data);\n" +                
        "?:=l_customer_trx_id;\n" +
        "END;";
        
        return sqlScriptFooter;
    }
    
    public String createSQLInvoiceScript(){
        String hdr = createInvoiceSQLScriptHeader();
        String bdy = createInvoiceOrderItemsBody("1-JQXX","30 NET");
        String ftr = createInvoiceSQLScriptFooter();
        
        return hdr +"\n"+bdy+"\n"+ftr;
    }
    
    public static void main(String[] args){
        String term_id;
        String cust_trx_type_id="";
        
        try {
            Connection theebsDBConn = ApplicationsConnection.connectToEBSDatabase();
            SiebelDataBean thesdb = ApplicationsConnection.connectSiebelServer();
            EBSData ed = new EBSData(theebsDBConn);
            term_id = ed.getEBSTermId("NET 30");
            cust_trx_type_id = ed.getEBSCustTrxTypeId("VEHICLE NONDEALERS");            
            EBSInvoiceCreation eic = new EBSInvoiceCreation(1187,50678,222,101,1002,101,thesdb,theebsDBConn);
            String hdr = eic.createInvoiceSQLScriptHeader();
            String invhdr = eic.createInvoiceHeader(20107, Integer.parseInt(cust_trx_type_id), 224,10078, "NGN");
            //String bdy = eic.createInvoiceOrderItemsBody("1-KS36","30 NET");
            String bdy = eic.createInvoiceQuoteItemsBody("1-150NH", "NET 30");
            String ftr = eic.createInvoiceSQLScriptFooter();
        
            String sqlSCript = hdr +"\n"+invhdr+"\n"+bdy+"\n"+ftr;
            MyLogging.log(Level.INFO, "Script: " +sqlSCript);
        } catch (IOException ex) {
            Logger.getLogger(EBSInvoiceCreation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
