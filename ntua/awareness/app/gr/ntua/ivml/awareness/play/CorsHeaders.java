package gr.ntua.ivml.awareness.play;

import play.mvc.*;

/* author Anna*
 * Adds CORS headers to play HeaderNames
 */
public class CorsHeaders implements Http.HeaderNames {

   public static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

   public static String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

   public static String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

   public static  String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

   public static  String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

   public static  String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

   public static String ORIGIN = "Origin";   

}