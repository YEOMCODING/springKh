<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style>
        #enrollForm>table{width:100%;}
        #enrollForm>table*{margin: 5px;}

    </style>
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<div class="content">
    <br><br>
    <div class="innerOuter">
        <h2>게시글 작성하기</h2>
        <br>
        <form id="enrollForm" action="insert.bo" enctype="multipart/form-data" method="post">
            <table align="center">
                <tr>
                    <th><label for="title">제목</label></th>
                    <td><input type="text" id="title" class="form-control" name="boardTitle" required></td>
                </tr>
                <tr>
                    <th><label for="writer">작성자</label></th>
                    <td><input type="text" id="writer" class="form-control" name="boardWriter" required value="${loginUser.userNo }"></td>
                </tr>
                <tr>
                    <th><label for="upfile">첨부파일</label></th>
                    <td><input type="file" id="upfile" class="form-control" name="upfile" required></td>
                </tr>
                <tr>
                    <th><label for="content">내용</label></th>
                    <td><textarea id="content" style="resize: none;" rows="10" class="form-control" name="boardContent" required></textarea></td>
                </tr>
            </table>
            <br>
            <div align="center">
                <button type="submit" class="btn btn-primary">등록하기</button>
                <button type="reset" class="btn btn-danger">취소하기</button>
            </div>



        </form>
    </div>

</div>


<jsp:include page="../common/footer.jsp"/>

</body>
</html>
