<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <style>
        .chatting-area{
            margin: auto;
            height: 600px;
            width: 800px;
            margin-top: 50px;
            margin-bottom: 50px;
        }
        #exit-area{
            text-align: right;
            margin-bottom: 10px;
        }
        .display-chatting {
            width: 100%;
            height: 450px;
            border: 1px solid black;
            overflow: auto;
            list-style: none;
            padding: 10px 10px;
        }
        .chat{
            display: inline-block;
            border-radius: 5px;
            padding: 5px;
            background-color: #eee;
        }
        .input-area{
            width: 100%;
            display: flex;
        }
        #inputChatting{
            width: 80%;
            resize: none;
        }
        #send{
            width: 20%;
        }
        .myChat{
            text-align: right;

        }
        .myChat > p {
            background-color: yellow;
        }
        .chatDate {
            font-size: 11px;
        }
    </style>

</head>
<body>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="chatting-area">
    <div id="exit-area">
        <button class="btn btn-outline-danger" id="exit-btn">나가기</button>
    </div>
    <ul class="display-chatting">

        <c:forEach items="${list}" var="msg">
            <fmt:formatDate var="chatDate" value="${msg.createDate}" pattern="yyyy년 MM월 dd일 HH:mm:ss"/>

            <%--                    1) 내가 보낸 메세지--%>
            <c:if test="${msg.userNo == loginUser.userNo}">
                <li class="myChat">
                    <span class="chatDate">${chatDate}</span>
                    <p class="chat">${msg.message}</p>
                </li>

            </c:if>

            <%--                    2) 남(이름)이 보낸 메세지--%>
            <c:if test="${msg.userNo != loginUser.userNo}">
                <li>
                    <b>${msg.userName}</b>
                    <p class="chat">${msg.message}</p>
                    <span class="chatDate">${chatDate}</span>
                </li>
            </c:if>
        </c:forEach>
    </ul>

    <div class="input-area">
        <textarea id="inputChatting" rows="3"></textarea>
        <button id="send">보내기</button>
    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<%-- sockjs --%>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>

<script>ㄷ
    // el태그를 통해 js변수 셋팅
    const userNo = "${loginUser.userNo}";
    const userEmail = "${loginUser.email}";
    const userName = "${loginUser.userName}";
    const chatRoomNo = "${chatRoomNo}"
    const contextPath = "${contextPath}"  // 어플리케이션 스코프에 저장되어있음

    // chat 이라는 요청주소로 통신할수 있는 webSocket 객체 생성 -> /spring/chat

    let chatSocket = new SockJS(contextPath + "/chat");
    // -> websocket 프로토콜을 이용해서 해당 주소로 데이터를 송/수신 할 수 있다.

    /*
    webSocket
    - 브라우저와 웹 서버간의 전이중 통신을 지원하는 프로토콜

    * 전이중 통신(full duplex) : 두대의 단말기가 동시에 송/수신하기위해 각각 독립된 회선을 사용하는 통신방식

    * HTML5 , JAVA7 버전 이상, SPRING 4 버전 이상에서 지원.
     */
</script>

<script src="${contextPath}/resources/js/chat.js"></script>

</body>
</html>
