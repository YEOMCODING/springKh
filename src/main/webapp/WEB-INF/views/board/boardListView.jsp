<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="list" value="${map.list}"/>
<c:set var="pi" value="${map.pi}"/>
<!--
	검색을 진행한 경우 key, query를 쿼리스트링 형태로 저장한 변수 생성.
-->
<c:if test="${!empty param.condition}">
	<c:set var="sUrl" value="&condition=${param.condition}&keyword=${param.keyword}"/>
</c:if>

<c:forEach items="${boardTypeList}" var="bt">
	<c:if test="${boardCode == bt.boardCd}">
		<c:set var="boardNM" value="${bt.boardName}"/>
	</c:if>
</c:forEach>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
		#boardList {text-align:center;}
        #boardList>tbody>tr:hover {cursor:pointer;}

        #pagingArea {width:fit-content; margin:auto;}

        #searchForm {
            width:80%;
            margin:auto;
        }
        #searchForm>* {
            float:left;
            margin:5px;
        }
        .select {width:20%;}
        .text {width:53%;}
        .searchBtn {width:20%;}
</style>
</head>
<body>
	<jsp:include page="../common/header.jsp"/>
	
	<div class="content">
		<br><br>
		<div class="innerOuter" style="padding: 5% 10%;">
			<h2>${boardNM}</h2>
			<br>
			<!-- 로그인시에만 보이는 글쓰기 버튼. -->
			<c:if test="${ not empty loginUser }">
			<a class="btn btn-secondary" style="float:right;" href="enrollForm.bo">
			글쓰기</a>
			</c:if>
			<br>
			<br>
			<table id="boardList" class="table table-hover" align="center">
				<thead>
					<tr>
						<th>글번호</th>
						<th>제목</th>
						<th>작성자</th>
						<th>조회수</th>
						<th>작성일</th>
						<th>첨부파일</th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${empty list }">
						<td colspan="6">게시글이 없습니다..</td>
					</c:if>
					<c:forEach var="b" items="${list }">
						<tr onclick="movePage(${b.boardNo});">
							<td class="bno">${b.boardNo }</td>
							<td>${b.boardTitle }</td>
							<td>${b.boardWriter }</td>
							<td>${b.count }</td>
							<td>${b.createDate }</td>
							<td>
								<c:if test="${ not empty b.originName }">
									♥첨부파일♥
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		
			<!-- 게시글 클릭했을때 게시글 상세보기화면으로 이동하는 스크립트 구현 -->
			 <script>
				 function movePage(bno) {
					 location.href = '${contextPath}/board/detail.bo?bno='+bno;
				 }

				 <%--$(function (){--%>
					<%-- $("#boardList>table>tbody tr").on("click", function (){--%>
					<%--	let bno = $(this).children().eq(0).text();--%>
					<%--	 location.href = '${pageContext.request.contextPath}/board/detail.bo?bno='+bno;--%>
					<%-- });--%>
				 <%--})--%>
			 
			 </script>
		
			<br>


			<c:set var="url" value="${boardCode}?cpage="/>
			<!-- 페이지 이동기능 구현 -->
			<div id="pagingArea">
				<ul class="pagination">
					<c:choose>
						<c:when test="${pi.currentPage eq 1}">
							<li class="page-item "><a class="page-Link disabled" herf="#">이전</a>
						</c:when>
						<c:otherwise>
							<li class="page-item"><a class="page-link" href="${url}${pi.currentPage - 1}${sUrl}">이전</a></li>
						</c:otherwise>
					</c:choose>

					<c:forEach var="item" begin="${pi.startPage }" end="${pi.endPage }">
						<li class="page-item"><a class="page-link" href="${url}${item }${sUrl}">${item }</a></li>
					</c:forEach>

					<c:choose>
						<c:when test="${pi.currentPage eq pi.maxPage}">
							<li class="page-item "><a class="page-Link disabled" herf="#">다음</a>
						</c:when>
						<c:otherwise>
							<li class="page-item"><a class="page-link" href="${url}${pi.currentPage + 1}${sUrl}">다음</a></li>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
		
			
			<!-- 검색 기능 구현 -->
			<br clear="both"><br>
		
			<form id="searchForm" action="${boardCode}" method="get" align="center">
				<div class="select">
					<input type="hidden" name="currentPage" value="1">
					<select class="custom-select" name="condition">
						<option value="writer">작성자</option>
						<option value="title">제목</option>
						<option value="content">내용</option>
						<option value="titleAndContent">제목 + 내용</option>
					</select>

					<c:if test="${not empty condition }">
					<script>
						let condition = document.querySelector(".select select option[value=${condition}]");
						condition.selected = true;
					</script>
					</c:if>

				</div>
				<div class="text">
					<input type="text" class="form-control" name="keyword" value="${keyword}">
				</div>
				<button type="submit" class="searchBtn btn btn-secondary">검색</button>
			</form>
			<br><br>
		</div>
		<br><br>
		
	
	</div>

	<jsp:include page="../common/footer.jsp"/>
</body>
</html>