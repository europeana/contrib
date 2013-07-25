package gr.ntua.ivml.awareness.security;

import java.util.ArrayList;
import java.util.List;



public class SecurityRole implements InheritableRole
{
	
	/*available inheritedRoles: admin,moderator,editor,contributor*/
	public static String ADMIN="admin";
	public static String CONTRIBUTOR="contributor";
	public static String EDITOR="editor";
	public static String MODERATOR="moderator";
	
	
	
    private String name;
    public List<SecurityRole> inheritedRoles;

    
    public SecurityRole(String name)
    {   
        this.name = name;
    }
    public String getRoleName()
    {
        return name;
    }
    
	@Override
	public List<? extends InheritableRole> getInheritedRoles() {
		
		if (this.name.equalsIgnoreCase("moderator")){
	    	inheritedRoles=new ArrayList<SecurityRole>();
	    	inheritedRoles.add(new SecurityRole("moderator"));
			inheritedRoles.add(new SecurityRole("contributor"));
	    } 
		else if (this.name.equalsIgnoreCase("admin")){
			inheritedRoles=new ArrayList<SecurityRole>();
			inheritedRoles.add(new SecurityRole("admin"));
			inheritedRoles.add(new SecurityRole("editor"));
			inheritedRoles.add(new SecurityRole("contributor"));
			inheritedRoles.add(new SecurityRole("moderator"));
	    }
	    else if (this.name.equalsIgnoreCase("editor")){
	    	inheritedRoles=new ArrayList<SecurityRole>();
	    	inheritedRoles.add(new SecurityRole("editor"));
			inheritedRoles.add(new SecurityRole("contributor"));
			inheritedRoles.add(new SecurityRole("moderator"));
	    }
	    else if(this.name.equalsIgnoreCase("contributor")){
	    	inheritedRoles=new ArrayList<SecurityRole>();
	    	inheritedRoles.add(new SecurityRole("contributor"));
	    }
		return inheritedRoles;
	}
}

