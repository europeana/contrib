
package gr.ntua.ivml.awareness.security;

import be.objectify.deadbolt.models.RoleHolder;

import java.util.List;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public interface InheritableRoleHolder extends RoleHolder
{
    public List<? extends InheritableRole> getInheritableRoles();
}
