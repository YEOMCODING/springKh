package com.kh.spring.member.model.dao;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.member.model.vo.Member;

@Repository
public class MemberDao {
	
	public Member loginMember(SqlSession sqlSession, Member m) {
		
		return sqlSession.selectOne("memberMapper.loginMember" , m);
	}
	
	public int insertMember(SqlSession sqlSession, Member m ) {
		return sqlSession.insert("memberMapper.insertMember", m);
	}
	
	public int updateMember(SqlSession sqlSession, Member m ) {
		return sqlSession.update("memberMapper.updateMember", m);
	}
	
	public int deleteMember(SqlSession sqlSession, String userId) {
		return sqlSession.update("memberMapper.deleteMember", userId);
	}


    public void updateMemberChangePwd(SqlSessionTemplate sqlSession) {
		sqlSession.update("memberMapper.updateMemberChangePwd");
    }
}



