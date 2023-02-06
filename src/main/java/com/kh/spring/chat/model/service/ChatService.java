package com.kh.spring.chat.model.service;


import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;


import java.util.List;


public interface ChatService {
    List<ChatRoom> selectChatRoomList();

    int openChatRoom(ChatRoom chatRoom);

    int insertMessage(ChatMessage cm);

    List<ChatMessage> joinChatRoom(ChatRoomJoin join);

    int exitChatRoom(ChatRoomJoin join);
}
