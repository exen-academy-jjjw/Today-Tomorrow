package com.ezen.jjjw.controller;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.dto.BkBoardDto;
import com.ezen.jjjw.service.BkBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class BkBoardController {
    private  final BkBoardService bkBoardService;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody BkBoardDto.Request bkrequest) {
        return bkBoardService.create(bkrequest);
    }

    // update
    @PutMapping("/update/{postId}")
    public ResponseEntity<Integer> update(@PathVariable("postId") Long postId, @RequestBody BkBoardDto.UpdateRequest bkrequest) {
        return bkBoardService.update(postId, bkrequest);
    }

    // list
    @GetMapping("/list")
    public ResponseEntity<List<BkBoard>> getAllBkBoardDto(){
        return bkBoardService.getAllBkBoardDto();
    }

    @GetMapping("/list/{category}")
    public ResponseEntity<List<BkBoard>> findAllByMemberIdAndCategory(@PathVariable String category){
        return bkBoardService.findAllByMemberIdAndCategory(category);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Integer> delete(@PathVariable("postId") Long postId) {
        return bkBoardService.delete(postId);
    }

    @GetMapping("/detail/{postId}")
    public ResponseEntity<?> detail(@PathVariable("postId") Long postId, Integer existReview) {
        return bkBoardService.getBkBoardById(postId);
    }

    /*compleetion 관련 코드 추가*/
    @PutMapping("/completion/{postId}")
    public ResponseEntity<Integer> updateCompletion(@PathVariable("postId") Long postId, @RequestBody BkBoardDto.RequestCompletion requestCompletion) {
        return bkBoardService.updateCompletion(postId, requestCompletion);
    }
}
