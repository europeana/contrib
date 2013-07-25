
package gr.ntua.ivml.awareness.util;
import play.mvc.*;
import play.mvc.Http.HeaderNames;

//import jetty.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


final class PlaySupport {
    public static void fixContentType(Http.Request req) {
        String query = req.queryString().toString();
        if (query != null) {
            query = query.toLowerCase();
            int pos = query.indexOf("content-type");
            if (pos != -1) {
                pos = query.indexOf('=', pos);
                int end = query.indexOf('&', pos);
                if (end == -1) {
                    end = query.length();
                }
                String dec;
                try {
                    dec = URLDecoder.decode(query.substring(pos + 1, end), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // should never occur
                    throw new RuntimeException(e.getMessage(), e);
                }
                pos = dec.indexOf(";"); // charset ?
                if (pos != -1) {
                    dec = dec.substring(0, pos).trim();
                }
                String[] vals=new String[1];
                vals[0]=dec;
               // req.headers().put(HeaderNames.CONTENT_TYPE, vals);
                
            }
        }
    }
}
