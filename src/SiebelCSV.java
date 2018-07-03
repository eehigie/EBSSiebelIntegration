/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.opencsv.CSVWriter; 
import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import com.siebel.data.SiebelPropertySet;
import com.siebel.data.SiebelService;
//import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
//import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author SAP Training
 */
public class SiebelCSV {

    /**
     * @param args the command line arguments
     */
    private final StringWriter errors = new StringWriter();
    private FileWriter writer;

    public SiebelCSV() {
        this.writer = null;
    }
    
    public void WriteSiebelDataToCSV(String PriceListId,String McId, String filePath,SiebelDataBean sdb){
        try {
            MyLogging.log(Level.INFO, "In WriteSiebelDataToCSV...");
            String priceListItemId;
            String productMarketCode;
            String txtLine; 
            boolean isRec;
            MyLogging.log(Level.INFO, "McId...:"+McId);
            //List<String> siebelDataArray = new ArrayList<String>();
            //siebelDataArray.add(0, "# Price List Item");
            //siebelDataArray.add(1, "# Price List Item.Id,Price List Item.PLX MC Id");
            
            //CSVWriter csvWriter;
            //csvWriter = new CSVWriter(new FileWriter(filePath),CSVWriter.DEFAULT_SEPARATOR,CSVWriter.NO_QUOTE_CHARACTER);            
            //csvWriter = new CSVWriter(new FileWriter(filePath),CSVWriter.NO_QUOTE_CHARACTER);            
            //csvWriter.writeNext(new String[]{"# PLX Price List Item"});
            //csvWriter.writeNext(new String[]{"# PLX Price List Item.Id,PLX Price List Item.PLX MC Id,Price List Item.PLX Exch Market Code"});
            
            
            
            writer = new FileWriter(filePath);
            writer.append("# PLX Price List Items Update");            
            writer.append('\n');            
            writer.append("# Price List Item.Id,Price List Item.PLX MC Id,Price List Item.PLX Exch Market Code");
            writer.append('\n');
            
            MyLogging.log(Level.INFO, "Connecting to Siebel...");            
            SiebelBusObject priceListBO =  sdb.getBusObject("Admin Price List");
            SiebelBusComp priceListItem = priceListBO.getBusComp("Price List Item");
            priceListItem.activateField("PLX Exch Market Code");
            priceListItem.setViewMode(3);
            priceListItem.clearToQuery();
            priceListItem.setSearchSpec("Price List Id", PriceListId);            
            priceListItem.executeQuery(true);
            
            isRec = priceListItem.firstRecord();
            MyLogging.log(Level.INFO, "isRec::"+isRec);
            int cnt = 0;
            while(isRec){
                priceListItemId = priceListItem.getFieldValue("Id");
                productMarketCode = priceListItem.getFieldValue("PLX Exch Market Code");
                txtLine = priceListItemId+","+McId+","+productMarketCode;                
                //csvWriter.writeNext(new String[]{txtLine}); 
                writer.append(txtLine);
                writer.append('\n');
                isRec = priceListItem.nextRecord();
                cnt = cnt +1;
                MyLogging.log(Level.INFO, "count::"+cnt);
                MyLogging.log(Level.INFO, "txtLine... "+txtLine);
            }
                        
            //csvWriter.close();          
            MyLogging.log(Level.INFO, "CSV created");
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));                                                            
            MyLogging.log(Level.SEVERE, "GetSiebelData ERROR::....."+ errors.toString());
        } catch (SiebelException ex){
            ex.printStackTrace(new PrintWriter(errors));                                                            
            MyLogging.log(Level.SEVERE, "GetSiebelData::SiebelException::ERROR::....."+ errors.toString());
        }finally{
            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace(new PrintWriter(errors)); 
                MyLogging.log(Level.SEVERE, "GetSiebelData::IOException::ERROR::....."+ errors.toString());
            }
           
        }
    }
    
    
    
    
    public void UpdateRecordsWithCSVFile(String csvFile, SiebelDataBean sdb) throws SiebelException{
        
        SiebelService wkflwService = sdb.getService("Workflow Process Manager");
        SiebelPropertySet inPS = sdb.newPropertySet();
        SiebelPropertySet outPS = sdb.newPropertySet();
        
        inPS.setProperty("ProcessName", "PLX Price List Item Update Workflow");
        inPS.setProperty("FileName", csvFile);
        
        wkflwService.invokeMethod("RunProcess", inPS, outPS);
                        
    }
    
    private  String csvWriterAll(List<String[]> stringArray, String filePath) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        writer.writeAll(stringArray);
        writer.close();
        //return Helpers.readFile(path);
        return "";
    }
    
    public void WriteToCSV(String[] siebelData, CSVWriter csvWriter){        
        try {
            csvWriter.writeNext(siebelData);
            csvWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));                                                            
            MyLogging.log(Level.SEVERE, "WriteToCSV::IOException::ERROR::....."+ errors.toString());
        }
    }
    
    public static void main(String[] args) {
        
        String priceListId = args[0];
	String mcId = args[1];
	String filePath = args[2];
        MyLogging.log(Level.INFO, "mcId...:"+mcId);   
        MyLogging.log(Level.INFO, "filePath...:"+filePath);
        
        try {
            SiebelCSV scsv = new SiebelCSV();
            SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
            //PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            scsv.WriteSiebelDataToCSV(priceListId,mcId,filePath,sdb);
            //scsv.UpdateRecordsWithCSVFile(filePath, sdb);
            sdb.logoff();
            //PrintWriter writer = new PrintWriter("/usr/app/siebel/intg/price_files/price_files.csv", "UTF-8");
            //new SiebelCSV().WriteSiebelDataToCSV("1-82OPI","1-564821","/usr/app/siebel/intg/price_files/price_files.csv");
            //PrintWriter writer = new PrintWriter("C:\\TEMP\\intg\\price_files\\price_files.csv", "UTF-8");
            //new SiebelCSV().WriteSiebelDataToCSV("1-82OPI","1-564821","C:\\TEMP\\intg\\price_files\\price_files.csv");
            /*CSVWriter csvWriter;
            csvWriter = new CSVWriter(new FileWriter("example.csv"));
            csvWriter.writeNext(new String[]{"1", "jan", "Male", "20"});
            csvWriter.writeNext(new String[]{"2", "con", "Male", "24"});
            csvWriter.writeNext(new String[]{"3", "jane", "Female", "18"});
            csvWriter.writeNext(new String[]{"4", "ryo", "Male", "28"});
            csvWriter.close();*/
        } catch (IOException ex) {
            Logger.getLogger(SiebelCSV.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SiebelException ex) {
            Logger.getLogger(SiebelCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
