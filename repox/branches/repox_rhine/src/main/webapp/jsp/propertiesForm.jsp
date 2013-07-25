<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:form action="properties.html" modelAttribute="propertiesForm">
    <table>
        <tr>
            <td><b>Repository Directory:</b></td>
            <td><form:input path="repository_dir"></form:input></td>
        </tr>
        <tr>
            <td>Directory of the REPOX Repository (XML files)</td>
        <tr>
            <td><b>Configuration Files Directory</b></td>
            <td><form:input path="xmlConfig_dir"></form:input></td>
        </tr>
        <tr>
            <td>Directory of the REPOX XML Dynamic 
                <br>Configuration Files (DataProviders, Statistics)</td>
        </tr>
        <tr>
            <td><b>OAI-PMH Requests Directory</b></td>
            <td><form:input path="oairequests_dir"></form:input></td>
        </tr>
        <tr>
            <td>Directory for the temporary OAI-PMH requests</td>
        </tr>
        <tr>
            <td><b>Derby Database Directory</b></td>
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
            <td><b>Administrator Email</b></td>
            <td><form:input path="administrator_email"></form:input></td>
        </tr>
        <tr>
            <td><b>SMTP Server for Administrator Email</b></td>
            <td><form:input path="smtp_server"></form:input></td>
        </tr>
        <tr>
            <td><b>REPOX Default Email</b></td>
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
