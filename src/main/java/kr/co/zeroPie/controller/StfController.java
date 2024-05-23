package kr.co.zeroPie.controller;



import kr.co.zeroPie.dto.StfDTO;
import kr.co.zeroPie.service.StfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StfController {

    //private final StfService stfService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam StfDTO stfDTO){

        //log.info(stfDTO.toString());

        log.info("파일이 들어왔네?");

        log.info("사진 잘 들어옴? : "+stfDTO.getProfile());

        return ResponseEntity.ok("성공!");
    }

    @PostMapping("/uploadImg")
    public ResponseEntity<?> register(@RequestParam(name="file") MultipartFile file){
        log.info("파일이 들어왔네?");

        log.info("사진 잘 들어옴? : "+file);

        return ResponseEntity.ok("성공!");
    }
}
