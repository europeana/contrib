package pt.utl.ist.repox.z3950;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Target
 *
 * @author Nuno Freire
 */

public class Target {
	private static final Logger log = Logger.getLogger(Target.class);

    protected String address;
    protected int port;
    protected String database;
    protected String user;
    protected String password;
    protected String charset;
    protected String recordSyntax; // "unimarc" or "usmarc"

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address =  address;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port =  port;
    }

    public String getDatabase(){
        return database;
    }

    public void setDatabase(String database){
        this.database =  database;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user =  user;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password =  password;
    }

    public String getCharset(){
        return charset;
    }

    public void setCharset(String charset){
        this.charset =  charset;
    }

    public String getRecordSyntax(){
        return recordSyntax;
    }

    public void setRecordSyntax(String recordSyntax){
        this.recordSyntax =  recordSyntax;
    }
    
    public Target(){
    }

    public Target(String address, int port, String database, String user, String password, String charset, String recordSyntax){
        this.address=address;
        this.port=port;
        this.database=database;
        this.user=user;
        this.password=password;
        this.charset=charset;
        this.recordSyntax = recordSyntax;
    }

    public Properties connectionProperties(){
        Properties p=new Properties();
        p.put("host",address);
        p.put("port",String.valueOf(port));
        p.put("serviceName","Long Name");
        p.put("defaultRecordSyntax",recordSyntax);
        p.put("defaultElementSetName","B");   
        p.setProperty("serviceUserPrincipal",user);
        p.setProperty("serviceUserGroup","");
        p.setProperty("serviceUserCredentials",password);                
//        p.setProperty("charsetEncoding","ANSEL"); 
        p.setProperty("charsetEncoding","ISO8859-1"); 
//        p.setProperty("charsetEncoding","ISO8859-2"); 
        p.setProperty("prefMessageSize","1048576"); 
        p.setProperty("exceptionalMessageSize","1048576"); 
//      private int pref_message_size; 
//      private int exceptional_message_size;    
//        private String default_element_set_name; 
//      private int pref_message_size; 
//      private int exceptional_message_size; 
//        private boolean use_reference_id; 
//        private String service_name;
//        private String service_id;
//        private int auth_type=0;
        
        return p;
    }

    /**
      * Forms the string containing values of member variables in the Bean.
      *
      * @return String containing member variable values.
      */
    public String toString(){
        StringBuffer b=new StringBuffer("******* Target - Begin *******")
        .append("\naddress = "+address)
        .append("\nport = "+port)
        .append("\ndatabase = "+database)
        .append("\nuser = "+user)
        .append("\npassword = "+password)
        .append("\ncharset = "+charset)
        .append("\nrecordSyntax = "+recordSyntax)
        .append("\n******* Target - End *******\n"); 
        return b.toString();
    }
    
}