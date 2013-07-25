<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:form action="properties.html" modelAttribute="propertiesForm">
    <table>
        <tr>
            <td><b>Repository Folder:</b></td>
            <td><form:input path="repository_dir"></form:input></td>
        </tr>
        <tr>
            <td>Folder of the REPOX Repository (XML files)</td>
        <tr>
            <td><b>Configuration Files Folder</b></td>
            <td><form:input path="xmlConfig_dir"></form:input></td>
        </tr>
        <tr>
            <td>Folder of the REPOX XML Dynamic
                <br>Configuration Files (DataProviders, Statistics)</td>
        </tr>
        <tr>
            <td><b>OAI-PMH Requests Folder</b></td>
            <td><form:input path="oairequests_dir"></form:input></td>
        </tr>
        <tr>
            <td>Folder for the temporary OAI-PMH requests</td>
        </tr>
        <tr>
            <td><b>Derby Database Folder</b></td>
            <td><form:input path="database_dir"></form:input></td>
        </tr>
        <tr>
            <td>Database: Derby</td>
        </tr>
        <tr>
            <td><b>Baseurn</b></td>
            <td><form:input path="baseurn"></form:input></td>
        </tr>
        <tr>
            <td>URN prefix of this REPOX instance
                <br>(syntax is urn:[instance identifier]:)</td>
        </tr>
        <tr>
            <td>
        </tr>
        <tr>
            <td><b>Default exportation folder</b>
                <br>(/var/www/harvest_dumps)</td>
            <td><form:input path="exportation_folder"></form:input></td>
        </tr>
        <tr>
            <td><b>Administrator Email</b></td>
            <td><form:input path="administrator_email"></form:input></td>
        </tr>
        <tr>
            <td><b>SMTP Server</b></td>
            <td><form:input path="smtp_server"></form:input></td>
        </tr>
        <tr>
            <td><b>SMTP Port</b></td>
            <td><form:input path="smtp_port"></form:input></td>
        </tr>
        <tr>
            <td><b>REPOX Default Email Sender</b></td>
            <td><form:input path="default_email"></form:input></td>
        </tr>
        <tr>
            <td><b>Administrator Email Password</b></td>
            <td><form:password path="administrator_email_pass"></form:password></td>
        </tr>
        <tr>
            <td></td>
            <td><input type="submit" value="Change properties" /></td>
        </tr>
    </table>
</form:form>
