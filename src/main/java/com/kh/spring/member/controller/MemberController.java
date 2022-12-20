package com.kh.spring.member.controller;

import javax.servlet.http.HttpSession;
import javax.swing.plaf.PanelUI;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

@Controller
// Controller 타입의 어노테이션을 붙여주면 빈 스캐너가 자동으로 빈 등록해줌-> servelt-context.xml안에 있는 <context:component-scan base-package="com.kh.spring" /> 태그 
public class MemberController extends QuartzJobBean {


	public MemberController(){

	}

	// private MemberService ms = new MemberService();

	/*
	 * 기존 객체생성방식. 서비스가 동시에 많은 횟수가 요청되면 그만큼의 객체가 생성된다. 즉 , 객체간의 결합도가 올라감.
	 * 
	 * Spring의 DI(Dependency injection) -> 객체를 스프링에서 생성해서 주입을 해주는 개념
	 * 
	 * new 연산자를 쓰지 않고 선언만 한후 @Autowired 어노테이션을 붙이면 객체를 주입받을수 있음.
	 */
	private MemberService memberService;
	
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	/* @Autowired
	 * 빈 객체 필드주입방식 장점 : 이해하기 편하다, 사용하기하도 편한다.
	 * 
	 * 순환 의존성 문제가 발생할수 있다. (각 2개이상의 클래스가 서로를 무한호출 하는 구조)
	 * final키워드로 지정할수가 없다.
	 * 무분별한 주입시 의존관계확인 어렵다.
	 */
	// 생성자 하나뿐이면 Autowired 생략가능.

	@Autowired
	public MemberController(MemberService memberService , BCryptPasswordEncoder bcryptPasswordEncoder) {
		this.memberService = memberService;
		this.bcryptPasswordEncoder = bcryptPasswordEncoder;
	}
	/* 빈객체 주입 : 권장하는 방식
	 * 생성자 주입방식 : 생성자에 참조할 클래스를 인자로 받아 필드에 매핑시킴.
	 * 필드주입방식과 비교해서 장점
	 *
	 * 1. 현재클래스에서 내가 주입시킬 객체들을 모아서관리할수 있기 때문에 한눈에 알아보기 편함.
	 * 2. 코드분석과 테스트에 유리하며, final로 필드값 받을수 있어서 안전하다.
	 */
	
	/*
	 * 그외 방식
	 * Setter 주입방식 : setter메서드로 빈을 주입받는 방식.
	 * 생성자에 너무많은 의존성을 주입하게되면 알아보기 힘들다라는 단점이 있어 보완하기 위해 사용하거나
	 * 혹은 의존성이 항상 필요한 선택사항인경우 사용함.
	 * 당연히 final키워드로 필드선언가능.
	 */
	@Autowired
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	
	/*
	 * @RequestMapping(value="login.me") // RequestMapping타입의 어노테이션을 붙여서 HandlerMapping등록.
	 * ()안에 속성을 여러개 넣을거라면 속성명=속성값 , 속성명=속성값 형태로 작성 1개만 쓸거면 값만써도 된다.
	 * 
	 * @RequestMapping("login.me")
	 * 
	 * 스프링에서 parameter(요청시 전달 값)을 받는 방법
	 * 1. HttpServeltRequset를 이용해서 전달받기(기존 방식 그대로)
	 * 해당 메소드의 메개변수로 HttpServeltRequest를 작성해놓으면 스프링 컨테이너가 해당메소드를 호출할때 자동으로
	 * request객체를 생생해서 매개변수로 주입해준다.
	 * 
	 */
//	@RequestMapping("login.me")
//	public String loginMember(HttpServletRequest request) {
//		String userId = request.getParameter("userId");
//		String userPwd = request.getParameter("userPwd");
//		
//		System.out.println("userId :"+userId );
//		System.out.println("userPwd : "+userPwd);
//		
//		return "main";
//	}
	/*
	 * 2. @RequestParam어노테이션을 이용하는 방법 request.getParameter("키")로 뽑는역할을 대신수행해주는
	 * 어노테이션. input 속성의 value로 jsp에서 작성했떤 name값을 입력해주면 알아서 매개변수로 담아온다.
	 * 만약 넘어온값이 비어있다면 defaultvalue로 기본값 설정 가능.
	 */
//	@RequestMapping("login.me")
//	public String loginMember(@RequestParam(value="userId", defaultValue="sss") String userId, 
//							  @RequestParam(value="userPwd") String userPwd) {
//		System.out.println("userId :"+userId );
//		System.out.println("userPwd : "+userPwd);
//		return "main";
//	}

	/*
	 * 3. @RequestParam 어노테이션을 생략하는 방법 단 매개변수명을 jsp의 name속성값(요청시 전달한 키값)과 동일하게
	 * 세팅해줘야한다 또한 위에서 사용했던 defaultValue속성 사용 불가
	 */
//	@RequestMapping("login.me")
//	public String loginMember(String userId, String userPwd) {
//		System.out.println(userId);
//		System.out.println(userPwd);
//		return "main";
//	}
	/*
	 * 4. 커맨드 객체 방식 해당 메소드의 매개변수로 요청시 전달값을 담고자하는 VO클래스 타입으로 세팅하고 요청시 전달값의 키값(jsp에서
	 * 넘겨주는 name)을 VO클래스에 담고자하는 필드명으로 작성
	 * 
	 * 스프링 컨테이너가 해다 객체를 기본생성자로 생성 후 내부적으로 setter메서드를 찾아서 요청시 전달한값을 필드에 담아줌
	 * 이때, name값과 담고자하는 vo객체의 필드값이 같아야한다.
	 */
//	@RequestMapping("login.me")
//	public String loginMember(Member m , HttpSession session, Model model) {
//		
//		System.out.println(m.getUserId());
//		//request.setAttribute("errorMsg","오류발생");
//		model.addAttribute("errorMsg","오류 발생!");
//		
//		return "main";
//	}

	/*
	 * 요청 처리 후 "응답데이터를 담고" 응답페이지로 포워딩 또는 url 재용청하는 방법. 포워딩할 응답뷰로 전달하고자 하는 데이터를
	 * 맵형식(key-value) 으로 담을수 있는 영역 -> Model객체. Model requestScope. 단 model안에 데이터 추가시
	 * addAttribute()메서드를 사용.
	 */

	/*
	 * 5. 스프링에서 제공하는 ModelAndView 객체를 이용하는 방법 Model은 데이터를 key-value세트로 담을수 있음.
	 * View는 응답뷰에 대한 정보를 담을수 있다. 이때 리턴타입이 String이아닌 modelAndView 형태로 전달해야함.
	 * 
	 * Model과 View가 결합된 형태의 객체 Model은 단독사용이 가능하지만 View는 불가능하다.
	 */
	@RequestMapping("login.me")
	public ModelAndView loginMember(Member m, HttpSession session, ModelAndView mv) {

		// 암호화 전
		// 로그인 요청처리
//		Member loginUser = memberService.loginMember(m);
//		System.out.println(m);
//		
//		if (loginUser == null) {
//			//실패.
//			mv.addObject("errorMsg", "로그인 실패!");
//			
//			//return common/errorPage
//			mv.setViewName("common/errorPage");
//		} else {
//			session.setAttribute("loginUser", loginUser);
//			mv.setViewName("redirect:/");
//		}
//
//		return mv;
		
		//암호화 후
		/*
		 *  기존에 평문이 디비에 등록되어 있었기 때문에 아이디랑 비밀번호를 같이 입력받아 조회하는 형태로 작업하였음.
		 *  암호화 작업을 하면 입력받은 비밀번호는 평문이지만 디비에 등록되어 있는 비밀번호는 암호문이기때문에 비교시 무조건 다르게된다.
		 *  아이디로 먼저 회원정보 조회 후 회원이 있으면 비밀번호 암호문 비교 메소드를 이용해서 일치하는지 확인.
		 */
		Member loginUser = memberService.loginMember(m);
		// loginUser : 아이디+비밀번호로 조회한 회원정보 => 아이디로만 조회
		// loginUser의 userPwd : 암호화회된 비밀번호
		// m.getUserPwd() : 암호화 안된 비밀번호(request전달받아 m객체안에 매핑됨)
		
		// BCryptPasswordEncoder 객체의 matches 메소드 사용
		// matches(평문 , 암호문)을 작성하면 내부적으로 복호화 작업이 이루어져서
		// boolean자료형을 반납(일치하면 true/ 아니면 fasle)
		
		if(loginUser != null && bcryptPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd())) {// 로그인성공
			session.setAttribute("loginUser", loginUser);
			mv.setViewName("redirect:/");
		}else {//로그인 실패
			mv.addObject("errorMsg", "로그인 실패!");
			mv.setViewName("common/errorPage");
		}
		return mv;
	}

	@RequestMapping("enrollForm.me")
	public String enrollForm() {
		return "member/memberEnrollForm";
	}
	
	//1. memberservice 호출해서 insertMember 메서드 실행 -> 멤버객체가 table에 등록되게.
	
	//2. 멤버테이블에 회원가입등록 성공했따면 alertMsg<-- 회원가입 성공 메세지 보내기(세션)
	//   에러페이이지로 메세지 담아서 보내기 errorMsg<-- 회원가입 실패(리퀘스트)
	
	@RequestMapping("insert.me")
	public String insertMember(Member m , HttpSession session, Model model) {
		
		System.out.println("암호화 전 비밀번호 : "+m.getUserPwd());
		
		//암호화 작업.
		String encPwd = bcryptPasswordEncoder.encode(m.getUserPwd());
		
		//암호화된 PWD를 Member m 에 담아주기
		m.setUserPwd(encPwd);
		
		System.out.println("암호화 후 비밀번호 : "+m.getUserPwd());
		
		//1. memberservice 호출해서 insertMember 메서드 실행 -> 멤버객체가 table에 등록되게.
		int result = memberService.insertMember(m);
		/*
		 * 비밀번호가 사용자가입력한 그대로(평문)이기때문에 보안에 문제가 있다.
		 *     -> Bcrypt 방식의 암호화를 통해서 pwd를 암호문으로 변경.
		 *     1) spring security모듈에서 제공하는 라이브러리를 pom.xml에 등록.
		 *     2) BcryptPasswordEncoder 클래스를 xml파일에 bean등록.
		 *     3) web.xml에서 로딩할수 있게 작성
		 * 
		 * 
		 * 
		 */
		
		
		//2. 멤버테이블에 회원가입등록 성공했따면 alertMsg<-- 회원가입 성공 메세지 보내기(세션)
		//   에러페이이지로 메세지 담아서 보내기 errorMsg<-- 회원가입 실패(리퀘스트)
		if(result > 0) {
			// 성공시- 메인페이지로 보내면서 
			session.setAttribute("alertMsg", "회원가입");
			return "redirect:/";
		}else {
			//실패 - 에러페이지로 메시지 담아서 보내기
			model.addAttribute("errorMsg", "회원가입 실패");
			return "common/errorPage";
		}
		
	}
	
	@RequestMapping("logout.me")
	public String logoutMember(HttpSession session) {
		session.removeAttribute("loginUser");
		return "redirect:/";
	}
	
	@RequestMapping("myPage.me")
	public String myPage() {
		return "member/myPage";
	}
	
	@RequestMapping("update.me")
	public String updateMember(Member m, Model model, HttpSession session) {
		//1. 회원정보 업데이트
		
		int result = memberService.updateMember(m);
		//2. 업데이트 성공시-> 새로운 회원정보를 sessioon에 저장, 성공메세지 추가, mypage로 이동.
		if( result > 0) {
			//업데이트 성공했으니 디비에 등록된 변경된 정보를 가지고 다시 조회해오기.
			Member updateMem = memberService.loginMember(m);
			session.setAttribute("loginUser", updateMem);
			session.setAttribute("alertMsg", "정보수정 성공");
			
			//response.sendRedirect("login.me");
			//return "redirect:/myPage.me"
			return "member/myPage";
		}else {
			//3. 업데이트 실패 -> 에러메세지 추가, 에러페이지 이동			
			model.addAttribute("errorMsg","회원 정보 수정 실패");
			return "common/errorPage";
		}
	}
	
	@RequestMapping("delete.me")
	public String deleteMember(String userPwd , HttpSession session , Model model){
		//입력한 비밀번호가 현재 회원의 비밀번호와 일치할경우 - 탈퇴처리
		String encPwd = ((Member) session.getAttribute("loginUser")).getUserPwd();
		String userId = ((Member) session.getAttribute("loginUser")).getUserId();
		
		if(bcryptPasswordEncoder.matches(userPwd , encPwd)) {
			//회원탈퇴처리
			int result = memberService.deleteMember(userId);
			if(result == 1) {
				//탈퇴 성공 
				session.removeAttribute("loginUser");
				session.setAttribute("alertMsg", "바잉");
				return "redirect:/";
			}else {
				// 탈퇴실패
				model.addAttribute("errorMsg","회원 탈퇴 실패 ㅎㅎ");
				return "common/errorPage";
			}
		}else {// 비밀번호가 틀림.
			session.setAttribute("alertMsg", "비밀번호를 잘못 입력하셨네요");
			return "redirect:/myPage.me";
		}
		
	}
	public int count = 0;

	//@Scheduled(fixedDelay = 1000)
	public void test(){
		System.out.println("1초마다 출력" + count++);
	}

	// crontab 방식
	public void testCron(){
		System.out.println("크론테스트");
	}

	public void testQuartz(){
		System.out.println("콰츠테스트");
	}


	/*
	회원정보 확인 스케줄러
	매일 오전 1시에 모든 사용자의 정보를 검색하여 사용자가 비밀번호를 안바꾼지 3개월이 지났다면, changePwd이값을 Y로 변경
	로그인할때 changePwd값이 Y라면 비밀번호 변경페이지로 이동
	 */
	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException{

		memberService.updateMemberChangePwd();

	}
	
}
