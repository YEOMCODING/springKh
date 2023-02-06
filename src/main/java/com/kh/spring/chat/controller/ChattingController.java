package com.kh.spring.chat.controller;


import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;
import com.kh.spring.member.model.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@SessionAttributes({"loginUser","chatRoomNo"})
// model 에 추가된 값의 key와 SessionAttribute 어노테이션에 작성된 키값이 같으면
// 해당 값을 session scope 에 자동으로 이동시켜줌.
public class ChattingController {

    @Autowired
    private ChatService service;

    /**
     * 채팅방 목록 조회
     */
    @GetMapping("/chat/chatRoomList")
    public String selectChatRoomList(Model model){

        List<ChatRoom> crList = service.selectChatRoomList();
        model.addAttribute("chatRoomList",crList);

        return "chat/chatRoomList";
    }

    /**
     * 채팅방 만들기
     * @ModelAttribute -> 커맨드 객체 @ModelAttribute 는 생략 가능하다..
     */
    @RequestMapping("/chat/openChatRoom")
    public String openChatRoom(
            @ModelAttribute("loginUser") Member loginUser,
            Model model,
            ChatRoom room, // 글 제목이 들어가 있음. request 에서 얻어오는 객체는 @ModelAttribute 생략 가능
            RedirectAttributes ra
            ){

        room.setUserNo(loginUser.getUserNo());

        // 채팅방 만드는 메소드
        int chatRoomNo = service.openChatRoom(room);

        String path = "redirect:/chat/";

        if(chatRoomNo > 0){
            path += "chatRoomList";
            ra.addFlashAttribute("alertMsg", "채팅방 만들기 성공");
        }else{
            path += "chatRoomList";
            ra.addFlashAttribute("alertMsg", "채팅방 만들기 실패");
            /*
                addFlashAttribute 내가 처음 넣은 객체를 sessionScope 저장시켰다가, redirect 가 완료되면 requestScope로 변경해줌.
                -> 일일이 session 에있는 메세지를 삭제해줄 필요가 없음.
             */
        }
        return path;
    }

    /**
     * 채팅방 입장
     */
    @GetMapping("/chat/room/{chatRoomNo}")
    public String joinChatRoom(
                            @ModelAttribute("loginUser") Member loginUser,
                            Model model,
                            @PathVariable("chatRoomNo") int chatRoomNo,
                            ChatRoomJoin join,
                            RedirectAttributes ra
    ){
        join.setUserNo(loginUser.getUserNo());
        List<ChatMessage> list = service.joinChatRoom(join);


        model.addAttribute("list", list);
        model.addAttribute("chatRoomNo",chatRoomNo); // session 스코프에 chatRoomNo 저장됨.
        return "chat/chatRoom";

    }

    @GetMapping("/chat/exit")
    @ResponseBody
    public int exitChatRoom(@ModelAttribute("loginUser") Member loginUser,
                            ChatRoomJoin join
                            ){
        return service.exitChatRoom(join);
    }
}
