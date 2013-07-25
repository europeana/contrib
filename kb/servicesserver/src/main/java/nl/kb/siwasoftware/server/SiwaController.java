package nl.kb.siwasoftware.server;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

/**
 * This controller
 *
 * @author Thomas Beekman (thomas.beekman@kb.nl)
 * @author Gerald de Jong <geralddejong@gmail.com>
 */

@Controller
public class SiwaController implements ServletContextAware {
    private static final int SESSION_MAX_AGE = 2 * 60 * 60; // Two Hours
    private ServletContext servletContext;
    private Logger log = Logger.getLogger(getClass());

    @RequestMapping
    public String legend() {
        return "redirect:/services";
    }

    @RequestMapping("/services")
    @ResponseBody
    public String services(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            @RequestParam(required = false) String src,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String callback,
            @RequestParam(required = false) String reset,
            @RequestParam(required = false) String uncookie,
            @RequestParam(required = false) String lock
    ) throws IOException {
        session.setMaxInactiveInterval(SESSION_MAX_AGE);

        // Delete Session Object or Cookie if requested
        if (reset != null) {
            session.setAttribute("json", null);
            ServicesUtil.removeCookie(response);
        }
        if (uncookie != null) {
            ServicesUtil.removeCookie(response);
        }

        // Check for an existing JSON Session object
        if ((session.getAttribute("json") != null) && (format != null) &&
                (format.equalsIgnoreCase("json"))) {

            String jsonString = session.getAttribute("json").toString();
            //response.setHeader("Content-Disposition", "inline; filename=services.json");
            //response.setContentType("application/json");

            // Process the parameter callback
            if (callback != null) {
                return callback + "(" + jsonString + ");";
            }
            else {
                return jsonString;
            }
        }
        else {
            // Set standards (JSON, services13.xml)

            if (version == null || version.isEmpty()) {
                version = "1.3";
            }
            if (format == null || format.isEmpty()) {
                format = "html";
            }

            String resDir = "/resources/";
            String servicesFile = "services13.xml";
            String servicesXml = "";

            // Process the cookie
            String cookieValue = ServicesUtil.getCookie(request);
            if (!cookieValue.isEmpty() && ServicesUtil.urlExists(cookieValue)) {
                servicesXml = ServicesReader.getServicesXmlFromUrl(servletContext, cookieValue);
                src = "";
            }

            // Retrieve the right services.xml
            if (src != null && !src.isEmpty()) {
                // Use the URL in the src parameter, first check if it exists
                if (ServicesUtil.urlExists(src)) {
                    servicesXml = ServicesReader.getServicesXmlFromUrl(servletContext, src);
                    if (lock != null) {
                        ServicesUtil.setCookie(response, src);
                    }
                }
            }

            // If the src parameter was not given OR not valid, process the version parameter
            if (servicesXml.isEmpty()) {
                // Use the version parameter
                if (version.equalsIgnoreCase("1.3"))
                    servicesFile = "services13.xml";
                else if (version.equalsIgnoreCase("kb"))
                    servicesFile = "services_kb.xml";
                else if (version.equalsIgnoreCase("tel"))
                    servicesFile = "services_tel.xml";

                String servicesUrl = resDir + servicesFile;
                servicesXml = ServicesReader.getServicesXmlFromResources(servletContext, servicesUrl);
            }

            // Define the requested format
            if (format.equalsIgnoreCase("json")) {
                //response.setHeader("Content-Disposition", "inline; filename=services.json");
                //response.setContentType("application/json");
                if ((callback == null) || (callback.isEmpty())) {
                    return ServicesReader.getServicesJSON(servicesXml);
                }
                else {
                    return callback + "(" + ServicesReader.getServicesJSON(servicesXml) + ");";
                }
            }
            else if (format.equalsIgnoreCase("xml")) {
                //response.setContentType("text/xml");
                return servicesXml;
            }
            else if (format.equalsIgnoreCase("debug")) {
                String output = "";
                output += "Creation Time: " + session.getCreationTime() + "<br>";
                output += "Last Accessed Time: " + session.getLastAccessedTime() + "<br>";
                output += "Max Inactive Time: " + session.getMaxInactiveInterval() + "<br>";
                output += "ID: " + session.getId() + "<br>";

                Enumeration enummer = session.getAttributeNames();
                while (enummer.hasMoreElements()) {
                    String name = (String) enummer.nextElement();
                    String value = session.getAttribute(name).toString();
                    output += name + " = " + value + "<br>";
                }
                output += "Registry URL: " + session.getServletContext().getInitParameter("registry-url") + "<br>";

                output += "<br>" + ServicesReader.getServicesJSON(servicesXml) + "<br>";

                return output;
            }
            else {
                Map params = request.getParameterMap();
                String pars = ServicesUtil.buildParsString(params);

                String file = request.getRequestURI();
                file += '?' + pars;

                URL reconstructedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), file);
                return ServicesReader.getServicesHtml(reconstructedURL.toString());
            }
        }
    }

    @RequestMapping(value = "/services.xml", method = RequestMethod.POST)
    @ResponseBody
    public String serv2xmlPost(
            @RequestParam("jsonstring") String jsonString
    ) throws IOException {
        // Convert the JSON Object to XML
        JSONObject json = JSONObject.fromObject(jsonString);
        XMLSerializer xmls = new XMLSerializer();

        xmls.setTypeHintsEnabled(false);
        xmls.setTypeHintsCompatibility(false);

        xmls.setRootName("services");
        xmls.addNamespace("dc", "http://purl.org/dc/elements/1.1/");
        xmls.addNamespace("tel", "http://krait.kb.nl/");
        xmls.addNamespace("dcterms", "http://purl.org/dc/terms/");
        xmls.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmls.addNamespace("sd", "http://www.theeuropeanlibrary.org/servicedescription");

        return xmls.write(json, "UTF-8");
    }

    @RequestMapping(value = "/regsearch", method = RequestMethod.GET)
    @ResponseBody
    public String regSearch(
            @RequestParam(required = false) String callback
    ) throws IOException {
        String servicesXml = ServicesReader.getServicesXmlFromUrl(servletContext, servletContext.getInitParameter("registry-url"));

        servicesXml = servicesXml.replaceAll("type", "SIWAtypeSIWA");

        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setSkipNamespaces(true);
        JSON json = xmlSerializer.read(servicesXml);

        // Get the result as a JSON object
        String out = json.toString(2);

        // Fast fix: change "SIWAtypeSIWA" back to "type" (the field "type" is neglected by net.sf.json) // todo: fix for real
        out = out.replaceAll("SIWAtypeSIWA", "type");
        out = out.replaceAll("\"@xsi:type\": \"dcterms:URI\",\n", "");

        // Set callback if requested
        if (callback != null) {
            out = callback + "(" + out + ");";
        }

        return out;
    }

    @RequestMapping(value = "/jsonloader", method = RequestMethod.GET)
    @ResponseBody
    public String jsonLoaderGet() throws IOException {
        return "Error: No services object given!";
    }

    @RequestMapping(value = "/jsonloader", method = RequestMethod.POST)
    @ResponseBody
    public String jsonLoaderPost(
            HttpSession session, HttpServletResponse response,
            @RequestParam("jsonstring") String jsonString
    ) throws IOException {
        String out;

        if (!jsonString.isEmpty()) {
            // Store the JSON in the Session
            session.setAttribute("json", jsonString);

            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", "services");

            out = "";
            //return out;
        }
        else {
            // We will only get here in case the previous action did not satisfy our need for a services object

            out = "Error: No services object given!";
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Refresh", "5; url=services");
        }
        return out;
    }

    @RequestMapping(value = "/jsonloadermp", method = RequestMethod.POST)
    public void jsonLoaderMP(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ) throws IOException, FileUploadException {
        if (ServletFileUpload.isMultipartContent(request)) {
            // Create a new file upload handler
            ServletFileUpload uploadHandler = new ServletFileUpload();

            FileItemIterator fiIter = uploadHandler.getItemIterator(request);
            boolean found = false;
            StringBuilder sb = new StringBuilder();
            String result = "";

            while (fiIter.hasNext() && !found) {
                FileItemStream fis = fiIter.next();

                if (!fis.isFormField()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis.openStream()));
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    br.close();
                    result = sb.toString();
                    found = true;
                }
            }

            if ((found) && (!result.isEmpty())) {
                // TODO: Add JSON integrity check
                session.setAttribute("json", result);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.setHeader("Location", "services");
                response.setContentType("text/html");
            }
        }
    }

    @RequestMapping(value = "/xmlloadermp", method = RequestMethod.POST)
    public void xmlLoaderMP(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ) throws IOException, FileUploadException {
        if (ServletFileUpload.isMultipartContent(request)) {
            // Create a new file upload handler
            ServletFileUpload uploadHandler = new ServletFileUpload();

            FileItemIterator fiIter = uploadHandler.getItemIterator(request);
            boolean found = false;
            StringBuilder sb = new StringBuilder();
            String result = "";

            while (fiIter.hasNext() && !found) {
                FileItemStream fis = fiIter.next();

                if (!fis.isFormField()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis.openStream()));
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    br.close();
                    result = sb.toString();
                    found = true;
                }
            }

            if (found && !result.isEmpty()) {
                session.setAttribute("json", ServicesReader.getServicesJSON(result));
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.setHeader("Location", "services");
                response.setContentType("text/html");
            }
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    // ===== Exceptions ===== 

    @ExceptionHandler({IOException.class, FileUploadException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void fileUploadException(Exception e) {
        log.warn("problem", e);
    }

}
