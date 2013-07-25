<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table>
    <tr>
        <td><font color=red>missing properties: </font></td>
        <td><font color=red><c:out value="${blank_fields}" /></font></td>
    </tr>
</table>
<hr />
<c:import url="propertiesForm.jsp" />
