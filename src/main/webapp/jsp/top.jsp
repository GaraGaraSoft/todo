<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page import="java.util.Calendar" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="todo.PlanBean" %>
    <%  Calendar cal = Calendar.getInstance();
    int y = cal.get(Calendar.YEAR);
    int m = cal.get(Calendar.MONTH)+1;
    int d = cal.get(Calendar.DATE);
    pageContext.setAttribute("y",y);
    pageContext.setAttribute("m",m);
    pageContext.setAttribute("d",d);
	if(request.getParameter("id")!=null){
	    int id =  Integer.parseInt(request.getParameter("id"));
	    String level = request.getParameter("level");
    	@SuppressWarnings("unchecked")
		ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
   	 	if(level.equals("small")){
    		for(PlanBean small:smallArray){
    			if(id == small.getId()){
    				request.setAttribute("nbean", small);
    				break;
 	   			}
    		}
    	
    	}
    }
    %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>目標・スケジュール管理（仮）</title>
<style>
table#daily{border:1px solid;margin:0;padding:0;width:100%;height:100%}
.day{border:1px solid;min-height: 50px}
#left{margin:0;padding:0;float:left;width:45%;height:auto}
#right{margin:0;padding:0;float:right;width:45%;height:auto}
#target{margin:0;padding:0;width:100%}
#schedule{margin:0;padding:0;width:100%}
#inp{margin:0;padding:0;width:100%}
        
</style>
</head>
<body>
<h1>目標・スケジュール管理（仮）</h1>

<% 
int e=0;
if(request.getAttribute("error")!=null){
	e = (Integer) request.getAttribute("error");
}
System.out.println(e);
if(e==1){
	out.println("<p><h3>エラーが発生しました。やり直してください。</h3></p>");
}

%>

<div id="left">
<table id="daily">
<tr><th class="day"><c:out value="本日(${ y }/${ m }/${ d })の予定"/></th></tr>
<tr><td class="day">
<c:forEach var="today" items="${ todayarray }">
・<c:out value="${ today.title }"/> <a href="/ToDo/jsp/edit.jsp?id=${ today.id }&level=${ today.level }">edit</a><br/>
</c:forEach>
</td></tr>
<tr><th class="day">小目標</th></tr>
<tr><td class="day">
<c:forEach var="smalls" items="${ smallarray }">
・<c:out value="(${ smalls.big_title })-(${ smalls.middle_title })-"/><a href="/ToDo/jsp/top.jsp?id=${ smalls.id }&level=small"><c:out value="${ smalls.title }"/></a> <a href="/ToDo/jsp/edit.jsp?id=${ smalls.id }&level=${ smalls.level }">edit</a>
<br/>
</c:forEach>
</td></tr>
</table>
<table border="1">
<tr><td>
<c:out value="${ nbean.content }"/>
</td></tr>
</table>
</div>
<div id="right">
<table border=1 id="target">
<tr><th>大目標</th></tr>
<tr><td>
<c:forEach var="bigs" items="${ bigarray }">
・<c:out value="${ bigs.title }"/> <a href="/ToDo/jsp/edit.jsp?id=${ bigs.id }&level=${ bigs.level }">edit</a><br/>
</c:forEach>
</td></tr>
<tr><th>中目標</th></tr>
<tr><td>
<c:forEach var="middles" items="${ middlearray }">
・<c:out value="(${ middles.big_title })-"/><c:out value="${ middles.title }"/> <a href="/ToDo/jsp/edit.jsp?id=${ middles.id }&level=${ middles.level }">edit</a>
<br/>
</c:forEach>
</td></tr>
</table><br/>
<table border="1" id="schedule">
<tr><th>スケジュール</th></tr>
<tr><td><c:forEach var="schedule" items="${ schearray }">
・<c:out value="${ schedule.title }"/><c:out value="-(${ schedule.year }/${ schedule.month }/${ schedule.day })"/> <a href="/ToDo/jsp/edit.jsp?id=${ schedule.id }&level=${ schedule.level }">edit</a><br/>
</c:forEach>
</td></tr>
</table><br/>

<!-- 新しい目標入力部分 -->
<form action="/ToDo/ToDoController" method="post" name="iform">
<table border=1 id="inp">
<tr>
<th>目標</th>
<td>
<input type="text" name="title" size="20"/>
</td>
</tr>
<tr>
<th>目標ランク</th>
<td>
<select name="level" id="level">
<option value="big">大目標</option>
<option value="middle">中目標</option>
<option value="small">小目標</option>
<option value="sche">予定</option>
</select>
</td>
</tr>
<tr>
<th>目標の内容</th>
<td>
<textarea name="content" cols="30" rows="10" wrap="soft"></textarea >
</td>
</tr>
<tr>
<th>各属性</th>
<td id="att">
<select name="big_ReadyMade" disabled id="bb">

      <c:forEach var="bigg" items="${ bigarray }">
        <option value="${ bigg.id }"><c:out value="${ bigg.title }"/></option>
      </c:forEach>
</select><br/>
<select name="middle_ReadyMade" disabled id="mm">
	<c:forEach var="middlee" items="${ middlearray }">
	<option value="${ middlee.id },${ middlee.big }"><c:out value="${ middlee.title }-(${ middlee.big_title })"/></option>
	</c:forEach>
</select><br/>
<select name="year" disabled id="tt1">
<c:forEach var="ye" begin="${ y }" end="${ y+4 }" step="1">
<option><c:out value="${ ye }"/></option>
</c:forEach>
</select>
<select name="month" disabled id="tt2">
<c:forEach var="mo" begin="1" end="12" step="1">
<option <c:if test="${m == mo}"> selected </c:if>><c:out value="${ mo }"/></option>
</c:forEach></select>
<select name="day" disabled id="tt3">
<c:forEach var="da" begin="1" end="31" step="1">
<option <c:if test="${d == da}"> selected </c:if>><c:out value="${ da }"/></option>
</c:forEach></select>

</td>
</tr>
<tr><td colspan="2">
<input type="submit" value="登録"/>
<input type="button" onclick="allReset()" value="やり直し"/>
</td></tr>
</table>
</form>
</div>
<script>
	let l = document.getElementById('level');
	l.addEventListener('change', inputChange);


	let b = document.getElementById('bb');
	let m = document.getElementById('mm');
	let t1 = document.getElementById('tt1');
	let t2 = document.getElementById('tt2');
	let t3 = document.getElementById('tt3');
	function inputChange(event){
		if(event.currentTarget.value=='big'){
			b.setAttribute('disabled', 'disabled');
			m.setAttribute('disabled', 'disabled');
			t1.setAttribute('disabled', 'disabled');
			t2.setAttribute('disabled', 'disabled');
			t3.setAttribute('disabled', 'disabled');
		}
		else if(event.currentTarget.value=='middle'){
			b.removeAttribute("disabled");
			m.setAttribute('disabled', 'disabled');
			t1.setAttribute('disabled', 'disabled');
			t2.setAttribute('disabled', 'disabled');
			t3.setAttribute('disabled', 'disabled');
			}
		else if(event.currentTarget.value=='small'){
			b.setAttribute('disabled', 'disabled');
			m.removeAttribute("disabled");
			t1.setAttribute('disabled', 'disabled');
			t2.setAttribute('disabled', 'disabled');
			t3.setAttribute('disabled', 'disabled');

			}
		else if(event.currentTarget.value=='sche'){
			b.setAttribute('disabled', 'disabled');
			m.setAttribute('disabled', 'disabled');
			t1.removeAttribute("disabled");
			t2.removeAttribute("disabled");
			t3.removeAttribute("disabled");

			}
	}

	function allReset(){
		 document.iform.reset();
		b.setAttribute('disabled', 'disabled');
		m.setAttribute('disabled', 'disabled');
		t1.setAttribute('disabled', 'disabled');
		t2.setAttribute('disabled', 'disabled');
		t3.setAttribute('disabled', 'disabled');
		}
</script>
</body>
</html>