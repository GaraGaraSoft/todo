<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.Calendar" %>
    <%@ page import="todo.PlanBean" %>
    <%
    //編集する目標のidとlevelを取得
 int id = Integer.parseInt(request.getParameter("id"));
 String level  = request.getParameter("level");
 if(request.getParameterValues("hold")!=null){
 	String hold = request.getParameterValues("hold")[0];
	 request.setAttribute("hold",hold);
 }
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
		//スケジュール編集時の元スケジュール日時を取得設定
		int year = Integer.parseInt(pbean.getDate().substring(0,4));
		int month = Integer.parseInt(pbean.getDate().substring(5,7));
		int day = Integer.parseInt(pbean.getDate().substring(8,10));
		request.setAttribute("year",year);
		request.setAttribute("month",month);
		request.setAttribute("day", day);
 }
		//データ更新当日の日時を取得設定
		Calendar cal = Calendar.getInstance();
	    int toyear = cal.get(Calendar.YEAR);
	    int tomonth = cal.get(Calendar.MONTH)+1;
	    int today = cal.get(Calendar.DATE);
	    request.setAttribute("ty",toyear);
	    request.setAttribute("tm",tomonth);
	    request.setAttribute("td",today);
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
<input type="hidden" name="beforelevel" value="${ level }">
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

<select name="afterlevel" id="level">
<option value="big" <c:if test="${ level == 'big' }">selected</c:if>>大目標</option>
<option value="middle" <c:if test="${ level == 'middle' }">selected</c:if>>中目標</option>
<option value="small" <c:if test="${ level == 'small' }">selected</c:if>>小目標</option>
<option value="sche" <c:if test="${ level == 'sche' }">selected</c:if>>予定</option>
</select>
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
<select name="big_ReadyMade" id="bb" <c:if test="${ level!='middle' }">disabled</c:if>>

      <c:forEach var="bigg" items="${ bigarray }">
      <c:choose>
      <c:when test="${ (level == 'big') && (bigg.id == id) }">
      </c:when>
      <c:when test="${ pbean.big == bigg.id }">
        <option value="${ bigg.id }" selected><c:out value="${ bigg.title }"/></option>
      </c:when>
      <c:otherwise>
        <option value="${ bigg.id }"><c:out value="${ bigg.title }"/></option>
      </c:otherwise>
      </c:choose>
      </c:forEach>
</select><br/>
<select name="middle_ReadyMade" id="mm" <c:if test="${ level!='small' }">disabled</c:if>>
	<c:forEach var="middlee" items="${ middlearray }">
      <c:choose>
      <c:when test="${ (level == 'big') && (middlee.big == id) }">
      </c:when>
      <c:when test="${ (level == 'middle') && (middlee.id == id) }">
      </c:when>
      <c:when test="${ pbean.middle == middlee.id }">
        <option value="${ middlee.id },${ middlee.big }" selected><c:out value="${ middlee.title }-(${ middlee.big_title })"/></option>
      </c:when>
      <c:otherwise>
        <option value="${ middlee.id },${ middlee.big }"><c:out value="${ middlee.title }-(${ middlee.big_title })"/></option>
      </c:otherwise>
      </c:choose>
	
	</c:forEach>
</select><br/>
<select name="year" id="tt1" <c:if test="${ level!='sche' }">disabled</c:if>>
<c:choose>
<c:when test="${ level == 'sche' }">
<c:forEach var="ye" begin="${ year-3 }" end="${ year+3 }" step="1">
<option <c:if test="${year == ye}"> selected </c:if>><c:out value="${ ye }"/></option>
</c:forEach>
</c:when>
<c:otherwise>
<c:forEach var="ye" begin="${ ty-3 }"  end="${ ty+3 }" step="1">
<option <c:if test="${ty == ye}"> selected </c:if>><c:out value="${ ye }"/></option>
</c:forEach>
</c:otherwise>
</c:choose>
</select>

<select name="month" id="tt2" <c:if test="${ level!='sche' }">disabled</c:if>>
<c:forEach var="mo" begin="1" end="12" step="1">
<c:choose>
<c:when test="${level == 'sche'}">
<option <c:if test="${ month == mo }">selected</c:if>><c:out value="${ mo }"/></option>
</c:when>
<c:when test="${level != 'sche'}">
<option <c:if test="${ tm == mo }">selected</c:if>><c:out value="${ mo }"/></option>
</c:when>
<c:otherwise>
<option><c:out value="${ mo }"/></option>
</c:otherwise>
</c:choose>
</c:forEach>
</select>

<select name="day" id="tt3" <c:if test="${ level!='sche' }">disabled</c:if>>
<c:forEach var="da" begin="1" end="31" step="1">
<c:choose>
<c:when test="${level == 'sche'}">
<option <c:if test="${ day == da }">selected</c:if>><c:out value="${ da }"/></option>
</c:when>
<c:when test="${level != 'sche'}">
<option <c:if test="${ td == da }">selected</c:if>><c:out value="${ da }"/></option>
</c:when>
<c:otherwise>
<option><c:out value="${ da }"/></option>
</c:otherwise>
</c:choose>
</c:forEach>
</select>

</td></tr>
<tr><th>保留状態</th><td><input type="checkbox" name="hold" value="on" <c:if test="${ hold == 'on' }">checked</c:if>></td></tr>
<tr><td colspan="2">
<input type="submit" value="登録"/>
<input type="button" onclick="allReset()" value="やり直し"/>
</td></tr>
</table>
</form>
<br/>
<a href="/ToDo/jsp/top.jsp">目標・スケジュール管理へ戻る</a>
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
		 document.eform.reset();
		 let ilevel = '${ level }';
			if(ilevel=='big'){
				b.setAttribute('disabled', 'disabled');
				m.setAttribute('disabled', 'disabled');
				t1.setAttribute('disabled', 'disabled');
				t2.setAttribute('disabled', 'disabled');
				t3.setAttribute('disabled', 'disabled');
			}
			else if(ilevel=='middle'){
				b.removeAttribute("disabled");
				m.setAttribute('disabled', 'disabled');
				t1.setAttribute('disabled', 'disabled');
				t2.setAttribute('disabled', 'disabled');
				t3.setAttribute('disabled', 'disabled');
				}
			else if(ilevel=='small'){
				b.setAttribute('disabled', 'disabled');
				m.removeAttribute("disabled");
				t1.setAttribute('disabled', 'disabled');
				t2.setAttribute('disabled', 'disabled');
				t3.setAttribute('disabled', 'disabled');

				}
			else if(ilevel=='sche'){
				b.setAttribute('disabled', 'disabled');
				m.setAttribute('disabled', 'disabled');
				t1.removeAttribute("disabled");
				t2.removeAttribute("disabled");
				t3.removeAttribute("disabled");

				}
		 
		}
</script>
</body>
</html>