package com.kh.spring.board.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.Utils;
import com.kh.spring.common.template.Pagination;
import com.mchange.v2.log.MLevel;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.model.vo.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
	/*
	게시글 삽입 + 이미지 삽입

	@Transactional

	AOP를 이용해서 DAO -> Service의 코드를 수행하는 시점에 예외가 발생한다면 -> rollback

	@Transactional 선언적 트랜잭션 처리방법
			-> RuntimeException (Unchecked Exception) 처리를 기본값으로 가진다.

	checked Exception : 예외처리가 필수 -> I/O 관련

	Unchecked Exception : 예외처리가 필수가 아님 -> NullPointerException ,ArrayOutOfBoundException

	rollbackFor : rollback 을 수행하기 위한 예외의 종류
	 */
	@Transactional(rollbackFor = {Exception.class}) // Exception.class 다형성 이용 , 모든종류 예외에 대해서 발생시 rollback 시킴
	@Override
	public int insertBoard(Board b, List<MultipartFile> list, String webPath, String serverFolderPath) throws Exception {



		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle())); // b라는 객체안에 set~ 넣어줄건데 utils~ 결과값 b.get얻어와서
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));

		// 1) 게시글 삽입
		// 게시글 등록 후 해당 게시글의 pk 값을 반환받음 => boardNo
		int boardNo = boardDao.insertBoard(sqlSession, b); // boardNo 새롭게 등록된 pk 값
		if(boardNo > 0 && list != null) { // 게시글이 제대로 등록
			// 2) 이미지 삽입
			// list -> 실제 파일이 담겨있는 리스트
			// <MultipartFile> 이라는 컬렉션 형태로 가져옴
			List<BoardImg> boardImageList = new ArrayList();

			// boardImageList : db에 등록한 데이터를 모아놓은 컬렉션
			List<String> renameList = new ArrayList();

			// renameList : 변경된 파일명을 저장할 리스트

			// list에서 담겨있는 파일 정보중 실제로 업로드된 파일만 분류하기

			for(int i = 0; i<list.size(); i++) {
				if(list.get(i).getSize() > 0) { // i번째요소에 업로드된 이미지가 존재하는 경우
					// 변경된 파일명 저장
					String changeName = Utils.saveFile(list.get(i), serverFolderPath);
					renameList.add(changeName);

					// BoardImg 객체를 생성해서 값을 추가한 후 boardImageList;
					BoardImg img = new BoardImg();
					img.setRefBno(boardNo); // 게시글 번호
					img.setImgLevel(i); // 이미지 순서
					img.setOriginName(list.get(i).getOriginalFilename()); // 파일의 원본이름
					img.setChangeName(changeName);

					boardImageList.add(img);
				}

			}
			// 분류작업완료 후 boardImageList 가 비어있다 ? --> 등록한 이미지가 없음
			//								비어있지 않다 ? --> 등록한 이미지가 있음

			if( !boardImageList.isEmpty()) {
				int result = boardDao.insertBoardImgList(sqlSession, boardImageList);

				if(result == boardImageList.size()) {
					// 삽입된 행의 개수와 업로드된 이미지 수가 같은경우
					// 서버에 이미지를 저장
					for(int i =0; i<boardImageList.size(); i++) {
						int level = boardImageList.get(i).getImgLevel();

						list.get(level).transferTo(new File(serverFolderPath+renameList.get(i)));
					}
				}else { // 이미지 삽입 실패시
					// 강제로 예외를 발생시키기
					throw new Exception("예외 발생");
				}
			}
		}
		return boardNo;
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public int updateBoard(Board b, List<MultipartFile> imgList, String webPath, String serverFolderPath,String deleteList) throws Exception{
		//1) XSS , 개행 문자 처리
		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		//2) 게시글 업데이트 수정 f
		int result = boardDao.updateBoard(sqlSession, b);
		if(result > 0){
			// 3) 업로드된 이미지만 분류하는 작업 수행
			List<BoardImg> boardImgList = new ArrayList<>();
			List<String> reNameList = new ArrayList<>();

			if(imgList != null) {

				for (int i = 0; i < imgList.size(); i++) {
					if (imgList.get(i).getSize() > 0) { // 업로드된 이미지가 있다면?
						// 변경된 파일명 저장
						String changeName = Utils.saveFile(imgList.get(i), serverFolderPath);
						reNameList.add(changeName);

						// BoardImg 객체를 생성해서 값을 추가한 후 boardImageList;
						BoardImg img = new BoardImg();
						img.setRefBno(b.getBoardNo()); // 게시글 번호
						img.setImgLevel(i); // 이미지 순서
						img.setOriginName(imgList.get(i).getOriginalFilename()); // 파일의 원본이름
						img.setChangeName(changeName);

						boardImgList.add(img);
					}
				}
			}
			// 4) x 버튼 눌렸을때 해당 이미지를 db에서 삭제
			if(deleteList != null&& !deleteList.equals((""))){
				Map<String, Object> map = new HashMap<>();
				map.put("boardNo",b.getBoardNo());
				map.put("deleteList", deleteList);
				result = boardDao.deleteBoardImage(sqlSession,map);
			}
			// 5) db에서 삭제를 성공했다면
			if(result > 0){

				for(BoardImg img: boardImgList){
					result = boardDao.updateBoardImg(sqlSession, img); // 변경된 이름, 원본파일이름, 게시글 번호, 레벨
					// 결과 1 --> 수정작업이 이루어졌다 -> 기존에 이미 이미지가 있었다.
					// 결과 2 --> 수정작업이 잘 안됐다 -> 기존에 이미지가 없었기 때문에.

					// 6) 결과값이 0 -> update 에는 실패했고, 실제로 db에는 올라가야하는 데이터이기 때문에 insert
					if(result == 0){
						result = boardDao.insertBoardImg(sqlSession,img);
						// -> 값을 하나씩 대입해서 삽입하는 경우 결과가 0이 나올수가 없다.
						// 단, 예외는 발생할수 있음.
					}
				}
			}
			// 7) 업로드된 이미지가 있다면 서버에 저장.
			if(!boardImgList.isEmpty() && result != 0){
				for(int i = 0; i< boardImgList.size(); i++){
					int level = boardImgList.get(i).getImgLevel();
					imgList.get(level).transferTo(new File(serverFolderPath+reNameList.get(i)));
				}
			}
		}
		return result;
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

