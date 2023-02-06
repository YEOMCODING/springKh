package com.kh.spring.chat.model.service;

import com.kh.spring.chat.model.dao.ChatDao;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;
import com.kh.spring.common.Utils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private SqlSession sqlSession;


    @Override
    public List<ChatRoom> selectChatRoomList() {
        return chatDao.selectChatRoomList(sqlSession);
    }

    @Override
    public int openChatRoom(ChatRoom chatRoom) {
        return chatDao.openChatRoom(sqlSession,chatRoom);
    }

    @Override
    public int insertMessage(ChatMessage cm) {
        // 데이터를 db에 넣어줄때 개행문자 처리
        cm.setMessage(Utils.newLineHandling(cm.getMessage()));
        cm.setMessage(Utils.XSSHandling(cm.getMessage()));
        return chatDao.insertMessage(sqlSession,cm);
    }

    @Override
    public List<ChatMessage> joinChatRoom(ChatRoomJoin join) {
        int result = chatDao.joinCheck(sqlSession,join);

        if(result == 0){
            chatDao.joinChatRoom(sqlSession, join);
        }
        return chatDao.selectChatMessage(sqlSession, join.getChatRoomNo());
    }

    @Override
    public int exitChatRoom(ChatRoomJoin join) {
        // 채팅방 나가기
        int result = chatDao.exitChatRoom(sqlSession, join);
        if(result >0){ // 채팅방 나가기 성공시
            // 현재방에 몇명이 남아있는지 확인, 내가 마지막 인원인경우 채팅방을 닫기.
            int cnt = chatDao.countChatRoomMember(sqlSession, join.getChatRoomNo());
            // 0명일 경우
            if(cnt == 0){
                result = chatDao.closeChatRoom(sqlSession, join.getChatRoomNo());
            }
        }
        return result;
    }
}
