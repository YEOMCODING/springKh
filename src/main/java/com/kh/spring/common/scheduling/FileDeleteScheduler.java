package com.kh.spring.common.scheduling;


import com.kh.spring.board.model.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class FileDeleteScheduler {

    // sysout 쓰지 않기위해서 logger 등록.
    private Logger logger = LoggerFactory.getLogger(FileDeleteScheduler.class);
//    1. Board 테이블 안에있는 이미지목록들 조회하여
//    2. Resources/uploadFiles디렉토리 안에 잇는 이미지들과 대조하여
//    3. 일치하지 않는 이미지파일을 삭제
//       (db에는 없는 데이터인데 uploadFiles안에는 존재하는 경우)
//    5. 5초간격으로 테스트 후 정상이라면, 정시마다 실행되는 크론표현식으로 변경하기.

    @Autowired
    private BoardService boardService;

    @Autowired
    private ServletContext application; // 서버 폴더 경로를 얻어오기 위해서 사용.

    @Scheduled(fixedDelay = 5000 )
    public void deleteFile(){
        logger.info("파일삭제 시작");

        // 1) board 테이블안에 있는 모든 파일목록 조회.
        List<String> dbFile = boardService.findFile();

        // 2) resources/uploadFiles폴더 아래에 존재하는 모든 이미지 파일 목록 조회.
        File path = new File(application.getRealPath("/resources/uploadFiles"));

        File[] files = path.listFiles(); // path가 참조가하고 있는 폴더에 들어가있는 모든 파일을 얻어와서 File배열로 반환해주는 녀석
        List<File> fileList =  Arrays.asList(files);
        System.out.println(fileList.get(0).getName());
        // 3) 두 목록을 비교해서 일치하지 않는 파일 삭제.
        for(int i =0 ; i<fileList.size(); i++ ) {
            if(!(dbFile.contains(fileList.get(i).getName()))){
                fileList.get(i).delete();
                System.out.println("파일 삭제 완료");

            }
        }
    }
}
