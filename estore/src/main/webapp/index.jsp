<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!--文件头开始-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%@include file="/header.jsp"%>

<!--文件体开始-->

<table cellspacing=1 cellpadding=3 align=center class=tableBorder2>
	<tr>
		<td height=25 valign=middle><img src="images/Forum_nav.gif"
			align="middle"> <a href=index.jsp>杰普电子商务门户</a> → <img
			border="0" src="images/dog.gif" width="19" height="18"> 欢迎<font
			color="red">${sessionScope.user.name}</font>光临！</td>
	</tr>
</table>
<br>

<table cellpadding=3 cellspacing=1 align=center class=tableborder1>
	<tr>
		<td valign=middle align=center height=25 background="images/bg2.gif"
			width=""><font color="#ffffff"><b>序号</b></font></td>
		<td valign=middle align=center height=25 background="images/bg2.gif"
			width=""><font color="#ffffff"><b>产品名称</b></font></td>
		<td valign=middle align=center height=25 background="images/bg2.gif"
			width=""><font color="#ffffff"><b>价格</b></font></td>
		<td valign=middle align=center height=25 background="images/bg2.gif"
			width=""><font color="#ffffff"><b>操作</b></font></td>
	</tr>

	<!-- 取得application对象中库存书籍 -->
	<c:forEach items="${booksFromDB}" var="book">
		<tr>
			<td class=tablebody2 valign=middle align=center width="">${book.id}</td>
			<td class=tablebody1 valign=middle width="">&nbsp;&nbsp;<a
				href="user/productDetail.jsp">${book.name}</a></td>
			<td class=tablebody2 valign=middle align=center width="">${book.price}</td>
			<td class=tablebody1 valign=middle align=center width=""><a
				href="user/addOrderline?bookid=${book.id}"> <img border="0"
					src="images/car_new.gif" width="97" height="18"></a></td>
		</tr>
	</c:forEach>

</table>
<!--文件尾开始-->
<%@include file="/footer.jsp"%>
</body>
</html>