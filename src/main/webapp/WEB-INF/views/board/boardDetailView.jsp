<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style>
        table * {margin:5px}
        table {width: 100%}
    </style>
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<div class="contents">
    <br><br>
    <div class="innerOuter">
        <h2>게시글 상세보기</h2>
        <br>
        <a class="btn btn-secondary" style="float: right;" href="list">목록으로</a>
        <br><br>
        <table id="contentArea" align="center" class="table">
            <tr>
                <th width="100">제목</th>
                <td colspan="3">${b.boardTitle}</td>
            </tr>
            <tr>
                <th>첨부파일</th>
                <td colspan="3">
                    <a href="${contextPath}/${b.changeName}" download="${b.originName}">${b.originName}</a>
                </td>
            </tr>
            <tr>
                <th>내용</th>
                <td colspan="3"></td>
            </tr>
            <tr>
                <td colspan="4"><p style="height: 150px;">${b.boardContent}</p></td>
            </tr>
        </table>
        <br>
        <div align="center">
            <!-- 수정하기, 삭제하기 버튼은 이 글이 본인이 작성한 글일경우에만 보여져야한다.-->
            <a class="btn btn-primary" href="">수정하기</a>
            <a class="btn btn-danger" href="${contextPath}/board/deleteBoard.bo?bno=${b.boardNo}">삭제하기</a>
        </div>
        <br><br>
        <!-- 댓글 등록 기능 -->
        <table id="replyArea" class="table" align="center">
            <thead>
                <tr>
                    <th colspan="2">
                        <textarea class="form-control" name="" id="content" rows="2" cols="55" style="resize: none; width: 100%"></textarea>
                    </th>
                    <th style="vertical-align: middle;"><button class="btn btn-secondary" onclick="insertReply();">등록하기</button></th>
                </tr>
                <tr>
                    <td colspan="3">댓글(<span id="recount"></span>)</td>
                </tr>
            </thead>
            <tbody>
                <!-- 스크립트 구문으로 댓글 추가 -->
            </tbody>
        </table>
    </div>
</div>


<jsp:include page="../common/footer.jsp"/>

<script>
    $(function (){
        reply();
    })

    function insertReply(){
        console.log($("#content").val())
        $.ajax({
            url:"rinsert.bo",
            data:{
                replyContent : $("#content").val(),
                refBno : ${b.boardNo}
            },
            success : (result) =>{
                if(result>0){
                    reply();
                    $("#content").val("");
                }
            },
            error : function (){
                console.log("댓글 작성용 ajax통신 실패");
            }
        })
    }


    function reply(){
            $.ajax({
                url : "reply.bo",
                data : {bno : ${b.boardNo}},
                dataType: "json",
                success : (list)=>{   // 화살표 함수
                    let result = "";
                    for(let i of list){ // list 배열에 담겨 있는 값들을 하나하나 꺼내서 i 에 담는 반복문
                        result += "<tr>"
                            +"<td>"+i.replyWriter +"</td>"
                            +"<td>"+i.replyContent+"</td>"
                            +"<td>"+i.createDate+"</td>"
                            + "</tr>";
                    }
                    $("#replyArea tbody").html(result);
                    $("#recount").html(list.length);
                },
                error : function(){
                    console.log("댓글리스트조회용 ajax 통신 실패~")
                }
            });
    }

</script>

</body>
</html>
