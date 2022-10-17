<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page import="java.util.Calendar" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="todo.PlanBean" %>
    <%@ page import="todo.LogBean" %>
    <%@ page import="java.util.Collections" %>
    <%@ page import="java.time.LocalDate" %>
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
	@SuppressWarnings("unchecked")
	List<LogBean> logs = (ArrayList<LogBean>) session.getAttribute("logarray");
	
	if(logs.size()!=0 && logs.get(0).getLogid()<logs.get(logs.size()-1).getLogid()){
		Collections.reverse(logs);
	}
	List<LogBean> rlogArray;
	if(logs.size() <10){
		rlogArray = logs.subList(0,logs.size());
	}else{
		rlogArray = logs.subList(0,10);
	}
	request.setAttribute("rlogarray",rlogArray);
	
    %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>目標・スケジュール管理（仮）</title>
<style>
table#daily{border:1px solid;margin:0;padding:0;width:100%;height:100%}
.day{border:1px solid;min-height: 50px}
#left{margin:0;padding:0;float:left;width:45%;height:50%}
#right{margin:0;padding:0;float:right;width:45%;height:auto}
#target{margin-top:50px;padding:0;width:100%}
#schedule{margin:0;padding:0;width:100%}
#inp{margin:0;padding:0;width:100%}
.reset{max-width:70px}
#log{margin:0;padding:20px;clear:both}
#logs{margin-left: auto;margin-right: auto;width:80%}
#cont{margin:10px;width:90%;height:40%}
#setting{ text-align:right }
</style>
</head>
<body>

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
<h1>目標・スケジュール管理（仮）</h1>
<table id="daily">
<tr><th class="day"><c:out value="本日(${ y }/${ m }/${ d })の予定"/></th></tr>
<tr><td class="day">
<c:forEach var="today" items="${ todayarray }">
<c:choose>
<c:when test="${ today.hold == true}"></c:when>
<c:otherwise>
・<c:out value="${ today.title }"/> <a href="/ToDo/jsp/edit.jsp?id=${ today.id }&level=${ today.level }">edit</a> <a href="/ToDo/EditController?id=${ today.id }&level=${ today.level }&action=del">del</a><br/>
</c:otherwise>
</c:choose>
</c:forEach>
</td></tr>
<tr><th class="day">小目標</th></tr>
<tr><td class="day">
<c:forEach var="smalls" items="${ smallarray }">
<c:choose>
<c:when test="${ smalls.hold == true}"></c:when>
<c:otherwise>
・<c:out value="(${ smalls.big_title })-(${ smalls.middle_title })-"/><a href="/ToDo/jsp/top.jsp?id=${ smalls.id }&level=small"><c:out value="${ smalls.title }"/></a> <a href="/ToDo/jsp/edit.jsp?id=${ smalls.id }&level=${ smalls.level }">edit</a> <a href="/ToDo/EditController?id=${ smalls.id }&level=${ smalls.level }&action=del">del</a>
</c:otherwise>
</c:choose>
<br/>
</c:forEach>
</td></tr>
</table>
<table border="1" id="cont">
<tr><td>
<c:out value="${ nbean.content }"/>
</td></tr>
</table>
</div>
<div id="right">
<div id="setting"><h5>[<a href="/ToDo/jsp/hold.jsp">保留状態</a>]</h5></div>
<table border=1 id="target">
<tr><th>大目標</th></tr>
<tr><td>
<c:forEach var="bigs" items="${ bigarray }">
<c:choose>
<c:when test="${ bigs.hold == true }"></c:when>
<c:otherwise>
・<c:out value="${ bigs.title }"/> <a href="/ToDo/jsp/edit.jsp?id=${ bigs.id }&level=${ bigs.level }">edit</a> <a href="/ToDo/EditController?id=${ bigs.id }&level=${ bigs.level }&action=del">del</a><br/>
</c:otherwise>
</c:choose>
</c:forEach>
</td></tr>
<tr><th>中目標</th></tr>
<tr><td>
<c:forEach var="middles" items="${ middlearray }">
<c:choose>
<c:when test="${ middles.hold == true }"></c:when>
<c:otherwise>
・<c:out value="(${ middles.big_title })-"/><c:out value="${ middles.title }"/> <a href="/ToDo/jsp/edit.jsp?id=${ middles.id }&level=${ middles.level }">edit</a> <a href="/ToDo/EditController?id=${ middles.id }&level=${ middles.level }&action=del">del</a>
<br/></c:otherwise>
</c:choose>
</c:forEach>
</td></tr>
</table><br/>
<table border="1" id="schedule">
<tr><th>スケジュール(<c:out value="${ y }/${ m }/${ d+1 }-${ y }/${ m }/${ d+7 }"/>)[<a href="/ToDo/jsp/schedule.jsp">全スケジュール</a>]</th></tr>
<tr><td><c:forEach var="schedule" items="${ weekarray }">
<c:choose>
<c:when test="${ schedule.hold == true }"></c:when>
<c:otherwise>
・<c:out value="${ schedule.title }"/><c:out value="-(${ schedule.date })"/> <a href="/ToDo/jsp/edit.jsp?id=${ schedule.id }&level=${ schedule.level }">edit</a> <a href="/ToDo/EditController?id=${ schedule.id }&level=${ schedule.level }&action=del">del</a><br/>
</c:otherwise>
</c:choose>
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
</td></tr>
<tr><th>保留状態</th><td><input type="checkbox" name="hold" value="on"></td></tr>
<tr><td colspan="2">
<input type="submit" value="登録"/>
<input type="button" onclick="allReset()" value="やり直し"/>
</td></tr>
</table>
</form>
</div>
<br/>
<div id="log">
<table border="1" id="logs">
<tr><th colspan="2">更新ログ（最新10件）[<a href="/ToDo/jsp/log.jsp">編集ログを全て見る</a>]</th></tr>

<c:forEach var="rlog" items="${ rlogarray }" begin="0" end="9" step="1"><tr><td>
・<c:choose>
<c:when test="${ (rlog.ope == 'insert') && (rlog.after_level == 'big') }">
<c:out value="[大目標]に[${ rlog.after_title }]を登録しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'insert') && (rlog.after_level == 'middle') }">
<c:out value="[中目標]に[${ rlog.after_title }-(${ rlog.after_big_title })]を登録しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'insert') && (rlog.after_level == 'small') }">
<c:out value="[小目標]に[${ rlog.after_title }-(${ rlog.after_big_title })-(${ rlog.after_middle_title })]を登録しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'insert') && (rlog.after_level == 'sche') }">
<c:out value="[スケジュール]に[${ rlog.after_title }(${ rlog.after_date })]を登録しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'delete') && (rlog.before_level == 'big') }">
<c:out value="[大目標]の[${ rlog.before_title }]を削除しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'delete') && (rlog.before_level == 'middle') }">
<c:out value="[中目標]の[${ rlog.before_title }-(${ rlog.before_big_title })]を削除しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'delete') && (rlog.before_level == 'small') }">
<c:out value="[小目標]の[${ rlog.before_title }-(${ rlog.before_big_title })-(${ rlog.before_middle_title })]を削除しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'delete') && (rlog.before_level == 'sche') }">
<c:out value="[スケジュール]の[${ rlog.before_title }(${ rlog.before_date })]を削除しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'update') && (rlog.before_level == 'big') }">
<c:out value="[大目標]の[${ rlog.before_title }]を[${ rlog.after_title }]に変更しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'update') && (rlog.before_level == 'middle') }">
<c:out value="[中目標]の[${ rlog.before_title }-(${ rlog.before_big_title })]を[${ rlog.after_title }-(${ rlog.after_big_title })]に変更しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'update') && (rlog.before_level == 'small') }">
<c:out value="[小目標]の[${ rlog.before_title }-(${ rlog.before_big_title })-(${ rlog.before_middle_title })]を[${ rlog.after_title }-(${ rlog.after_big_title })-(${ rlog.after_middle_title })]に変更しました。"/>
</c:when>
<c:when test="${ (rlog.ope == 'update') && (rlog.before_level == 'sche') }">
<c:out value="[スケジュール]の[${ rlog.before_title }(${ rlog.before_date })]を[${ rlog.after_title }(${ rlog.after_date })]に変更しました。"/>
</c:when></c:choose>
</td>
<td class="reset"><form action="/ToDo/resetController" method="post" name="rform">
<input type="hidden" name="logid" value="${ rlog.logid }">
<input type="submit" value="変更リセット"/></form></td></tr>

</c:forEach>
</table></div>
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