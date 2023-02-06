package com.kh.spring.common;

import jdk.dynalink.beans.StaticClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


import javax.swing.plaf.nimbus.State;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);


    // 변경된 이름을 돌려주면서 원본파일을 변경된 파일이름으로 서버에 저장시키는 메서드
    static public String saveFile(MultipartFile upfile,String savePath ) {

        String originName = upfile.getOriginalFilename(); // 원본 파일명 뽑기
        String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // 시간 형식을 문자열로 뽑아오기
        int ranNum = (int) (Math.random() * 90000 + 10000); // 뒤에 붙을 5자리 랜덤값 뽑기
        String ext = originName.substring(originName.lastIndexOf(".")); // 원본파일명으로부터 확장자명 뽑기
        String changeName = currentTime + ranNum + ext; // 다 이어붙이기

        return changeName;
    }

    // 크로스 사이트 스크립트 공격을 방지하기 위한 메소드
    public static String XSSHandling(String content){
        if(content != null){
            content = content.replaceAll("&","%amp;");
            content = content.replaceAll("<","&lt;");
            content = content.replaceAll(">","&gt;");
            content = content.replaceAll("\"","&quot;");
        }
        return content;
    }

    // 개행문자 처리
    public static String newLineHandling(String content){
        return content.replaceAll("(\r\n|\r|\n|\n\r)","<br>");
    }

    public static String newLineClear(String content){
        return content.replaceAll("<br>", "\n");
    }
}


