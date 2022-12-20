package com.kh.spring.board.model.vo;

import lombok.Data;

import java.sql.Date;


@Data
public class Reply {
    private int replyNo;
    private String replyContent;
    private int refBno;
    private String replyWriter;
    private Date createDate;
    private String status;
}
