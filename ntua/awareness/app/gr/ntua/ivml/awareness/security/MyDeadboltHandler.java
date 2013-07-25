package gr.ntua.ivml.awareness.security;

import org.bson.types.ObjectId;

import be.objectify.deadbolt.AbstractDeadboltHandler;
import be.objectify.deadbolt.DynamicResourceHandler;

import gr.ntua.ivml.awareness.persistent.User;
import gr.ntua.ivml.awareness.util.MongoDB;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;



/**
* @author anna
*/
public class MyDeadboltHandler extends AbstractDeadboltHandler
{
    public Result beforeAuthCheck(Http.Context context)
    {
    	String uid = context.session().get("uid");
		if(uid==null){
			context.response().setContentType("application/json");
			
			return unauthorized("Oops, you are not connected");
		}
        // returning null means that everything is OK. Return a real result if you want a redirect to a login page or
        // somewhere else
        return null;
    }

   
 
    public DynamicResourceHandler getDynamicResourceHandler(Http.Context context)
    {
        return new MyDynamicResourceHandler();
    }

    @Override
    public Result onAccessFailure(Http.Context context,
                                  String content)
    {
    	context.response().setContentType("application/json");
		
    	return unauthorized("unauthorized access");
        
    }

	@Override
	public Result beforeRoleCheck(Context arg0) {
		String uid = arg0.session().get("uid");
		if(uid==null){
			arg0.response().setContentType("application/json");
			
			return unauthorized("Oops, you are not connected");
		}
        // returning null means that everything is OK. Return a real result if you want a redirect to a login page or
        // somewhere else
        return null;
	}

	@Override
	public InheritableRoleHolder getRoleHolder(Context arg0) {
		String uid = arg0.session().get("uid");
		User u=MongoDB.getUserDAO().get(new ObjectId(uid));
		return u;
	}
}