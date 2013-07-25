
package controllers;

import play.mvc.*;
import play.mvc.Http.HeaderNames;

import gr.ntua.ivml.awareness.play.*;

public class CorsSetup extends Action.Simple {

	
	public Result call(Http.Context ctx) throws Throwable {
		
		if(ctx.request().getHeader("Origin")!=null 
			&& (Application.DOMAINS.indexOf(ctx.request().getHeader("Origin"))>-1 
				|| Application.DOMAINS.indexOf("*")==0)){
	    	ctx.response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ctx.request().getHeader("Origin"));
	    }
		ctx.response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		ctx.response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS");
		ctx.response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, X-Requested-With, Accept");
		ctx.response().setHeader(CorsHeaders.ACCESS_CONTROL_MAX_AGE,"86400");
		
		return delegate.call(ctx);
	  }
	
	
}