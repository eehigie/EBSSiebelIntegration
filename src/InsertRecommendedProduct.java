/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import com.siebel.data.SiebelPropertySet;
import com.siebel.data.SiebelService;
import com.siebel.eai.SiebelBusinessService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SAP Training
 */
public class InsertRecommendedProduct {
    private static StringWriter errors = new StringWriter();
    
    
    private static String getProductId(SiebelDataBean sdb, String partNumber){
        
         
        try {
            SiebelBusObject buObject = sdb.getBusObject("Admin ISS Product Definition");
            SiebelBusComp prodBusComp = buObject.getBusComp("Internal Product - ISS Admin");
        } catch (SiebelException ex) {
            Logger.getLogger(InsertRecommendedProduct.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return "";
    }
    
    public static void insertRelatedProduct(String ParentProductPartNum, String ReplacementProductPartNum, String ReplacementType, String OrganizationNumber) throws IOException, SiebelException{
        SiebelDataBean sdb = ApplicationsConnection.connectSiebelServer();
        SiebelService sbs = sdb.getService("PLX Product Replacement Service");
        SiebelPropertySet inputs = sdb.newPropertySet();
        SiebelPropertySet outputs = sdb.newPropertySet();
        try {                                    
            inputs.setProperty("ParentProductPartNum", ParentProductPartNum);
            inputs.setProperty("ReplacementProductPartNum", ReplacementProductPartNum);
            inputs.setProperty("ReplacementType", ReplacementType);
            inputs.setProperty("OrganizationNumber", OrganizationNumber);
            sbs.invokeMethod("InsertProductReplacement", inputs, outputs);
        } catch(SiebelException ex){
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "insertRelatedProduct::....."+ errors.toString());
        }finally{
            outputs = null;
            inputs = null;
            sbs = null;
            sdb.logoff();
        }
        
    }
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            insertRelatedProduct("A9484905019", "A9484905019", "Substitute", "123");
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "IOException::....."+ errors.toString());
        } catch (SiebelException ex) {
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "SiebelException::....."+ errors.toString());
        }
        
    }
    
}
