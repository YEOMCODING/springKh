package com.kh.spring.board.contoller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.google.gson.Gson;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.member.model.vo.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.sound.midi.Soundbank;

@Controller
@RequestMapping("/board")
// 클래스에서도 리퀘스트매핑 추가가능.
// 그럼 현재 컨트롤러는 /spring/board 의 경로로 들어오는 모든 url요청을 받아준다.
public class BoardController {

	private BoardService boardService;

	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	public BoardController(BoardService boardService) {
		this.boardService =  boardService;
	}
	
	@RequestMapping("/list/{boardCode}")

	// /spring/board/list/C

	// /spring/board/list/T
	// @PathVariable("value") : URL 경로에 포함되어 있는 값을 변수로 사용할 수 있게 해줌. --> 자동으로 requestScope 에 저장됨
	// -> jsp 에서도 el태그로 사용가능.
	public String selectList(
			@PathVariable("boardCode") String boardCode,
			@RequestParam(value="cpage", defaultValue="1") int currentPage,
			Model m,
			@RequestParam Map<String,Object> paramMap
			// 검색 요청이 들어왔다면 paramMap안에는 key, condition
	) {

		Map<String,Object> map = new HashMap<>();

		if(paramMap.get("condition") == null) { // 검색 요청을 하지 않은 경우
			map = boardService.selectList(currentPage,boardCode);
		}else{ // 검색 요청을 한 경우
			paramMap.put("cpage", currentPage);
			paramMap.put("boardCode", boardCode);

			map = boardService.selectList(paramMap);
		}

		m.addAttribute("map", map);

		
		return "board/boardListView";

	}

	@RequestMapping("enrollForm.bo")
	public String boardEnrollForm(){
		return "board/boardEnrollForm";
	}

	@RequestMapping("insert.bo")
	public String insertBoard(Board b, MultipartFile upfile, HttpSession session, Model model){

		// 첨부파일을 선택하고 안하고 상관없이 객체는 생성됨(upfile)
		// 전달된 파일이 있는지 없는지는 filename으로 첨부파일 유무를 확인

		// 전달된 파일이 있을경우 -> 파일명 수정작업 후 서버 업로드 -> 원본 파일명과 수정된 파일명, 서버에 업로드된 경로를 b객체 안에 담기
		if(!upfile.getOriginalFilename().equals("")) { //원본파일네임이 넘어왔는지 빈칸인지 검사

			String changeName = saveFile(upfile, session);

			b.setOriginName(upfile.getOriginalFilename());
			b.setChangeName("resources/uploadFiles/" + changeName);
		}

		int result = boardService.insertBoard(b);

		if(result > 0) {
			session.setAttribute("alertMsg", "게시글 작성 성공");
			return "redirect:list";
		} else {
			model.addAttribute("errorMsg", "게시글 작성 실패");
			return "common/errorPage";
		}
	}

	// 변경된 이름을 돌려주면서 원본파일을 변경된 파일이름으로 서버에 저장시키는 메서드
	public String saveFile(MultipartFile upfile, HttpSession session) {

		String originName = upfile.getOriginalFilename(); // 원본 파일명 뽑기
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // 시간 형식을 문자열로 뽑아오기
		int ranNum = (int)(Math.random() * 90000 + 10000); // 뒤에 붙을 5자리 랜덤값 뽑기
		String ext = originName.substring(originName.lastIndexOf(".")); // 원본파일명으로부터 확장자명 뽑기
		String changeName = currentTime + ranNum + ext; // 다 이어붙이기
		String savePath = session.getServletContext().getRealPath("/resources/uploadFiles/"); // 업로드 하고자하는 물리적인 위치 알아내기

		try {
			upfile.transferTo(new File(savePath + changeName)); // 경로와 수정파일명을 합쳐서 업로드하기
		} catch (IllegalStateException | IOException e) {
			logger.error("에러남");
			//e.printStackTrace();
		}
		return changeName;
	}

	@RequestMapping("detail.bo")
	public ModelAndView selectBoard(int bno, ModelAndView mv) {
		// 1. 조회수 증가
		int result = boardService.increaseCount(bno);


		if(result>0) { // 성공적으로 조회수가 증가되었다.
			// 상세보기할 정보를 조회
			Board b = null;
			try {
				b = boardService.selectBoard(bno);
			} catch (Exception e) {
				logger.error("에러발생");
			}
			mv.addObject("b",b);
			mv.setViewName("board/boardDetailView");
		}else {
			mv.addObject("errorMsg", "게시글 조회 실패");
			mv.setViewName("common/errorPage");
		}
		return mv;
	}



	@RequestMapping("/deleteBoard.bo")
	public String deleteBoard(@RequestParam("bno") int bno) {
		boardService.deleteBoard(bno);
		return "redirect:list.bo";
	}

	@RequestMapping("reply.bo")
	@ResponseBody
	// responseBody : 별도의 뷰페이지가 아니라 리턴값을 직접 지정해야 하는 경우
	public String selectReplyList(int bno){
		// 댓글 목록 조회
		System.out.println(bno);
		ArrayList<Reply> list = boardService.selectReplyList(bno);

		String result = new Gson().toJson(list);

		return result;
	}

	@RequestMapping("rinsert.bo")
	@ResponseBody
	public int insertReply(Reply reply , HttpSession session){

		String userNo = Integer.toString(((Member)session.getAttribute("loginUser")).getUserNo());
		reply.setReplyWriter(userNo);
		int result = boardService.insertReply(reply);

		return  result;
	}





























	
}
