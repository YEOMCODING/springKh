package com.kh.spring.member.model.service;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.Member;

@Service
public class MemberService {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	
	public Member loginMember(Member m) {
		
		Member loginUser = memberDao.loginMember(sqlSession, m);
		/*
		 * SqlSessionTemplate 객체를 bean등록후 부터는
		 * 스프링컨테이너가 자원 사용후 자동으로 반납을 해주기 때문에 close()할 필요가 없다.
		 */
		
		return loginUser;
	}
	
	public int insertMember(Member m) {
		return memberDao.insertMember(sqlSession, m);
	}
	
	public int updateMember(Member m) {
		return memberDao.updateMember(sqlSession, m);
	}
	
	public int deleteMember(String userId) {
		return memberDao.deleteMember(sqlSession, userId);
	}


	public void updateMemberChangePwd() { memberDao.updateMemberChangePwd(sqlSession);}
}
