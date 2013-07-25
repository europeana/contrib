package nl.kb.siwasoftware.server;

/**
 * Created by IntelliJ IDEA.
 * Developer: Thomas Beekman (thomas.beekman@kb.nl)
 * Date: 8-feb-2010
 * Time: 16:01:24 
 */

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Class: Services
 * Description: The main servlet of the webapp. This servlet handles the parameters which can be used to display the
 * desired output format of the services.xml of the user's choice. It checks whether the user has defined his own
 * set of services. It also checks if a session object is available for faster loading the user's services.
 */
public class ServicesUtil {
    private static final String COOKIE_NAME = "siwasrc";
    private static final int COOKIE_MAX_AGE = 2 * 365 * 24 * 60 * 60; // 2 years

    /**
     * Checks whether a URL exists or not.
     *
     * @param URLName the URL which needs to be checked for availability.
     * @return true if the URL exists and false if it doesn't.
     */
    public static boolean urlExists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Places a cookie on the clients computer with the link to services.xml which should be used every time the
     * servicesserver is invoked.
     *
     * @param res the HttpServletResponse object.
     * @param url the URL to the services.xml of the clients' choice.
     */
    public static void setCookie(HttpServletResponse res, String url) {
        try {
            url = URLEncoder.encode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            System.out.println("Encoding Error: " + uee.getMessage());
        }

        Cookie ck = new Cookie(COOKIE_NAME, url);
        ck.setMaxAge(COOKIE_MAX_AGE);
        res.addCookie(ck);
    }

    /**
     * Gets the siwasrc cookie.
     *
     * @param req the HttpServletRequest object.
     * @return the value of the cookie if present, else an empty String.
     */
    public static String getCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null)
            for (Cookie ck : cookies) {
                String currCookieName = ck.getName();
                String currCookieValue = ck.getValue();

                if (currCookieName.equals(COOKIE_NAME)) {
                    try {
                        currCookieValue = URLDecoder.decode(currCookieValue, "UTF-8");
                    }
                    catch (UnsupportedEncodingException uee) {
                        System.out.println("Decoding Error: " + uee.getMessage());
                        // todo: should this problem really be ignored?
                    }

                    return currCookieValue;
                }
            }
        return "";
    }

    /**
     * Removes the cookie from the client computer.
     *
     * @param res the HttpServletResponse object.
     */
    public static void removeCookie(HttpServletResponse res) {
        Cookie ck = new Cookie(COOKIE_NAME, "");
        ck.setMaxAge(0);
        res.addCookie(ck);
    }

    /**
     * Rebuilds the parameter String of the URL, rewriting it for the Javascript to be able to load the JSON String of
     * the services object.
     *
     * @param params the request.getParameterMap() object.
     * @return the requested parameters rewritten for loading the JSON object from the Javascript.
     */
    public static String buildParsString(Map params) {
        StringBuilder pars = new StringBuilder();
        for (Object keyObject : params.keySet()) {
            String key = (String) keyObject;
            String value = ((String[]) params.get(key))[0];
            if ((!key.equalsIgnoreCase("format")) && (!key.equalsIgnoreCase("callback"))) {
                pars.append(key);
                if (value != null && !value.isEmpty()) {
                    pars.append("=").append(value);
                }
                pars.append("&");
            }
        }
        pars.append("format=json&callback=setServices");
        return pars.toString();
    }
}