<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="todo.PlanBean" %>
    <%
    //編集する目標のidとlevelを取得
 int id = Integer.parseInt(request.getParameter("id"));
 String level  = request.getParameter("level");
 request.setAttribute("id",id);
 request.setAttribute("level",level);
 
 PlanBean pbean = null;
 if(level.equals("big")){
		@SuppressWarnings("unchecked")
		ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
		for(PlanBean b:bigArray){
			if(id == b.getId()){
				pbean = b;
				break;
			}
		}
 }else if(level.equals("middle")){
		@SuppressWarnings("unchecked")
		ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
		for(PlanBean m:middleArray){
			if(id == m.getId()){
				pbean = m;
				break;
			}
		}
 }else if(level.equals("small")){
		@SuppressWarnings("unchecked")
		ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
		for(PlanBean s:smallArray){
			if(id == s.getId()){
				pbean = s;
				break;
			}
		}
 }else if(level.equals("sche")){
		@SuppressWarnings("unchecked")
		ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
		for(PlanBean sc:scheArray){
			if(id == sc.getId()){
				pbean = sc;
				break;
			}
		}
		int year = Integer.parseInt(pbean.getDate().substring(0,4));
		int month = Integer.parseInt(pbean.getDate().substring(5,7));
		int day = Integer.parseInt(pbean.getDate().substring(8,10));
		request.setAttribute("year",year);
		request.setAttribute("month",month);
		request.setAttribute("day", day);
		
 }
 request.setAttribute("pbean",pbean);
 
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>目標、スケジュールの編集</title>
</head>
<body>
<h1>目標、スケジュールの編集</h1><p/>
<!-- 目標の編集・削除 -->
<form action="/ToDo/EditController" method="post" name="eform">
<input type="hidden" name="id" value="${ id }">
<input type="hidden" name="action" value="edi">
<table border=1 id="inp">
<tr>
<th>目標</th>
<td>
<input type="text" name="title" size="20" value="${ pbean.title }"/>
</td>
</tr>
<tr>
<th>目標ランク</th>
<td>
<c:choose>
<c:when test="${ level=='big' }">
<input type="hidden" name="level" value="big">
<c:out value="大目標"/>
</c:when>
<c:when test="${ level=='middle' }">
<input type="hidden" name="level" value="middle">
<c:out value="中目標"/>
</c:when>
<c:when test="${ level=='small' }">
<input type="hidden" name="level" value="small">
<c:out value="小目標"/>
</c:when>
<c:when test="${ level=='sche' }">
<input type="hidden" name="level" value="sche">
<c:out value="予定"/>
</c:when>
</c:choose>
</td>
</tr>
<tr>
<th>目標の内容</th>
<td>
<textarea name="content" cols="30" rows="10" wrap="soft"><c:out value="${ pbean.content }"/></textarea >
</td>
</tr>
<tr>
<th>各属性</th>
<td id="att">
<c:choose>
<c:when test="${ level=='big' }">
</c:when>
<c:when test="${ level=='middle' }">
<select name="big_ReadyMade" id="bb">

      <c:forEach var="bigg" items="${ bigarray }">
      <c:choose>
      <c:when test="${ pbean.big == bigg.id }">
        <option value="${ bigg.id }" selected><c:out value="${ bigg.title }"/></option>
      </c:when>
      <c:otherwise>
        <option value="${ bigg.id }"><c:out value="${ bigg.title }"/></option>
      </c:otherwise>
      </c:choose>
      </c:forEach>
</select>
</c:when>
<c:when test="${ level=='small' }">
<select name="middle_ReadyMade" id="mm">
	<c:forEach var="middlee" items="${ middlearray }">
      <c:choose>
      <c:when test="${ pbean.middle == middlee.id }">
        <option value="${ middlee.id },${ middlee.big }" selected><c:out value="${ middlee.title }-(${ middlee.big_title })"/></option>
      </c:when>
      <c:otherwise>
        <option value="${ middlee.id },${ middlee.big }"><c:out value="${ middlee.title }-(${ middlee.big_title })"/></option>
      </c:otherwise>
      </c:choose>
	
	</c:forEach>
</select>
</c:when>
<c:when test="${ level=='sche' }">
<select name="year" id="tt1">
<c:forEach var="ye" begin="${ year-3 }" end="${ year+3 }" step="1">
<option <c:if test="${year == ye}"> selected </c:if>><c:out value="${ ye }"/></option>
</c:forEach>
</select>
<select name="month" id="tt2">
<c:forEach var="mo" begin="1" end="12" step="1">
<option <c:if test="${month == mo}"> selected </c:if>><c:out value="${ mo }"/></option>
</c:forEach></select>
<select name="day" id="tt3">
<c:forEach var="da" begin="1" end="31" step="1">
<option <c:if test="${day == da}"> selected </c:if>><c:out value="${ da }"/></option>
</c:forEach></select>
</c:when>
</c:choose>
</td></tr>
<tr><th>保留状態</th><td><input type="checkbox" name="hold" value="on" ></td></tr>
<tr><td colspan="2">
<input type="submit" value="編集完了"/>
<input type="reset" value="やり直し"/>
</td></tr>
</table>
</form>
<br/>
<a href="/ToDo/jsp/top.jsp">目標・スケジュール管理へ戻る</a>
</body>
</html>