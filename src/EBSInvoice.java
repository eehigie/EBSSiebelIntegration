/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;

public class EBSInvoice {
    
    private int invoiceNumber;
    private int customerId;
    private Date creationDate;
    private String currencyCode;
    private int amountInvoiced;
    private int amountRemaining;
    private StringWriter errors = new StringWriter();
    
    public EBSInvoice() {
        super();
    }
    
     
    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setAmountInvoice(int amountInvoiced) {
        this.amountInvoiced = amountInvoiced;
    }

    public int getAmountInvoice() {
        return amountInvoiced;
    }

    public void setAmountRemaining(int amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public int getAmountRemaining() {
        return amountRemaining;
    }
    

    private void checkInvoice() throws SQLException{ 
            MyLogging.log(Level.INFO, "In Check Invoice");
            CallableStatement callablestatement = null; 
            String invProc = "{call PlexGetInvoiceByID(?,?,?,?,?,?)}";
            
            try{
                //log.debug("Calling Strored Procedure...");
                callablestatement = ApplicationsConnection.connectToEBSDatabase().prepareCall(invProc);                                     
                callablestatement.setInt(1, invoiceNumber);
                callablestatement.registerOutParameter(2, java.sql.Types.INTEGER);
                callablestatement.registerOutParameter(3, java.sql.Types.DATE);
                callablestatement.registerOutParameter(4, java.sql.Types.VARCHAR);
                callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);
                callablestatement.registerOutParameter(6, java.sql.Types.INTEGER);
                ResultSet R=callablestatement.executeQuery();
                creationDate = callablestatement.getDate(3);
                currencyCode = callablestatement.getString(4);
                amountInvoiced = callablestatement.getInt(5);
                amountRemaining = callablestatement.getInt(6);
                MyLogging.log(Level.INFO, "Printing Invoice Information...");
                MyLogging.log(Level.INFO, "INVOICE ID:"+invoiceNumber+"|"+" CREATION DATE:" +creationDate+"|"+ " CURRENCY CODE:"+ currencyCode+"|"+ " AMOUNT INVOICED :"+amountInvoiced+"|"+ " AMOUNT REMAINING:" + amountRemaining);
               // System.out.println("there is data");
                
            }catch(SQLException e){
                 //log.error("Error in calling Stored Procedure "+e);
                 e.printStackTrace(new PrintWriter(errors));
                 MyLogging.log(Level.SEVERE, "Error in Create Invoice: "+errors.toString());
            }finally{
                if(callablestatement != null){
                    callablestatement.close();
                }
            }
        }
        
    
   
public static void main (String [] args) throws SQLException{

EBSInvoice test = new EBSInvoice();
test.invoiceNumber =10178;
test.checkInvoice();
}
    
}
