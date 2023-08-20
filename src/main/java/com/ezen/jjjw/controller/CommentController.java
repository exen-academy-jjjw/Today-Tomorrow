package com.ezen.jjjw.controller;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.CommentReqDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.service.CommentService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.ezen.jjjw.controller
 * fileName       : CommentController.java
 * author         : won
 * date           : 2023-07-28
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-28        won       최초 생성
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/comment")
public class CommentController {
    private final CommentService commentService;
    private final TokenProvider tokenProvider;

    // 댓글 작성 POST /comment/create/{postId}
    @PostMapping(value = "/create/{postId}")
    public ResponseEntity<Integer> createComment(@PathVariable Long postId, @RequestBody CommentReqDto commentReqDto){
        Member member = tokenProvider.getMemberFromAuthentication();
        return commentService.createComment(postId, commentReqDto, member);
    }

    @PostMapping(value = "/reply/{commentId}")
    public ResponseEntity<Integer> createReply(@PathVariable Long commentId, @RequestBody CommentReqDto commentReqDto){
        Member member = tokenProvider.getMemberFromAuthentication();
        return commentService.createReply(commentId, commentReqDto, member);
    }

    @GetMapping(value = "/detail/{postId}")
    public ResponseEntity<?> detailComment(@PathVariable Long postId){
        return commentService.detailComment(postId);
    }

    @GetMapping(value = "/count/{postId}")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long postId) {
        return commentService.getCommentCount(postId);
    }

    @PutMapping(value = "/update/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentReqDto commentReqDto){
        return commentService.updateComment(commentId, commentReqDto);
    }

    @DeleteMapping(value = "/delete/{commentId}")
    public ResponseEntity<Integer> deleteComment(@PathVariable Long commentId){
        return commentService.deleteComment(commentId);
    }

}
