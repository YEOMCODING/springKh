package com.kh.spring.chat.model.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.sql.Date;
import java.util.Collections;

import java.util.HashSet;
import java.util.Set;

public class ChatWebsocketHandler extends TextWebSocketHandler {


    @Autowired
    private ChatService chatService;
    /*
    webSocketHandler 인터페이스 : 웹 소켓을 위한 메소드를 지원하는 인터페이스.
    -> WebSocketHandler 인터페이스를 상속받은 클래스(TextWebSocketHandler)를 이용해서 웹소켓 기능을 구현

    * 웹소켓 핸들러 주요 메소드 *

    1. void handlerMessage(WebSocketSession session, WebSocketMessage message)
        -> 클라이언트로부터 메세지가 도착했을시 실행

    2. void afterConnectionEstablished(WebSocketSession session)
        -> 클라이언트롸 연결이 완료되고, 통신할 준비가 되면 실행

    3. void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        -> 클라이언트와 연결이 종료되면 실행

    4. void handlerTransportError(WebSocketSession session , Throwable exception)
        -> 메세지 전송중 에러발생하면 실행
     */


    /**
     * synchronizedSet : 동기화된 Set은 반환해주는 메서드.
     * -> 멀티스레드 환경에서하나의 컬랙션요소에 여러 스레드가 동시에 접근하면 충돌이 발생 할수 있으므로 동기화를(충동이 안나도록)
     * 진행한다.
     */
    private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());


    /**
     * 클라이언트와 연결이 수립되고, 통신준비가 완료되면 수행되는 메서드
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{

        // WebSocketSession : 웹소켓에 접속/요청한 클라이언트의 세션
        System.out.println(session.getId() + "연결됨"); // 세션 아이디 확인
        sessions.add(session);
        // 전달받은 WebSocketSession 을 Set에 추가

    }

    /**
     * 클라이언트와 연결이 종료되면 수행
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{

        sessions.remove(session);
        // 웹 소켓 연결이 종료되는 경우, session 안에 저장되어있던 클라이언트의 session 정보를 삭제
    }

    /**
     * 클라이언트로부터 텍스트 메세지를 전달받았을때 수행
     * TextMessage : 웹 소켓을 이용해 전달된 텍스트가 담겨있는 객체
     * payload : 전송되는 데이터 (JSON 객체)
     * JackSon 라이브러리 : java 에서 json 을 다루기 위한 라이브러니
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{

        System.out.println("전달된 메세지 : " + message.getPayload());

        // Jackson-databind -> ObjectMapper 를 이용해서 JSON 형태로 넘어온 데이터를 특정 VO 필드에 맞게 자동매핑.
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("objectMapper : "+objectMapper);

        // Jsoon 형태의 message 를 특정 ChatMessage Vo 필드에 맞게 매핑 해주는 것.
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        chatMessage.setCreateDate(new Date(System.currentTimeMillis()));

        System.out.println(chatMessage);

        // 전달받은 채팅메세지를 db에 삽입

        int result = chatService.insertMessage(chatMessage); // 삽입된 행의 갯수 chatService.insertMessage(chatMessage);
        if(result>0){
            // 같은 방에 접속중인 클라이언트에게 전달받은 메세지를 보내기
            for(WebSocketSession s : sessions){

                // 반복을 진행중인 webSocketSession 안에 담겨있는 방번호
                int chatRoomNo = (Integer)s.getAttributes().get("chatRoomNo");

                // 메세지에 담겨있는 채팅방 번호와 chatRoomNo 가 같은지 비교
                if(chatMessage.getChatRoomNo() == chatRoomNo){
                    // 같은방 클라이언트에게 JSON 형식으로 메세지를 보냄
                    // s.sendMessage(new TextMessage(message.getPayload()));

                    s.sendMessage(new TextMessage(new Gson().toJson(chatMessage)));
                }
            }
        }
    }
}
