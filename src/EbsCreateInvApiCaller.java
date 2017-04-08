/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  oracle.jdbc.OracleDriver
 */


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
//import PLXSiebelEBSInterface;
import oracle.jdbc.OracleDriver;

public class EbsCreateInvApiCaller {
    private String accountName;
    private String organisationName;
    private String ecs;
    private String address;
    private String city;
    private int postalCode;
    private String state;
    private String cust_id;
    private String msg_data;
    private String return_status;
    public String theCusterRef;
    private static final Logger LOG = Logger.getLogger(EBSOnHandQty.class.getName());
    
    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOrganisationName() {
        return this.organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getEcs() {
        return this.ecs;
    }

    public void setEcs(String ecs) {
        this.ecs = ecs;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    /*public void createCust() throws NamingException, SQLException {
        EBSSiebelIntegration nsitf = new EBSSiebelIntegration();
        try {
            Connection conn = nsitf.connectToDatabase();
            if (conn == null) {
                System.out.println("IT IS NULL");
            }
            String Plsql = "DECLARE\n p_cust_account_rec HZ_CUST_ACCOUNT_V2PUB.CUST_ACCOUNT_REC_TYPE;\n p_organization_rec HZ_PARTY_V2PUB.ORGANIZATION_REC_TYPE;\n p_customer_profile_rec HZ_CUSTOMER_PROFILE_V2PUB.CUSTOMER_PROFILE_REC_TYPE;\n x_cust_account_id NUMBER;\n x_account_number VARCHAR2(2000);\n x_party_id NUMBER;\n x_party_number VARCHAR2(2000);\n x_profile_id NUMBER;\n x_return_status VARCHAR2(2000);\n x_msg_count NUMBER;\n x_msg_data VARCHAR2(2000);\n\nBEGIN\nfnd_global.apps_initialize(user_id => 5, resp_id => 50682, resp_appl_id => 222 );\nmo_global.init('S',82); \n p_cust_account_rec.account_name := '" + this.accountName + "';\n" + " p_cust_account_rec.created_by_module := 'BO_API';\n" + "  p_cust_account_rec.account_number := '" + this.ecs + "';" + " p_organization_rec.organization_name := '" + this.organisationName + "';\n" + " p_organization_rec.created_by_module := 'BO_API';\n" + "\n" + " hz_cust_account_v2pub.create_cust_account(\n" + " 'T',\n" + " p_cust_account_rec,\n" + " p_organization_rec,\n" + " p_customer_profile_rec,\n" + " 'F',\n" + " x_cust_account_id,\n" + " x_account_number,\n" + " x_party_id,\n" + " x_party_number,\n" + " x_profile_id,\n" + " x_return_status,\n" + " x_msg_count,\n" + " x_msg_data);\n" + " ?:=x_cust_account_id; \n" + " ?:=x_party_id;\n" + " ?:=x_party_number;\n" + " ?:=x_profile_id;\n" + " ?:=x_return_status;\n" + " ?:=x_msg_count;\n" + " ?:=x_msg_data;\n" + "END; ";
            CallableStatement cs = conn.prepareCall(Plsql);
            cs.registerOutParameter(1, 4);
            cs.registerOutParameter(2, 4);
            cs.registerOutParameter(3, 12);
            cs.registerOutParameter(4, 4);
            cs.registerOutParameter(5, 12);
            cs.registerOutParameter(6, 4);
            cs.registerOutParameter(7, 12);
            cs.execute();
            int cust_account_id = cs.getInt(1);
            int x_party_id = cs.getInt(2);
            String x_party_number = cs.getString(3);
            int x_profile_id = cs.getInt(4);
            String x_return_status = cs.getString(5);
            int x_msg_count = cs.getInt(6);
            String x_msg_data = cs.getString(7);
            this.cust_id = Integer.toString(cust_account_id);
            this.msg_data = x_msg_data;
            this.return_status = x_return_status;
            System.out.println("**************************");
            System.out.println("Output information ....");
            System.out.println("x_cust_account_id: " + cust_account_id);
            System.out.println("x_party_id: " + x_party_id);
            System.out.println("x_party_number: " + x_party_number);
            System.out.println("x_profile_id: " + x_profile_id);
            System.out.println("x_return_Status: " + x_return_status);
            System.out.println("x_msg_count: " + x_msg_count);
            System.out.println("x_msg_data: " + x_msg_data);
            System.out.println("**************************");
            cs.close();
            String Plsql2 = "DECLARE\n p_location_rec HZ_LOCATION_V2PUB.LOCATION_REC_TYPE;\n x_location_id NUMBER;\n x_return_status VARCHAR2(2000);\n x_msg_count NUMBER;\n x_msg_data VARCHAR2(2000);\nBEGIN\n p_location_rec.country := 'NG';\n p_location_rec.address1 := '" + this.address + "';\n" + " p_location_rec.city := '" + this.city + "';\n" + " p_location_rec.postal_code := '" + this.postalCode + "';\n" + " p_location_rec.state := '" + this.state + "';\n" + " p_location_rec.created_by_module := 'BO_API';\n" + " hz_location_v2pub.create_location(\n" + " 'T',\n" + " p_location_rec,\n" + " x_location_id,\n" + " x_return_status,\n" + " x_msg_count,\n" + " x_msg_data);\n" + "?:=x_location_id;" + "\n" + " dbms_output.put_line('***************************');\n" + " dbms_output.put_line('Output information ....');\n" + " dbms_output.put_line('x_location_id: '||x_location_id);\n" + " dbms_output.put_line('x_return_status: '||x_return_status);\n" + " dbms_output.put_line('x_msg_count: '||x_msg_count);\n" + " dbms_output.put_line('x_msg_data: '||x_msg_data);\n" + " dbms_output.put_line('***************************');\n" + "\n" + "END;";
            CallableStatement custLocation = conn.prepareCall(Plsql2);
            custLocation.registerOutParameter(1, 4);
            custLocation.execute();
            int location_id = custLocation.getInt(1);
            custLocation.close();
            System.out.println("***********************");
            System.out.println("Location_id " + location_id);
            System.out.println("***********************");
            String Plsql3 = "DECLARE\n p_party_site_rec HZ_PARTY_SITE_V2PUB.PARTY_SITE_REC_TYPE;\n x_party_site_id NUMBER;\n x_party_site_number VARCHAR2(2000);\n x_return_status VARCHAR2(2000);\n x_msg_count NUMBER;\n x_msg_data VARCHAR2(2000);\nBEGIN\n p_party_site_rec.party_id := " + x_party_id + ";\n" + " p_party_site_rec.location_id :=" + location_id + ";\n" + " p_party_site_rec.identifying_address_flag := 'Y';\n" + " p_party_site_rec.created_by_module := 'BO_API';\n" + " hz_party_site_v2pub.create_party_site(\n" + " 'T',\n" + " p_party_site_rec,\n" + " x_party_site_id,\n" + " x_party_site_number,\n" + " x_return_status,\n" + " x_msg_count,\n" + " x_msg_data);\n" + " ?:=x_party_site_id;\n" + "\n" + " dbms_output.put_line('***************************');\n" + " dbms_output.put_line('Output information ....');\n" + " dbms_output.put_line('x_party_site_id: '||x_party_site_id);\n" + " dbms_output.put_line('x_party_site_number: '||x_party_site_number);\n" + " dbms_output.put_line('x_return_status: '||x_return_status);\n" + " dbms_output.put_line('x_msg_count: '||x_msg_count);\n" + " dbms_output.put_line('x_msg_data: '||x_msg_data);\n" + " dbms_output.put_line('***************************');\n" + "\n" + "END;";
            CallableStatement custPartySite = conn.prepareCall(Plsql3);
            custPartySite.registerOutParameter(1, 4);
            custPartySite.execute();
            int Party_site_id = custPartySite.getInt(1);
            System.out.println("***********************");
            System.out.println("Party_id " + x_party_id);
            System.out.println("***********************");
            System.out.println("***********************");
            System.out.println("Party_Site_id " + Party_site_id);
            System.out.println("***********************");
            custPartySite.close();
            String Plsql4 = "DECLARE\n p_cust_acct_site_rec hz_cust_account_site_v2pub.cust_acct_site_rec_type;\n x_return_status VARCHAR2(2000);\n x_msg_count NUMBER;\n x_msg_data VARCHAR2(2000);\n x_cust_acct_site_id NUMBER;\nBEGIN\nfnd_global.apps_initialize(user_id => 5, resp_id => 50682, resp_appl_id => 222 );\nmo_global.init('S',82); \n p_cust_acct_site_rec.cust_account_id :=" + cust_account_id + ";\n" + " p_cust_acct_site_rec.party_site_id :=" + Party_site_id + ";\n" + " p_cust_acct_site_rec.language := '';\n" + " p_cust_acct_site_rec.created_by_module := 'BO_API';\n" + " hz_cust_account_site_v2pub.create_cust_acct_site(\n" + " 'T',\n" + " p_cust_acct_site_rec,\n" + " x_cust_acct_site_id,\n" + " x_return_status,\n" + " x_msg_count,\n" + " x_msg_data);\n" + " ?:=x_cust_acct_site_id;" + " ?:=x_msg_data;" + "\n" + " dbms_output.put_line('***************************');\n" + " dbms_output.put_line('Output information ....');\n" + " dbms_output.put_line('x_cust_acct_site_id: '||x_cust_acct_site_id);\n" + " dbms_output.put_line('x_return_status: '||x_return_status);\n" + " dbms_output.put_line('x_msg_count: '||x_msg_count);\n" + " dbms_output.put_line('x_msg_data: '||x_msg_data);\n" + " dbms_output.put_line('***************************');\n" + "\n" + "END;";
            CallableStatement custAccSite = conn.prepareCall(Plsql4);
            custAccSite.registerOutParameter(1, 4);
            custAccSite.registerOutParameter(2, 12);
            custAccSite.execute();
            int c_acct_site_id = custAccSite.getInt(1);
            String msg2 = custAccSite.getString(2);
            System.out.println("cust_acct_site_id " + c_acct_site_id);
            System.out.println("Msg: " + msg2);
            String Plsql5 = "DECLARE\n p_cust_site_use_rec HZ_CUST_ACCOUNT_SITE_V2PUB.CUST_SITE_USE_REC_TYPE;\n p_customer_profile_rec HZ_CUSTOMER_PROFILE_V2PUB.CUSTOMER_PROFILE_REC_TYPE;\n x_site_use_id NUMBER;\n x_return_status VARCHAR2(2000);\n x_msg_count NUMBER;\n x_msg_data VARCHAR2(2000);\nBEGIN\n p_cust_site_use_rec.cust_acct_site_id :=" + c_acct_site_id + ";\n" + " p_cust_site_use_rec.site_use_code := 'BILL_TO';\n" + " p_cust_site_use_rec.created_by_module := 'BO_API';\n" + " hz_cust_account_site_v2pub.create_cust_site_use(\n" + " 'T',\n" + " p_cust_site_use_rec,\n" + " p_customer_profile_rec,\n" + " '',\n" + " '',\n" + " x_site_use_id,\n" + " x_return_status,\n" + " x_msg_count,\n" + " x_msg_data);\n" + " ?:= x_msg_data;" + "\n" + " dbms_output.put_line('***************************');\n" + " dbms_output.put_line('Output information ....');\n" + " dbms_output.put_line('x_site_use_id: '||x_site_use_id);\n" + " dbms_output.put_line('x_return_status: '||x_return_status);\n" + " dbms_output.put_line('x_msg_count: '||x_msg_count);\n" + " dbms_output.put_line('x_msg_data: '||x_msg_count);\n" + " dbms_output.put_line('***************************');\n" + "\n" + "END;\n";
            CallableStatement custSiteUseBillTo = conn.prepareCall(Plsql5);
            custSiteUseBillTo.registerOutParameter(1, 12);
            custSiteUseBillTo.execute();
            String msg3 = custSiteUseBillTo.getString(1);
            System.out.println("Final Output************");
            System.out.println("Msg " + msg3);
            System.out.println("Customer Created Succesfully");
            System.out.println("************************");
            custSiteUseBillTo.close();
            if (conn != null) {
                conn.close();
            }
        }
        catch (Exception ex) {
            try {
                throw ex;
            }
            catch (Exception ex1) {
                Logger.getLogger(EbsCreateInvApiCaller.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }*/

    public void createInvoice(Integer cust_id, double amount, String trxId) throws NamingException, SQLException, FileNotFoundException, IOException, Throwable {
        DriverManager.registerDriver((Driver)new OracleDriver());        
        EBSSiebelIntegration es = new EBSSiebelIntegration();
        String prop_file = es.getThePropFilePath();
        FileInputStream input = null;
        //tring conn_string = "";
        Properties propFile = new Properties();
        input = new FileInputStream(prop_file);
        propFile.load(input);
        String vConnection = propFile.getProperty("database");
        String vUsername = propFile.getProperty("dbuser");
        String vPassword = propFile.getProperty("dbpassword");
        //conn_string = propFile.getProperty("dbconn");
        //System.out.println("conn:" + conn_string);
        try {
            Connection conn = DriverManager.getConnection(vConnection, vUsername, vPassword);
            Throwable throwable = null;
            try {
                String csql = "DECLARE\n   v_customer_id             NUMBER;\n   v_costshare_value         NUMBER;\n   v_date                    DATE;\n   v_customer_reference      VARCHAR2(30);\nBEGIN\nAPPS.xxcreate_invoice_ar(\nv_customer_id =>" + cust_id + ",\n" + "v_costshare_value =>" + amount + ",\n" + "v_date =>sysdate,\n" + "v_customer_reference => '" + trxId + "');\n" + "\n" + "END;\n";
                CallableStatement cs = conn.prepareCall(csql);
                cs.executeQuery();
                cs.close();
                String sqlcheck = "SELECT  unique interface_header_attribute1,bill_to_customer_id\n        FROM ra_customer_trx_all a,ra_customer_trx_lines_all b\n       WHERE a.customer_trx_id = b.customer_trx_id\n       and a.bill_to_customer_id =" + cust_id + "\n" + "       and a.interface_header_attribute1 ='" + trxId + "'";
                PreparedStatement ps = conn.prepareStatement(sqlcheck);
                Throwable throwable2 = null;
                try {
                    ResultSet rs = ps.executeQuery(sqlcheck);
                    Throwable throwable3 = null;
                    try {
                        if (rs.next()) {
                            String custref = rs.getString("interface_header_attribute1");
                            int customerId = rs.getInt("bill_to_customer_id");
                            rs.close();
                            this.theCusterRef = custref;
                            System.out.println("The following invoice for customer: " + customerId + ", With Ref: " + custref + ", has been created successfully");
                        }
                    }
                    catch (Throwable x2) {
                        throwable3 = x2;
                        throw x2;
                    }
                    finally {
                        if (rs != null) {
                            if (throwable3 != null) {
                                try {
                                    rs.close();
                                }
                                catch (Throwable x2) {
                                    throwable3.addSuppressed(x2);
                                }
                            } else {
                                rs.close();
                            }
                        }
                    }
                }
                catch (Throwable x2) {
                    throwable2 = x2;
                    throw x2;
                }
                finally {
                    if (ps != null) {
                        if (throwable2 != null) {
                            try {
                                ps.close();
                            }
                            catch (Throwable x2) {
                                throwable2.addSuppressed(x2);
                            }
                        } else {
                            ps.close();
                        }
                    }
                }
            }
            catch (Throwable x2) {
                throwable = x2;
                throw x2;
            }
            finally {
                if (conn != null) {
                    if (throwable != null) {
                        try {
                            conn.close();
                        }
                        catch (Throwable x2) {
                            throwable.addSuppressed(x2);
                        }
                    } else {
                        conn.close();
                    }
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Sql or connection error");
            e.printStackTrace();
        }
    }

    public String getCustRef() {
        if (this.theCusterRef != null) {
            return this.theCusterRef;
        }
        return "";
    }

    public EbsCreateInvApiCaller(String accountName, String organisationName, String ecs, String address, String city, int postalCode, String state) {
        this.accountName = accountName;
        this.organisationName = organisationName;
        this.ecs = ecs;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.state = state;
    }

    public String getReturnStatus() {
        return this.return_status;
    }

    public String getMsgStatus() {
        if (this.msg_data != null) {
            return this.msg_data;
        }
        return "";
    }

    public String getCustId() {
        return this.cust_id;
    }

    public EbsCreateInvApiCaller() {
    }
    
    
    
    public static void main(String[] args) throws NamingException {
        EbsCreateInvApiCaller test = new EbsCreateInvApiCaller();
        try {
            LOG.log(Level.INFO,"Starting create invoice");
            test.createInvoice(7373,90000.00,"13002");
            LOG.log(Level.INFO,"Ending create invoice");
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}

