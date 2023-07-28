package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Comment;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.CommentReqDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.CommentRepository;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * packageName    : com.ezen.jjjw.service
 * fileName       : CommentService.java
 * author         : won
 * date           : 2023-07-28
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-28        won       최초 생성
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final BkBoardRepository bkBoardRepository;
    private final CommentRepository commentRepository;

    private final CustomExceptionHandler customExceptionHandler;

    @Transactional
    public ResponseEntity<Integer> createComment(Long postId, CommentReqDto commentReqDto, Member member) {
        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        Comment comment = Comment.builder()
                .commentTxt(commentReqDto.getCommentTxt())
                .member(member)
                .bkBoard(bkBoard)
                .build();
        commentRepository.save(comment);
        log.info("댓글 작성 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);

    }

    @Transactional(readOnly = true)
    public BkBoard isPresentPost(Long id) {
        Optional<BkBoard> optionalPost = bkBoardRepository.findById(id);
        return optionalPost.orElse(null);
    }

}
