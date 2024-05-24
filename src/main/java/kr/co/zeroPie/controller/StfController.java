package kr.co.zeroPie.controller;



import jakarta.servlet.http.HttpSession;
import kr.co.zeroPie.dto.StfDTO;
import kr.co.zeroPie.entity.Dpt;
import kr.co.zeroPie.entity.Rnk;
import kr.co.zeroPie.entity.Stf;
import kr.co.zeroPie.service.StfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StfController {

    private final StfService stfService;

    //회원가입 기능
    @PostMapping("/upload")
    public ResponseEntity<?> register(StfDTO stfDTO){//파일업로드하는 동시에 아이디를 만들어서 no에 저장도 해야함!
        log.info("파일이 들어왔네?");

        log.info("stfDTO 출력 : "+stfDTO);

        log.info("사진 잘 들어옴? : "+stfDTO.getThumbFile());

        stfService.register(stfDTO);

        return ResponseEntity.ok("성공!");
    }


    //부서 목록 찾기
    @GetMapping("/findPosition")
    public ResponseEntity<?> findPosition(){

        List<Dpt> findPositions = stfService.findPosition();

        log.info("findPosition : "+findPositions);
        Map<String, List<Dpt>> lists = new HashMap<>();
        lists.put("result", findPositions);

        return ResponseEntity.ok().body(lists);
    }

    //직급 목록 찾기
    @GetMapping("/findRnk")
    public ResponseEntity<?> findRnk(){

        List<Rnk> findRnks = stfService.findRnk();

        log.info("findRnk : "+findRnks);

        Map<String, List<Rnk>> lists = new HashMap<>();
        lists.put("result", findRnks);

        return ResponseEntity.ok().body(lists);
    }

    //이메일 인증 보내기
    @GetMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(HttpSession session, @RequestParam("email")String email){

        log.info("일단 들어오니?");

        log.info("email : "+email);

        int count = stfService.findStf(email);//같은 이메일이 몇개인지 체크

        log.info("count={}", count);

        Map<String, String> lists = new HashMap<>();

        if (count  < 1) {//이메일 중복이 없어야됨
            log.info("email={}", email);
            stfService.sendEmailCode(session, email);

            lists.put("result", "성공");

            return ResponseEntity.ok().body(lists);
        }else{

            lists.put("result","실패");
            return ResponseEntity.ok().body(lists);
        }
    }
    @GetMapping("/verifyCode")
    public ResponseEntity<?> sendVerifyCode(HttpSession session,@RequestParam("email") String email, @RequestParam("code")String code){

        log.info("여기까지 잘 들어왔어!");

        log.info("email : "+email);

        log.info("code : "+code);

        String sessionCode = (String) session.getAttribute("code1");

        log.info("시스템에서 보낸 code : " + sessionCode);

        log.info("내가 입력한 code : " + code);

        Map<String, String> lists = new HashMap<>();

        if (sessionCode.equals(code)) {
            //Json 생성
            log.info("여기는 잘 입력한 쪽");
            lists.put("result", "통과");
        } else {
            //Json 생성
            log.info("여기는 잘 못 입력한 쪽");
            lists.put("result", "실패");
        }

        return ResponseEntity.ok().body(lists);

    }
}
