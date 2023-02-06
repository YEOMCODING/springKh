package com.kh.spring.board.contoller;

import java.io.File;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.Utils;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	// 리팩토링 : 게시글 등록수정 부분과 등록기능의 url을 합치기 리퀘스트 맵핑: 별도의 url 요청방법 중 메서드
	// post, get으로 받아주고 있는데 요청방법을 다르게 해서 같은 url 로 전송 방법만 메서드로 method="get" 같은 형태로 돌려받을 수 있다.
	// 속성 여러개 쓰지 말라고 어노테이션이 나눠져 있는데
	@RequestMapping("enrollForm/{boardCode}")
	public String boardEnrollForm(@PathVariable("boardCode") String boardCode,
								  Model model,
								  @RequestParam(value = "mode",required = false,defaultValue = "insert") String mode, // mode 변한게 있는지
								  @RequestParam(value = "bno",required = false,defaultValue = "0") int bno // RequestParam null이 들어와도 exeption처리 안해줌
	) {
		if(mode.equals("update")){
			// 수정페이지요청.
			try {
				Board b = boardService.selectBoard(bno);
				// 개행 문자가 <br>태그로 치환되어 있는 상태 -->
				// textarea에 출력할 것이므로 \n으로 변경해줌.

				b.setBoardContent(Utils.newLineClear(b.getBoardContent()));
				model.addAttribute("b", b);

			} catch (Exception e) {
				logger.error("selectBoard 메서드 에러");
			}
		}

		if(boardCode.equals("C")) {
			return "board/boardEnrollForm";
		}else {
			return "board/boardPictureEnrollForm";
		}
	}

	@RequestMapping("insert/{boardCode}")
	public String insertBoard(Board b,
							  @RequestParam(value = "images", required = false) List<MultipartFile> imgList, // requered=false -> 일반게시판은 없어도 상관없음 업로드용 이미지
							  MultipartFile upfile, // 첨부파일
							  @PathVariable("boardCode") String boardCode,
							  HttpSession session,
							  Model model,
							  @RequestParam(value = "mode",required = false,defaultValue = "insert") String mode,
							  @RequestParam(value="deleteList" , required = false) String deleteList
	) {

		System.out.println("imgList의값 : "+imgList.get(0));

		// 사진게시판 이미지들 저장경로 얻어오기
				String webPath = "/resources/images/boardT/"; // 끝/ : 안쪽
		String serverFolderPath = session.getServletContext().getRealPath(webPath);
		b.setBoardCd(boardCode);
		int result = 0;
		System.out.println("serverFolderPath : " + serverFolderPath);
		// 첨부파일을 선택하고 안하고 상관없이 객체는 생성됨(upfile)
		// 전달된 파일이 있는지 없는지는 filename으로 첨부파일 유무를 확인

		// 전달된 파일이 있을경우 -> 파일명 수정작업 후 서버 업로드 -> 원본 파일명과 수정된 파일명, 서버에 업로드된 경로를 b객체 안에 담기
		if (!upfile.getOriginalFilename().equals("")) { //원본파일네임이 넘어왔는지 빈칸인지 검사
			String savePath = session.getServletContext().getRealPath("/resources/uploadFiles/"); // 업로드 하고자하는 물리적인 위치 알아내기
			String changeName = Utils.saveFile(upfile, savePath);

			try {
				upfile.transferTo(new File(savePath + changeName)); // 경로와 수정파일명을 합쳐서 업로드하기
			} catch (IllegalStateException | IOException e) {
				logger.error(e.getMessage());
			}
			b.setOriginName(upfile.getOriginalFilename());
			b.setChangeName("resources/uploadFiles/" + changeName);
		}
		if(mode.equals("insert")){
			try {
				result = boardService.insertBoard(b , imgList, webPath, serverFolderPath);
			} catch (Exception e) {
				logger.error("에러발생");
			}
		}else{ // 수정

			// 게시글 수정 서비스 호출
			try {
				result = boardService.updateBoard(b,imgList,webPath,serverFolderPath,deleteList);
			} catch (Exception e) {
				logger.error("에러발생");
			}
		}

		if (result > 0) {
			session.setAttribute("alertMsg", "게시글 작성 성공");
			return "redirect:../list/"+boardCode;
		} else {
			model.addAttribute("errorMsg", "게시글 작성 실패");
			return "common/errorPage";
		}
	}


	@RequestMapping("/detail/{boardCode}/{boardNo}")
	public ModelAndView selectBoard(
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			HttpSession session,
			ModelAndView mv,
			HttpServletRequest req,
			HttpServletResponse res
			) throws Exception{

		Board b = boardService.selectBoard(boardNo);
		// 쿠키를 이용해서 조회수 중복으로 증가되지 않도록 방지 + 본인의 글은 애초에 조회수가 증가되지 않도록.
		if (b != null) { // 상세 조회 성공
			Member loginUser = (Member) session.getAttribute("loginUser");

			int userNo = 0;
			if (loginUser != null) {
				userNo = loginUser.getUserNo();
			}

			// 작성자의 번호와 현재세션의 유저번호가 같지 않을때만 조회수 증가
			if (!b.getBoardWriter().equals(userNo + "")) {

				Cookie cookie = null; // 기존에 존재하던 쿠키를 저장하는 변수

				Cookie[] cArr = req.getCookies(); //
				if (cArr != null && cArr.length > 0) {
					for (Cookie c : cArr) {
						if (c.getName().equals("readBoardNo")) {
							cookie = c;
							break;
						}
					}
				}
				int result = 0;
				if (cookie == null) { // 원래 readBoardNo라는 이름의 쿠키가 없다.
					// 쿠키 생성
					cookie = new Cookie("readBoardNo", boardNo + "");
					// 조회수 증가 서비스 호출
					result = boardService.increaseCount(boardNo);

				} else {
					// 기존에 readboardNo 라는 이름의 쿠키가 있다. -> 쿠키에 저장된 값 뒤쪽에 현재 조회된 게시글 번호를 추가
					//										  단, 기존 쿠키값에 중복되는 번호가 없는경우에만 추가.
					String[] arr = cookie.getValue().split("/");
					// "readBoardNo" : 1/2/5/14/103/115 내가 읽은 게시판 boardNo 가 기록되어 있음.

					List<String> list = Arrays.asList(arr); // 컬렉션으로 변환

					//"100".indexOf("100") -> 0
					//"100".indexOf("1) -> 0
					// List.indexOf(Object) :
					// - list 안에서 매개변수로들어온 Object와 일치하는(equals) 부분의 인덱스 반환
					// - 일치하는 부분이 없으면 -1 없으면

					if (list.indexOf(boardNo + "") == -1) { // 기존값에 같은 글번호가 없다.
						cookie.setValue(cookie.getValue() + "/" + boardNo);
						result = boardService.increaseCount(boardNo);
					}
				}
				// 1. 조회수 증가
				if (result > 0) { // 성공적으로 조회수가 증가되었다.
					b.setCount(b.getCount() + 1);
					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(60 * 60 * 1); // 1시간
					res.addCookie(cookie);
				}
			}
			mv.addObject("b", b);
			mv.setViewName("board/boardDetailView");

		}else {
			mv.addObject("errorMsg", "게시글 조회 실패");
			mv.setViewName("common/errorPage");
		}
			return mv;
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
