/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SAP Training
 */

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EBSOrganizationCustomerCreation {
    private static Logger LOG;
    static int user_id ;
    static int resp_id;
    static int resp_appl_id;
    int trx_header_id;
    StringWriter errors = new StringWriter();

    public EBSOrganizationCustomerCreation() {
        
    }
    
    public EBSOrganizationCustomerCreation(int user_id,int resp_id, int resp_appl_id, int trx_header_id) {
        this.resp_id = resp_id;
        this.user_id = user_id;
        this.resp_appl_id = resp_appl_id;
        this.trx_header_id = trx_header_id;
        
    }
    
    public String createCustomerSQLHeader(String customerName, String customerNumber,String customerClassification){
        MyLogging.log(Level.INFO, "in createCustomerSQLHeader...");
        String sqlscriptHeader = "DECLARE\n" +
               " p_cust_account_rec     HZ_CUST_ACCOUNT_V2PUB.CUST_ACCOUNT_REC_TYPE;\n" +
               " p_organization_rec     HZ_PARTY_V2PUB.ORGANIZATION_REC_TYPE;\n" +
               " p_customer_profile_rec HZ_CUSTOMER_PROFILE_V2PUB.CUSTOMER_PROFILE_REC_TYPE;\n" +
               " x_cust_account_id      NUMBER;\n" +
               " x_account_number       VARCHAR2(2000);\n" +
               " x_party_id             NUMBER;\n" +
               " x_party_number         VARCHAR2(2000);\n" +
               " x_profile_id           NUMBER;\n" +
               " x_return_status        VARCHAR2(2000);\n" +
               " x_msg_count            NUMBER;\n" +
               " x_msg_data             VARCHAR2(2000);\n" +
               "\n" +
               "BEGIN\n" +
               "fnd_global.apps_initialize("+user_id+", "+resp_id+","+resp_appl_id+",0);        \n" +
               "mo_global.init ('AR');   \n" +
               " p_cust_account_rec.account_name      := '"+customerName+"';\n" +
               " p_cust_account_rec.customer_type  := 'R';\n"+
               "p_cust_account_rec.customer_class_code  := '"+customerClassification+"';\n"+
               //" p_cust_account_rec.account_number      := '"+customerNumber+"';\n" +
               " p_cust_account_rec.created_by_module := 'BO_API';\n" +
               " p_organization_rec.organization_name := '"+customerName+"';\n" +
               " p_organization_rec.created_by_module := 'BO_API';";
        
        return sqlscriptHeader;
    }
    
     public String createCustomerSQLBody(){
         MyLogging.log(Level.INFO, "in createCustomerSQLBody...");
         String sqlScriptBody = "HZ_CUST_ACCOUNT_V2PUB.CREATE_CUST_ACCOUNT\n" +
"             (\n" +
"              p_init_msg_list       => FND_API.G_TRUE,\n" +
"              p_cust_account_rec    =>p_cust_account_rec,\n" +
"              p_organization_rec    =>p_organization_rec,\n" +
"              p_customer_profile_rec=>p_customer_profile_rec,\n" +
"              p_create_profile_amt  =>FND_API.G_FALSE,\n" +
"              x_cust_account_id     =>x_cust_account_id,\n" +
"              x_account_number      =>x_account_number,\n" +
"              x_party_id            =>x_party_id,\n" +
"              x_party_number        =>x_party_number,\n" +
"              x_profile_id          =>x_profile_id,\n" +
"              x_return_status       =>x_return_status,\n" +
"              x_msg_count           =>x_msg_count,\n" +
"              x_msg_data            =>x_msg_data\n" +
"              );\n"+
                "?:=x_cust_account_id;\n" +
                 "?:=x_account_number;\n" +
                 "?:=x_party_id;\n" +
                 "?:=x_party_number;\n" +
                 "?:=x_profile_id;\n" +
                 "?:=x_return_status;\n" +
                 "END;";
      
         return sqlScriptBody;
     }
    
     public static void main(String[] args) {        
        EBSOrganizationCustomerCreation ecc = new EBSOrganizationCustomerCreation(1132,50678,222,101);
        String hdr = ecc.createCustomerSQLHeader("EKEJ", "1234","VEHICLE NONDEALERS");
        String bdy = ecc.createCustomerSQLBody();
        MyLogging.log(Level.INFO, hdr+bdy);
    }
}
