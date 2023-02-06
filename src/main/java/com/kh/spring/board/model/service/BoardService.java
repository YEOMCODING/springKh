package com.kh.spring.board.model.service;

import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.model.vo.PageInfo;
import org.springframework.web.multipart.MultipartFile;

public interface BoardService {

	public int selectListCount(String boardCode);

	public int selectListCount(Map<String ,Object> paramMap);

	public Map<String, Object> selectList(int currentPage , String boardCode);
	
	public Map<String, Object> selectList(Map<String,Object> paramMap);

	int insertBoard(Board b, List<MultipartFile> imgList , String webPath , String serverFolderPath) throws Exception;

	public int increaseCount(int bno);

	public Board selectBoard(int bno) throws Exception;

    ArrayList<Reply> selectReplyList(int bno);

	int insertReply(Reply reply);


 	List<String> findFile();

    public List<BoardType> selectBoardTypeList();

	public int updateBoard(Board b, List<MultipartFile> imgList, String webPath, String serverFolderPath,String deleteList) throws Exception;
}
