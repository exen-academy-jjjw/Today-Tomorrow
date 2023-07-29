package com.ezen.jjjw.controller;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.BkBoardDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.service.BkBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class BkBoardController {

    private  final BkBoardService bkBoardService;
    private final TokenProvider tokenProvider;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody BkBoardDto.Request bkrequest) {
        Member member = tokenProvider.getMemberFromAuthentication();
        return bkBoardService.create(bkrequest, member);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<Integer> update(@PathVariable("postId") Long postId, @RequestBody BkBoardDto.UpdateRequest bkrequest) {
        Member member = tokenProvider.getMemberFromAuthentication();
        return bkBoardService.update(postId, bkrequest, member);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Integer> delete(@PathVariable("postId") Long postId) {
        return bkBoardService.delete(postId);
    }

    @GetMapping("/detail/{postId}")
    public ResponseEntity<?> detail(@PathVariable("postId") Long postId) {
        return bkBoardService.getBkBoardById(postId);
    }

    @GetMapping("/list")
    public ResponseEntity<List<BkBoard>> getAllBkBoardDto(@RequestParam("page") int page){
        Member member = tokenProvider.getMemberFromAuthentication();
        return bkBoardService.getAllBkBoardDto(page, member);
    }

    @GetMapping("/share")
    public ResponseEntity<List<BkBoard>> getAllShareBkBoardDto(@RequestParam("page") int page){
        tokenProvider.getMemberFromAuthentication();
        return bkBoardService.getAllShareBkBoardDto(page);
    }

    @GetMapping("/list/{category}")
    public ResponseEntity<List<BkBoard>> findAllByMemberIdAndCategory(@PathVariable String category, @RequestParam("page") int page){
        Member member = tokenProvider.getMemberFromAuthentication();
        return bkBoardService.findAllByMemberIdAndCategory(category, page, member);
    }

    @PutMapping("/completion/{postId}")
    public ResponseEntity<Integer> updateCompletion(@PathVariable("postId") Long postId, @RequestBody BkBoardDto.RequestCompletion requestCompletion) {
        Member member = tokenProvider.getMemberFromAuthentication();
        return bkBoardService.updateCompletion(postId, requestCompletion, member);
    }

    @PutMapping("/share/{postId}")
    public ResponseEntity<Integer> updateShare(@PathVariable("postId") Long postId, @RequestBody BkBoardDto.RequestShare requestShare) {
        Member member = tokenProvider.getMemberFromAuthentication();
        return bkBoardService.updateShare(postId, requestShare, member);
    }
}
