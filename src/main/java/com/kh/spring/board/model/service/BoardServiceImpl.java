package com.kh.spring.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.template.Pagination;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.model.vo.PageInfo;

@Service
@RequiredArgsConstructor
// @RequiredArgsConstructor -- 룸북을 통해 생성사 주입을 자동으로 해준다.
public class BoardServiceImpl implements BoardService{
	
	private final BoardDao boardDao;
	
	private final SqlSession sqlSession;

	private final Pagination pagination;

//	public BoardServiceImpl(BoardDao boardDao, SqlSession sqlSession) {
//		this.boardDao = boardDao;
//		this.sqlSession = sqlSession;
//	}

	@Override
	public int selectListCount(String boardCode) {
		return boardDao.selectListCount(sqlSession,boardCode);
	}

	@Override
	public int selectListCount(Map<String ,Object> paramMap){
		return boardDao.selectListCount(sqlSession,paramMap);
	};


	@Override
	public Map<String,Object> selectList(Map<String,Object> paramMap){

		Map<String,Object> map = new HashMap<>();

		int listCount = selectListCount(paramMap);

		int pageLimit = 10;
		int boardLimit = 5;

		PageInfo pi = pagination.getPageInfo(listCount,(Integer)paramMap.get("cpage"),pageLimit,boardLimit);
		paramMap.put("pi", pi);

		map.put("pi", pi);

		ArrayList<Board> list = boardDao.selectList(sqlSession, paramMap);
		map.put("list",list);

		return map;
	}

	@Override
	public Map<String, Object> selectList(int currentPage,String boardCode){

		Map<String,Object> map = new HashMap<>();

		// 1. 페이징 처리 작업.
		int listCount = selectListCount(boardCode);

		int pageLimit = 10;
		int boardLimit = 5;
		PageInfo pi = pagination.getPageInfo(listCount, currentPage, pageLimit, boardLimit);

		map.put("pi", pi);
		ArrayList<Board> list = boardDao.selectList(sqlSession, pi,boardCode);
		map.put("list",list);
		return map;

	}


	@Override
	public int insertBoard(Board b) {
		return boardDao.insertBoard(sqlSession, b);
	}

	@Override
	public int increaseCount(int bno) {
		return boardDao.increaseCount(sqlSession,bno);
	}

	@Override
	public Board selectBoard(int bno) throws Exception{

//		if(true) {
//			throw new Exception("강제예외처리");
//		}


		return boardDao.selectBoard(sqlSession, bno);
	}


	@Override
	public int deleteBoard(int bno){
		return boardDao.deleteBoard(sqlSession, bno);
	}

	@Override
	public ArrayList<Reply> selectReplyList(int bno){
		return boardDao.selectReplyList(sqlSession,bno);
	}

	@Override
	public int insertReply(Reply reply){
		return  boardDao.insertReply(sqlSession,reply);
	};

	@Override
	public List<String> findFile(){
		return boardDao.findFile(sqlSession);
	}

	@Override
	public List<BoardType> selectBoardTypeList() {
		return boardDao.selectBoardTypeList(sqlSession);
	}

	;
}
