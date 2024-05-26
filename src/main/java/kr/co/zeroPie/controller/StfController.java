package kr.co.zeroPie.controller;



import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.zeroPie.dto.StfDTO;
import kr.co.zeroPie.entity.Dpt;
import kr.co.zeroPie.entity.Rnk;
import kr.co.zeroPie.entity.Stf;
import kr.co.zeroPie.security.MyUserDetails;
import kr.co.zeroPie.service.StfService;
import kr.co.zeroPie.util.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StfController {

    private final StfService stfService;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;

    private final RedisTemplate<String, String> redisTemplate;



    @PostMapping("/login")
    public ResponseEntity login(@RequestBody StfDTO userDTO){

        log.info("login...1 : " + userDTO);

        try {
            // Security 인증 처리
            UsernamePasswordAuthenticationToken authToken
                    = new UsernamePasswordAuthenticationToken(userDTO.getStfNo(), userDTO.getStfPass());


            // 사용자 DB 조회
            Authentication authentication = authenticationManager.authenticate(authToken);
            log.info("login...2");

            // 인증된 사용자 가져오기
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            Stf user = userDetails.getStf();

            log.info("login...3 : " + user);


            // 토큰 발급(액세스, 리프레쉬)
            String access  = jwtProvider.createToken(user, 1); // 1일
            String refresh = jwtProvider.createToken(user, 7); // 7일

            // 리프레쉬 토큰 DB 저장

            // 액세스 토큰 클라이언트 전송
            Map<String, Object> map = new HashMap<>();
            map.put("grantType", "Bearer");
            map.put("username", user.getStfNo());
            map.put("accessToken", access);
            map.put("refreshToken", refresh);

            return ResponseEntity.ok().body(map);

        }catch (Exception e){
            log.info("login...3 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
    }


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
    public ResponseEntity<?> sendEmail(@RequestParam("email")String email){

        log.info("일단 들어오니?");

        log.info("email : "+email);

        int count = stfService.findStf(email);//같은 이메일이 몇개인지 체크

        log.info("count={}", count);

        Map<String, String> lists = new HashMap<>();

        if (count  < 1) {//이메일 중복이 없어야됨
            log.info("email={}", email);
            stfService.sendEmailCode(email);

            lists.put("result", "성공");

            return ResponseEntity.ok().body(lists);
        }else{

            lists.put("result","실패");
            return ResponseEntity.ok().body(lists);
        }
    }
    @GetMapping("/verifyCode")
    public ResponseEntity<?> sendVerifyCode(@RequestParam("email") String email, @RequestParam("code")String code){

        log.info("여기까지 잘 들어왔어!");

        log.info("email : "+email);

        log.info("code : "+code);
        Map<String,String> lists = new HashMap<>();

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String savedCode = valueOperations.get("code1");

        log.info("savedCode : "+savedCode);

        if (savedCode != null && savedCode.equals(code)) {
            lists.put("result", "성공");
        } else {
            lists.put("result", "실패");
        }

        return ResponseEntity.ok().body(lists);

    }
}
