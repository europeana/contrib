package gr.ntua.ivml.awareness.util;
import play.mvc.*;
import play.mvc.Http.HeaderNames;



import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public final class IECorsFilter  {

    private static final Logger LOGGER = Logger.getLogger(IECorsFilter.class.getName());
    private static final String[] DAYS = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan"};
    private static final TimeZone __GMT = TimeZone.getTimeZone("GMT");
    private static final ThreadLocal<DateGenerator> __dateGenerator = new ThreadLocal<DateGenerator>() {
        @Override
        protected DateGenerator initialValue() {
            return new DateGenerator();
        }
    };
    private static final String __01Jan1970_COOKIE = __dateGenerator.get().formatDate(0);

   
    

    
    public void doFilter(Http.Context ctx,Http.Request servletRequest, Http.Response servletResponse) throws Exception {
        final Http.Request req = servletRequest;
        final Http.Response res = servletResponse;
        String ua;
        String query = req.queryString().toString();
        query = query == null ? "" : query.toLowerCase();
        if (query.contains("_xdr=") && (ua = req.getHeader("User-Agent")) != null && ua.contains("MSIE")) {
            // change content-type for a POST request to allow request parsing if some data is given
            if ("POST".equals(req.method().toUpperCase())) {
                
                PlaySupport.fixContentType(req);
                
                if (req.getHeader(HeaderNames.CONTENT_TYPE) == null && LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.severe("[XDR] No Content-Type received for IE CORS POST request " + req.uri());
                }
            }
            // intercepts calls which set cookies
            final List<String> headers = new LinkedList<String>();
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            final AtomicInteger status = new AtomicInteger(200);
            final AtomicBoolean closed = new AtomicBoolean(false);
            final long id = getId(req);

             if(req.getHeader(HeaderNames.ACCEPT_ENCODING)==null){
            	 req.headers().put(HeaderNames.ACCEPT_ENCODING, "gzip, deflate".split(","));
             }
            
            

            writeStream(ctx,req, res, headers, status, output.toByteArray(), id, false);

        } 
    }

    private static long getId(Http.Request req) {
        try {
        	
            return Long.parseLong(req.queryString().get("_xdr")[0]);
        } catch (RuntimeException e) {
            return -1;
        }
    }

    private void writeStream(Http.Context ctx,Http.Request req, Http.Response res, List<String> headers, AtomicInteger status, byte[] data, long id, boolean close) throws IOException {
        if (LOGGER.isLoggable(Level.FINE)) {
          //  LOGGER.fine("[XDR-" + id + "][" + req.uri() + "] Writing stream (length=" + data.length + ", close=" + close + ", suspended=" + req. + "):\n=> " + new String(data));
        }

        
        // build header line
        StringBuilder header = new StringBuilder();
        for (String cookie : headers) {
            header.append(header.length() == 0 ? "" : ",").append(cookie);
        }

        // add session id
        String sessionHeader = getSessionHeader(ctx, res);
        if (sessionHeader != null) {
            header.insert(0, ",").insert(0, sessionHeader);
        }
       
        // prepare header
        final int len = header.length();
        final boolean needsHeader = len > 0 || status.get() != 200;

        if (needsHeader) {
           
           // header.append("~").append(status.get()).append("~").append(len).append("~");

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("[XDR-" + id + "][" + req.uri() + "] Appending header: " + header);
            }

            if ("gzip".equals(res.getHeaders().get("Content-Encoding"))) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("[XDR-" + id + "][" + req.uri() + "] Uncompressing GZIP response...");
                }
                res.setHeader("Content-Encoding", null);
                ByteArrayOutputStream uncompressed = new ByteArrayOutputStream();
                GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(data));
                int c;
                byte[] buffer = new byte[8096];
                while ((c = gzipStream.read(buffer)) != -1) {
                    uncompressed.write(buffer, 0, c);
                }
                res.setHeader(HeaderNames.CONTENT_LENGTH, header.length() + uncompressed.size()+"");
                //uncompressed.writeTo(res.);
            } else {
            	res.setHeader(HeaderNames.CONTENT_LENGTH, header.length() + data.length+"");
                //res.setContentLength(header.length() + data.length);
                //res.getOutputStream().write(data);
            }
            

        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("[XDR-" + id + "][" + req.uri() + "] No header to append, status=200, length=" + data.length);
            }
            res.setHeader(HeaderNames.CONTENT_LENGTH,data.length+"");
            
        }
    }

    private static final class DateGenerator {
        private final StringBuilder buf = new StringBuilder(32);
        private final GregorianCalendar gc = new GregorianCalendar(__GMT);

        /**
         * Format HTTP date "EEE, dd MMM yyyy HH:mm:ss 'GMT'"
         */
        public String formatDate(long date) {
            buf.setLength(0);
            gc.setTimeInMillis(date);

            int day_of_week = gc.get(Calendar.DAY_OF_WEEK);
            int day_of_month = gc.get(Calendar.DAY_OF_MONTH);
            int month = gc.get(Calendar.MONTH);
            int year = gc.get(Calendar.YEAR);
            int century = year / 100;
            year = year % 100;

            int hours = gc.get(Calendar.HOUR_OF_DAY);
            int minutes = gc.get(Calendar.MINUTE);
            int seconds = gc.get(Calendar.SECOND);

            buf.append(DAYS[day_of_week]);
            buf.append(',');
            buf.append(' ');
            append2digits(buf, day_of_month);

            buf.append(' ');
            buf.append(MONTHS[month]);
            buf.append(' ');
            append2digits(buf, century);
            append2digits(buf, year);

            buf.append(' ');
            append2digits(buf, hours);
            buf.append(':');
            append2digits(buf, minutes);
            buf.append(':');
            append2digits(buf, seconds);
            buf.append(" GMT");
            return buf.toString();
        }

        /* ------------------------------------------------------------ */

        /**
         * Format "EEE, dd-MMM-yy HH:mm:ss 'GMT'" for cookies
         */
        private void formatCookieDate(StringBuilder buf, long date) {
            gc.setTimeInMillis(date);

            int day_of_week = gc.get(Calendar.DAY_OF_WEEK);
            int day_of_month = gc.get(Calendar.DAY_OF_MONTH);
            int month = gc.get(Calendar.MONTH);
            int year = gc.get(Calendar.YEAR);
            year = year % 10000;

            int epoch = (int) ((date / 1000) % (60 * 60 * 24));
            int seconds = epoch % 60;
            epoch = epoch / 60;
            int minutes = epoch % 60;
            int hours = epoch / 60;

            buf.append(DAYS[day_of_week]);
            buf.append(',');
            buf.append(' ');
            append2digits(buf, day_of_month);

            buf.append('-');
            buf.append(MONTHS[month]);
            buf.append('-');
            append2digits(buf, year / 100);
            append2digits(buf, year % 100);

            buf.append(' ');
            append2digits(buf, hours);
            buf.append(':');
            append2digits(buf, minutes);
            buf.append(':');
            append2digits(buf, seconds);
            buf.append(" GMT");
        }

    }

    private static void append2digits(StringBuilder buf, int i) {
        if (i < 100) {
            buf.append((char) (i / 10 + '0'));
            buf.append((char) (i % 10 + '0'));
        }
    }

    private static String removeHttpOnly(String header) {
        int pos = header.indexOf("HttpOnly");
        if (pos != -1) {
            int start = pos - 1;
            while (header.charAt(start) != ';') {
                start--;
            }
            return header.substring(0, start) + header.substring(pos + 8);
        }
        return header;
    }

    private static String buildHeader(Http.Cookie cookie) {
        StringBuilder header = new StringBuilder(cookie.name()).append("=").append(cookie.value());
        if (cookie.path() != null && cookie.path().length() > 0) {
            header.append(";Path=").append(cookie.path());
        }
        if (cookie.maxAge() >= 0) {
            header.append(";Expires=");
            if (cookie.maxAge() == 0) {
                header.append(__01Jan1970_COOKIE);
            } else {
                __dateGenerator.get().formatCookieDate(header, System.currentTimeMillis() + 1000L * cookie.maxAge());
            }
        }
        return header.toString();
    }

    private static String getSessionHeader(Http.Context ctx,Http.Response res) {
    	Http.Session session=ctx.session();
        //HttpSession session = req..getSession(false);
    	if(session!=null){
    		//Http.Cookie cookie=new Http.Cookie("PLAY_SESSION", session.toString(), -1, "/", ctx.request().getHeader(HeaderNames.HOST), false, false);
    		//add new cookie that is not httponly
    		session.remove("HTTPOnly");
    		session.remove("HttpOnly");
    		//session.remove("Http_Only");
    		/*String cookie_value=session.toString().substring(1,session.toString().length()-1);
    		try {
				cookie_value=URLEncoder.encode(cookie_value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			session.clear();
    		res.setCookie("PLAY_SESSION", cookie_value, -1, "/", "", false, false);*/
    		//(java.lang.String name, java.lang.String value, int maxAge, java.lang.String path, java.lang.String domain, boolean secure, boolean httpOnly)
            
    //	String hSetCookie="PLAY_SESSION";
     //   hSetCookie+=session.toString();
    	//String header = null;
        /*if(hSetCookie!=null){
        	header=removeHttpOnly(hSetCookie);
        }*/
       
        //if(header!=null){res.setHeader(HeaderNames.SET_COOKIE, header);}
        //int start, end;
        //if (session != null && hSetCookie != null && (start = hSetCookie.indexOf("=" + session.get("uuid"))) != -1) {
           /* end = start-- + 1;
            for (; start > 0; start--) {
                if (hSetCookie.charAt(start) == ' ' || hSetCookie.charAt(start) == ',') {
                    start++;
                    break;
                }
            }
            boolean expire = false;
            for (int max = hSetCookie.length(); end < max; end++) {
                if (hSetCookie.charAt(end) == ',') {
                    if (!expire && hSetCookie.substring(start, end).contains("Expires=")) {
                        expire = true;
                    } else if (expire || !hSetCookie.substring(start, end).contains("Expires=")) {
                        break;
                    }
                }
            }*/
            //return removeHttpOnly(hSetCookie);
    		return null;
        }
        return null;
    }

   
}
