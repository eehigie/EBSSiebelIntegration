
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class EBSCustomerInfo {
    
    private String CustomerProfileClass;
    private String CustomerTaxReference;
    private String CustomerReceivableAccount;
    private String CustomerRevenueAccount;
    private String CustomerCategoryCode;
    private String CustomerClassification;
    private String CustomerType;
    private String CustomerPartyType;
    private String customerEBSId;
    private Connection ebsConnection;
    
    
    
    public EBSCustomerInfo() {
    }
    
    
    
    
    public EBSCustomerInfo(String customerEBSId,Connection ebsConnection){
        
        this.customerEBSId = customerEBSId;
        this.ebsConnection = ebsConnection;
        
    }
    
    public void processCustomerProfileClass(){
        runSQLPartyType(this.customerEBSId,this.ebsConnection);
        runSQLCustomerClassAndCustomerType(this.customerEBSId,this.ebsConnection);        
    }
    
    public String getCustomerProfileClass() {
        return CustomerProfileClass;
    }

    public void setCustomerProfileClass(String CustomerProfileClass) {
        this.CustomerProfileClass = CustomerProfileClass;
    }

    public String getCustomerTaxReference() {
        return CustomerTaxReference;
    }

    public void setCustomerTaxReference(String CustomerTaxReference) {
        this.CustomerTaxReference = CustomerTaxReference;
    }

    public String getCustomerReceivableAccount() {
        return CustomerReceivableAccount;
    }

    public void setCustomerReceivableAccount(String CustomerReceivableAccount) {
        this.CustomerReceivableAccount = CustomerReceivableAccount;
    }

    public String getCustomerRevenueAccount() {
        return CustomerRevenueAccount;
    }

    public void setCustomerRevenueAccount(String CustomerRevenueAccount) {
        this.CustomerRevenueAccount = CustomerRevenueAccount;
    }

    public String getCustomerCategoryCode() {
        return CustomerCategoryCode;
    }

    public void setCustomerCategoryCode(String CustomerCategoryCode) {
        this.CustomerCategoryCode = CustomerCategoryCode;
    }

    public String getCustomerClassification() {
        return CustomerClassification;
    }

    public void setCustomerClassification(String CustomerClassification) {
        this.CustomerClassification = CustomerClassification;
    }

    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String CustomerType) {
        this.CustomerType = CustomerType;
    }

    public String getCustomerPartyType() {
        return CustomerPartyType;
    }

    public void setCustomerPartyType(String CustomerPartyType) {
        this.CustomerPartyType = CustomerPartyType;
    }
    
    
    
    public void runSQLPartyType(String ebsId,Connection ebsConnection){
        String partyType = "";
        String sqlStmtPartyType = "select party_type from HZ_PARTIES where PARTY_ID='"+ebsId+"'";
        MyLogging.log(Level.INFO, "sqlStmtPartyType is:::"+ sqlStmtPartyType);
        try {
            Statement stmt = ebsConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStmtPartyType);
            while (rs.next()) {
                partyType = rs.getString(1);               
            }
        } catch (SQLException ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "In runSQLPartyType::: "+ errors.toString());
        }
        
        this.CustomerPartyType = partyType;
    }
    
    
    public void runSQLCustomerClassAndCustomerType(String ebsId,Connection ebsConnection){
        String customerClassCode = "";
        String customerType = "";
        String custType = "";
        String sqlStmt = "select CUSTOMER_CLASS_CODE,CUSTOMER_TYPE from HZ_CUST_ACCOUNTS where PARTY_ID='"+ebsId+"'";
        MyLogging.log(Level.INFO, "sqlStmt is:::"+ sqlStmt);
        try {
            Statement stmt = ebsConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStmt);
            while (rs.next()) {
                customerClassCode = rs.getString(1); 
                custType = rs.getString(2);
            }
            MyLogging.log(Level.INFO, "customerClassCode is:::"+ customerClassCode);
            MyLogging.log(Level.INFO, "custType is:::"+ custType);
            sqlStmt = "select meaning\n" +
                      "  from fnd_lookup_values\n" +
                      "Where lookup_type = 'CUSTOMER_TYPE' and LOOKUP_CODE = \n" +
                      "(select CUSTOMER_TYPE from HZ_CUST_ACCOUNTS where PARTY_ID='"+ebsId+"')";
            MyLogging.log(Level.INFO, "sqlStmt is:::"+ sqlStmt);
            rs = stmt.executeQuery(sqlStmt);
            while (rs.next()) {
                customerType = rs.getString(1);                 
            }
            MyLogging.log(Level.INFO, "customerType is:::"+ customerType);
        } catch (SQLException ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            MyLogging.log(Level.SEVERE, "In runSQLDouble::: "+ errors.toString());
        }
        this.CustomerType =   customerType;
        this.CustomerClassification = customerClassCode;
    }
    
    
    public static void main(String[] args) {
        Connection ebsConn = ApplicationsConnection.connectToEBSDatabase();        
        EBSCustomerInfo eci = new EBSCustomerInfo("71248",ebsConn);
        eci.processCustomerProfileClass(); 
        MyLogging.log(Level.INFO, "CustomerType is:"+ eci.getCustomerType() );  
        MyLogging.log(Level.INFO, "CustomerClassification is:"+ eci.getCustomerClassification());
        MyLogging.log(Level.INFO, "CustomerPartyType is:"+ eci.getCustomerPartyType());  
        
        try {
            ebsConn.close();
        } catch (SQLException ex) {
            Logger.getLogger(EBSCustomerInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
