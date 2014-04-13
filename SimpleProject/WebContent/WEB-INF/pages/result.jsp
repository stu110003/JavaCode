<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Ask Something</title>
</head>
<body>
  <table border="1">
	  <c:forEach items="${rows}" var="row">
	    <tbody>
		    <tr>
		      <td><c:out value="${row.id}"/></td>
		      <td><c:out value="${row.date}"/></td>
		      <td><c:out value="${row.price}"/></td>
		      <td><c:out value="${row.title}"/></td>
		    </tr>
	    </tbody>
	  </c:forEach>
  </table>
  <span style="color:#f00;"><c:out value="${errorMessage}"></c:out></span>
</body>
</html>