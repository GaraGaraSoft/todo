<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page import="java.util.Calendar" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="todo.LoginBean" %>
    <%@ page import="todo.PlanBean" %>
    <%@ page import="todo.SQLOperator" %>
    <%  Calendar cal = Calendar.getInstance();
    int y = cal.get(Calendar.YEAR);
    int m = cal.get(Calendar.MONTH)+1;
    int d = cal.get(Calendar.DATE);
    pageContext.setAttribute("y",y);
    pageContext.setAttribute("m",m);
    pageContext.setAttribute("d",d);
    
	//セッションからログイン情報を取得
	LoginBean lbean = (LoginBean) session.getAttribute("loginbean");
    
	@SuppressWarnings("unchecked")
	ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
	ArrayList<PlanBean> scheSort = new ArrayList<>();
	
	scheSort = SQLOperator.setSchedule(scheArray,lbean.getUserid());
	
/* 	if(logs.size()!=0 && logs.get(0).getLogid()<logs.get(logs.size()-1).getLogid()){
		Collections.reverse(logs);
	} */
	
	request.setAttribute("schesort",scheSort);
	
    %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>スケジュール</title>
<style>
	#s1{margin:0;padding:0;width:80%}
	#s2{margin-top:20px;margin-left: auto;margin-right: auto;width:auto}
	.title{width:80%}
</style>
</head>
<body>
<h1>スケジュール</h1>

<div id="s1">
<table border="1" id="s2">
<tr><th colspan="4">スケジュール</th></tr>

<c:forEach var="sche" items="${ schesort }">
<c:choose>
<c:when test="${ sche.hold == true }"></c:when>
<c:otherwise>
<tr><td class="date"><c:out value="${ sche.date }"/></td>
<td class="title">・<c:out value="${ sche.title }"/></td>
<td class="e"><a href="/ToDo/jsp/edit.jsp?id=${ sche.id }&level=${ sche.level }">edit</a></td>
<td class="d"><a href="/ToDo/EditController?id=${ sche.id }&level=${ sche.level }&action=del">del</a>
</td></tr>
</c:otherwise>
</c:choose>
</c:forEach>
</table></div>
</body>
</html>