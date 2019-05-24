
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
<title>Save <<ObjectName>></title>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>

<body>
<div id="wrapper">
	<div id="header">
	<h2><<ObjectName>></h2>
	</div>
</div>

<div id="container">
	<h3>Save <<ObjectName>></h3>
	
	<form:form action="save<<ObjectName>>" modelAttribute="<<objectName>>" method="POST">
		<!--  need to associate this data with <<objectName>> id -->
		<form:hidden path="id"/> <!-- Use PK here? -->
		
		<table>
			<tbody>
			<tr>
				<td><label>First name:</label></td>
				<td><form:input path="<<columnName>>"
			</tr>
			
			</tbody>
		
		</table>
		
	
	
	
	
	
	</form:form>
	

</div>

</body>

</html>