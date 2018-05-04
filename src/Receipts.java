/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */
public class Receipts {
    public String reciptNum;
    public String amountPaid;
    public String reciptDate;
    public String amountApplied;
    public String paymentMethod;
    public String paymentStatus;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Receipts(String reciptNum, String amountPaid, String reciptDate, String paymentMethod,String paymentStatus) {
        this.reciptNum = reciptNum;
        this.amountPaid = amountPaid;
        this.reciptDate = reciptDate;        
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }
    
    
    
    public String getReciptNum() {
        return reciptNum;
    }

    public void setReciptNum(String reciptNum) {
        this.reciptNum = reciptNum;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getReciptDate() {
        return reciptDate;
    }

    public void setReciptDate(String reciptDate) {
        this.reciptDate = reciptDate;
    }

    public String getAmountApplied() {
        return amountApplied;
    }

    public void setAmountApplied(String amountApplied) {
        this.amountApplied = amountApplied;
    }
    
}
