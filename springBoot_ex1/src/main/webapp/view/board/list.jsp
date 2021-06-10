<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>        
<!DOCTYPE html><html><head>
<meta charset="EUC-KR"><title>�Խù� ��� ����</title>
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/main.css">
</head><body>
<table>
  <caption>MODEL1 �Խ��� ���</caption>
  <c:if test="${boardcount == 0 }" >
  <tr><td colspan="5">��ϵ� �Խñ��� �����ϴ�.</td></tr>
 </c:if>
 <c:if test="${boardcount != 0 }" >
  <tr><td colspan="5" style="text-align:right">�۰���:${boardcount}</td></tr>
  <tr><th width="8%">��ȣ</th><th width="50%">����</th>
      <th width="14%">�ۼ���</th><th width="17%">�����</th>
      <th width="11%">��ȸ��</th></tr>
<c:forEach   var="b"   items="${list}">
   <tr><td>${boardnum}</td><td style="text-align: left">
   <c:set var="boardnum"  value="${boardnum-1 }" />
   	<c:if test="${b.file1 != null && !b.file1.trim() eq ''}">
      <a href="upfile/${b.file1}" style="text-decoration: none;">@</a>
      </c:if>
    <c:if test="${b.file1 == null || b.file1.trim() eq ''}">
     &nbsp;&nbsp;&nbsp; </c:if>
     
        <c:if test="${b.reflevel > 0}">
	       <c:forEach var="i"  begin="1"  end="${b.reflevel-1 }">
		  
	              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	      
	      </c:forEach>	       
	  ��</c:if>   
	  <a href="info?num=${b.num}" >${b.subject}</a>
		</td><td>${b.name}</td><td>${b.regdate}</td>
		<td>${b.readcnt}</td></tr>
		</c:forEach>
		
   <tr><td colspan="5">
   	<c:if test="${startpage <= bottomLine}">[����] </c:if>
     <c:if test="${startpage > bottomLine}">
      <a href="list?page=${startpage - bottomLine}">[����]</a></c:if>
      
      <c:forEach  var="a"  begin="${startpage}"   end="${endpage}">
      
      <c:if test="${a==pageNum }">[${a}] </c:if>
       <c:if test="${a!=pageNum }">
        
           <a href="list?page=${a}">[${a}]</a>
         </c:if>
         </c:forEach>
        <c:if test="${endpage >= maxpage}">  [����]</c:if>
       <c:if test="${endpage < maxpage}">
          <a href="list?page=${startpage + bottomLine}">[����]</a></c:if>
    </td></tr>  
  
   
</c:if>


  <tr><td colspan="5" style="text-align:right">
     <a href="writeForm">[�۾���]</a></td></tr>
</table></body></html>		
   
   
    
    

		