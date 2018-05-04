/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */
public class GetInvoiceReceipts {
    private String invoiceNumber;
    
    public GetInvoiceReceipts(String invoiceNumber ){
        this.invoiceNumber = invoiceNumber;
    }
    
    public boolean GetAndInsertReceipts(){
        EBSReceipts er = new EBSReceipts();
        er.customerId =1066;
        return true;
    }
}
