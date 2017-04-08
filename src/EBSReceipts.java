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
import oracle.jdbc.OracleTypes;

public class EBSReceipts {
    public int customerId;
    private String receipt_number;
    private Date creation_date;
    private String status;
    private String receiptType;
    private String currency_code;
    private int amount;
    private StringWriter errors = new StringWriter();
    
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getReceipt_number() {
        return receipt_number;
    }

    public void setReceipt_number(String receipt_number) {
        this.receipt_number = receipt_number;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void getCustReceipts() throws SQLException {
        MyLogging.log(Level.INFO, "In getCustReceipts");
        CallableStatement callablestatement = null;
        String RecProc = "{call PlexGetReceiptsByCustID(?,?,?,?,?,?,?,?)}";

        try {
            //log.debug("Calling Strored Procedure...");
            
            callablestatement = ApplicationsConnection.connectToEBSDatabase().prepareCall(RecProc);
            callablestatement.registerOutParameter(1, OracleTypes.CURSOR);
            callablestatement.setInt(2, customerId);
            callablestatement.registerOutParameter(3, java.sql.Types.VARCHAR);
            callablestatement.registerOutParameter(4, java.sql.Types.DATE);
            callablestatement.registerOutParameter(5, java.sql.Types.VARCHAR);
            callablestatement.registerOutParameter(6, java.sql.Types.VARCHAR);
            callablestatement.registerOutParameter(7, java.sql.Types.VARCHAR);
            callablestatement.registerOutParameter(8, java.sql.Types.INTEGER);
           
            callablestatement.execute();
            ResultSet R= (ResultSet)callablestatement.getObject(1);
                   
            
            
            MyLogging.log(Level.INFO, "Printing Receipts.....");
                     
            MyLogging.log(Level.INFO, "RECEIPT NO " + "|" + " CREATION DATE" + "|" + "      STATUS" + "|" + " RECEIPT TYPE" + "|" + " CURRENCY" +"|"+ "AMOUNT");
            while (R.next()){
               //System.out.println(R.getString(1));
            MyLogging.log(Level.INFO, R.getString(1) + "    |" + R.getString(2) + "    |" + R.getString(3) + "    |" + R.getString(4) + "         |" + R.getString(5) + "      |" + R.getString(6));
           }
        } catch (SQLException e) {
            //log.error("Error in calling Stored Procedure "+e);
            e.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "Error in Create Invoice: "+errors.toString());
        } finally {
            if (callablestatement != null) {
                callablestatement.close();
            }
        }

    }

    public static void main(String[] args) throws SQLException {

        EBSReceipts test = new EBSReceipts();
        test.customerId =1066;
        test.getCustReceipts();
    }
}
