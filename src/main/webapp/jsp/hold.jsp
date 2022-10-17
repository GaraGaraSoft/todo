<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>保留中の目標・スケジュール</h1>

<div id="log">
<table border="1" id="logs">
<tr><th colspan="2">大目標</th></tr>
<c:forEach var="big" items="${ bigarray }">
<c:choose>
<c:when test="${ big.hold == true }"><tr><td>
・<c:out value="${ big.title }"/>
</td>
<td class="reset"><form action="/ToDo/holdController" method="post" name="rform">
<input type="hidden" name="logid" value="${ big.id }">
<input type="submit" value="変更"/></form>
</td></tr>
</c:when>
</c:choose>
</c:forEach>

<tr><th colspan="2">中目標</th></tr>
<c:forEach var="middle" items="${ middlearray }">
<c:choose>
<c:when test="${ middle.hold == true }"><tr><td>
・<c:out value="${ middle.title }"/>
</td>
<td class="reset"><form action="/ToDo/holdController" method="post" name="rform">
<input type="hidden" name="logid" value="${ middle.id }">
<input type="submit" value="変更"/></form>
</td></tr></c:when>
</c:choose>
</c:forEach>

<tr><th colspan="2">小目標</th></tr>
<c:forEach var="small" items="${ smallarray }">
<c:choose>
<c:when test="${ small.hold == true }"><tr><td>
・<c:out value="${ small.title }"/>
</td>
<td class="reset"><form action="/ToDo/holdController" method="post" name="rform">
<input type="hidden" name="logid" value="${ small.id }">
<input type="submit" value="変更"/></form>
</td></tr></c:when>
</c:choose>
</c:forEach>

<tr><th colspan="2">スケジュール</th></tr>
<c:forEach var="sche" items="${ schearray }">
<c:choose>
<c:when test="${ sche.hold == true }"><tr><td>
・<c:out value="${ sche.title }"/>
</td>
<td class="reset"><form action="/ToDo/holdController" method="post" name="rform">
<input type="hidden" name="logid" value="${ sche.id }">
<input type="submit" value="変更"/></form>
</td></tr></c:when>
</c:choose>
</c:forEach>

</table></div>

</body>
</html>