<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <% String success = request.getParameter("success"); %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>目標・スケジュール管理（仮）</title>
<style>
.button{text-align:center;}
#login{margin:5%;padding:0;float:left;}
#register{margin:5%;padding:0;float:left;}
</style>
</head>
<body>
<h1>目標・スケジュール管理（仮）</h1>
<% 
int s = 0;
if(request.getAttribute("success")!=null){
	s =(Integer) request.getAttribute("success");
}

if(s==1){ 
	out.println("<h2>登録完了しました。</h2>");
 }else if(s==2){ 
	out.println("<h2>登録失敗しました。もう一度お試しください。</h2>");
 } %>
<form action="/ToDo/LoginController" method="post">
<table border=1 id="login">
<tr><th colspan=2>アカウントログイン</th></tr>
<tr><th>ユーザーID</th>
<td><input type="text" name="userId" size="30" required/></td></tr>
<tr><th>パスワード</th>
<td><input type="password" name="password" size="30" required/></td></tr>

<tr><td colspan="2">
<div class="button">
<input type="submit" value="ログイン"/>
<input type="reset" value="やり直し"/>
</div>
</td></tr>
</table>
</form>
<form action="/ToDo/RegisterController" method="post">
<table border="1" id="register">
<tr><th colspan=2>アカウント新規登録</th></tr>
<tr><th>ユーザーネーム(省略可)</th>
<td><input type="text" name="username" size="30"></td></tr>
<tr><th>ユーザーID</th>
<td><input type="text" name="userId" size="30" required/></td></tr>
<tr><th>パスワード</th>
<td><input type="text" name="password" size="30" required/></td></tr>
<tr><td colspan="2">
<div class="button">
<input type="submit" value="新規登録"/>
<input type="reset" value="やり直し"/>
</div></td></tr>
</table>
</form>

<% 
int e=0;
if(request.getAttribute("error")!=null){
	e = (Integer) request.getAttribute("error");
}
System.out.println(e);
if(e==1){
	out.println("<h2>ユーザー名かパスワードが間違っています。</h2>");
}else if(e==2){
	out.println("<h2>エラーが発生しました。やり直してください。</h2>");
}

%>


</body>
</html>