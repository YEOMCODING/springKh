package com.kh.spring.board.model.vo;

import java.sql.Date;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardWriter;
	private String boardContent;
	private int count;
	private Date createDate;
	private String status;


	private String originName;
	private String changeName;

	private String boardCd;

	private ArrayList<BoardImg> imgList;
}
