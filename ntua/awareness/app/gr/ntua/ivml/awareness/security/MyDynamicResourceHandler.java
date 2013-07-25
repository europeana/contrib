
package gr.ntua.ivml.awareness.security;

import be.objectify.deadbolt.DeadboltHandler;
import be.objectify.deadbolt.DynamicResourceHandler;
import be.objectify.deadbolt.models.Permission;
import be.objectify.deadbolt.models.RoleHolder;


import play.Logger;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Iterator; 
import java.util.List;
import java.util.Map;

/**
 * author Anna
 */
public class MyDynamicResourceHandler implements DynamicResourceHandler
{
    private static final Map<String, DynamicResourceHandler> HANDLERS = new HashMap<String, DynamicResourceHandler>();

    static
    {
        HANDLERS.put("minimumLevelRequired",
                     new AbstractDynamicResourceHandler()
                     {
                         public boolean isAllowed(String name,
                                                  String roleName,
                                                  DeadboltHandler deadboltHandler,
                                                  Http.Context context)
                         {
                             boolean isAllowed = false;
                             InheritableRoleHolder roleHolder = ((MyDeadboltHandler) deadboltHandler).getRoleHolder(context);

                             List<? extends InheritableRole> inheritableRoles = roleHolder.getInheritableRoles();
                             
                             SecurityRole requiredRole = new SecurityRole(roleName);
                             Map<String, InheritableRole> checkedRoles = new HashMap<String, InheritableRole>();
                             for (Iterator<? extends InheritableRole> iterator = inheritableRoles.iterator(); !isAllowed && iterator.hasNext(); )
                             {
                                 isAllowed = isRolePresent(requiredRole,
                                                           (SecurityRole)iterator.next(),
                                                           checkedRoles);
                             }
                             return isAllowed;
                         }

                         private boolean isRolePresent(SecurityRole requiredRole,
                                                       SecurityRole roleToCheck,
                                                       Map<String, InheritableRole> checkedRoles)
                         {
                             boolean isRolePresent = false;
                           
                             // avoid infinite loops for complex role trees
                             if (!checkedRoles.containsKey(roleToCheck.getRoleName()))
                             {
                                 if (roleToCheck.getRoleName().equals(requiredRole.getRoleName()))
                                 {
                                     isRolePresent = true;
                                 }
                                 else
                                 {
                                     checkedRoles.put(roleToCheck.getRoleName(),
                                                      roleToCheck);
                                     List<? extends InheritableRole> inheritedRoles = roleToCheck.getInheritedRoles();
                                     for (Iterator<? extends InheritableRole> iterator = inheritedRoles.iterator(); !isRolePresent && iterator.hasNext(); )
                                     {
                                         isRolePresent = isRolePresent(requiredRole,
                                                                       (SecurityRole)iterator.next(),
                                                                       checkedRoles);

                                     }
                                 }
                             }

                             return isRolePresent;
                         }
                     });
    }
    
    public boolean isAllowed(String name,
                             String meta,
                             DeadboltHandler deadboltHandler,
                             Http.Context context)
    {
        DynamicResourceHandler handler = HANDLERS.get(name);
        boolean result = false;
        if (handler == null)
        {
            Logger.error("No handler available for " + name);
        }
        else
        {
            result = handler.isAllowed(name,
                                       meta,
                                       deadboltHandler,
                                       context);
        }
        return result;
    }

    public boolean checkPermission(String permissionValue,
                                   DeadboltHandler deadboltHandler,
                                   Http.Context ctx)
    {
        boolean permissionOk = false;
        RoleHolder roleHolder = deadboltHandler.getRoleHolder(ctx);
        
        if (roleHolder != null)
        {
            List<? extends Permission> permissions = roleHolder.getPermissions();
            for (Iterator<? extends Permission> iterator = permissions.iterator(); !permissionOk && iterator.hasNext(); )
            {
                Permission permission = iterator.next();
                permissionOk = permission.getValue().contains(permissionValue);
            }
        }
        
        return permissionOk;
    }
}
