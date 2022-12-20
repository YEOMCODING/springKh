package com.kh.spring.member.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * lombok
 * - 자동 코드 생성 라이브러리
 * - 반복되는 getter , setter , toString등 메소드작성코드를 줄여주는 역할의 코드 라이브러리
 * 
 * lombok 설치방법
 * 1. 라이브러리 다운 후 적용(pom.xml)
 * 2. 다운로드된 jar파일 찾아서 설치
 * 3. ide재실행
 * 
 * lombok 사용시 주의사항
 * - uName, bTtitle과 같이 앞글자가 소문자외자인 필드명은 만들면 안됨.
 * - 필드명 작성시 소문자 두글자이상으로 시작해야함.
 * - el표기법 사용시 내부적으로 getter메소드를 찾게되는데 이때 getuName() , getbTitle()이라는 이름으로 메소드 호출하기때문
 *   기존방식이라면 getUName()으로 작성될것이기 때문에 호출될수 없다.
 * 
 */

//@NoArgsConstructor// 기본생성자
//@AllArgsConstructor// 모든필드를 매개변수로 갖는 생성자.
//@Setter // setter
//@Getter // getter
//@ToString // toString
//@EqualsAndHashCode // equals , hashcode 오버라이딩
@Data// 위에 기술한 모든 메소드를 포함하는 어노테이션.
public class Member {

	private int userNo;
	private String userId; // USER_ID
	private String userPwd; // USER_PWD
	private String userName; // USER_NAME..
	private String email;
	private String gender;
	private String age; // AGE NUMBER
	private String phone;
	private String address;
	private Date enrollDate;
	private Date modifyDate;
	private String status;
	private String role;
	
}
