<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="todo.LogBean" %>
    <%@ page import="java.util.Collections" %>
    <%@ page import="java.util.List" %>
    <% 
	@SuppressWarnings("unchecked")
	List<LogBean> logs = (ArrayList<LogBean>) session.getAttribute("logarray");
	for(int i=0;i<logs.size();i++){
		System.out.println("てすと:"+logs.get(i).getLogid());
	}
	if(logs.get(0).getLogid()<logs.get(logs.size()-1).getLogid()){
		Collections.reverse(logs);
	}
	request.setAttribute("rlogarray",logs);
    %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>更新ログ</title>
<style>
.reset{max-width:60px}
#log{margin:0;padding:0;clear:both}
#logs{margin-top:10px;margin-left: auto;margin-right: auto;width:80%}
        
</style>
</head>
<body>
<h1>更新ログ</h1>

<div id="log">
<table border="1" id="logs">
<tr><th colspan="2">更新内容</th></tr>
<c:forEach var="rlog" items="${ rlogarray }"><tr><td class="logcon">
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
<c:out value="[スケジュール]に[${ rlog.after_title }(${ rlog.after_year }/${ rlog.after_month }/${ rlog.after_day })]を登録しました。"/>
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
<c:out value="[スケジュール]の[${ rlog.before_title }(${ rlog.before_year }/${ rlog.before_month }/${ rlog.before_day })]を削除しました。"/>
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
<c:out value="[スケジュール]の[${ rlog.before_title }(${ rlog.before_year }/${ rlog.before_month }/${ rlog.before_day })]を[${ rlog.after_title }(${ rlog.after_year }/${ rlog.after_month }/${ rlog.after_day })]に変更しました。"/>
</c:when>
</c:choose></td>
<td class="reset">更新をリセット</td>
</tr>
</c:forEach>
</table></div>
</body>
</html>