package com.ezen.jjjw.controller;




import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.BkBoardDto;
import com.ezen.jjjw.dto.response.ResponseDto;
import com.ezen.jjjw.service.BkBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class BkBoardController {
    private  final BkBoardService bkBoardService;


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody BkBoardDto.Request bkrequest) {
        return bkBoardService.create(bkrequest);
    }


    // update
    @PutMapping("/update/{postId}")
    public ResponseEntity<ResponseDto<Object>> update(@PathVariable("postId") Long postId, @RequestBody BkBoardDto.Request bkrequest) {
        log.info("postId = {}", postId);
        ResponseDto<Object> response = bkBoardService.update(postId, bkrequest);
        ResponseDto<Object> responseDto = ResponseDto.success(response);
        return ResponseEntity.ok().body(responseDto);
    }


    // list
    @GetMapping("/list")
    public ResponseEntity<List<BkBoard>> getAllBkBoardDto(){
        System.out.println("진입확인");
        return bkBoardService.getAllBkBoardDto();
    }


    @GetMapping("/list/{category}")
    public ResponseEntity<List<BkBoard>> findAllbyMemberIdAndCategory(@PathVariable String category){

        return bkBoardService.findAllbyMemberIdAndCategory(category);
    }

    // delete
    @DeleteMapping("/delete/{postId}")
    public void delete(@PathVariable("postId") Long postId, HttpServletRequest request) {
        bkBoardService.delete(postId, request);
    }

    @GetMapping("/detail/{postId}")
    public ResponseEntity<Object> detail(@PathVariable("postId") Long postId, HttpServletRequest request) {
        BkBoard bkBoard = bkBoardService.getBkBoardById(postId);
        if (bkBoard == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(bkBoard);
    }




}
