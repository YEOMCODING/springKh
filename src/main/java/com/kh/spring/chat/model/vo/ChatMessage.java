package com.kh.spring.chat.model.vo;


import lombok.Data;

import java.sql.Date;

@Data
public class ChatMessage {
    private int cmNo;
    private String message;
    private Date createDate;
    private int chatRoomNo;
    private int userNo;
    private  String userName;
}
