package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Comment;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.CommentReqDto;
import com.ezen.jjjw.dto.response.CommentResDto;
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
import java.util.ArrayList;
import java.util.List;
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

        Comment parent = null;
        // 자식댓글인 경우
        if (commentReqDto.getParentId() != null) {
            parent = (Comment) member.getComments();
            if (parent == null) {
                log.info("댓글이 존재하지 않습니다.");
                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
            }
            // 부모댓글의 게시글 번호와 자식댓글의 게시글 번호 같은지 체크하기
            if (!parent.getBkBoard().getPostId().equals(commentReqDto.getPostId())) {
                log.info("댓글의 게시글 번호가 일치하지 않습니다.");
                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        Comment comment = Comment.builder()
                .commentTxt(commentReqDto.getCommentTxt())
                .member(member)
                .bkBoard(bkBoard)
                .build();

        if(null != parent){
            comment.updateParent(parent);
        }
        commentRepository.save(comment);

        CommentResDto commentResDto = null;
        if(parent != null){
            commentResDto = CommentResDto.builder()
                    .id(comment.getId())
                    .commentTxt(comment.getCommentTxt())
                    .nickname(comment.getMember().getNickname())
                    .parent(comment.getParent().getId())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build();
        } else {
            commentResDto = CommentResDto.builder()
                    .id(comment.getId())
                    .commentTxt(comment.getCommentTxt())
                    .nickname(comment.getMember().getNickname())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build();
        }

        log.info("댓글 작성 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional(readOnly = true)
    public BkBoard isPresentPost(Long id) {
        Optional<BkBoard> optionalPost = bkBoardRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> detailComment(Long postId) {
        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        customExceptionHandler.getNotFoundCommentStatusOrgetComment(bkBoard);

        List<Comment> findComments = commentRepository.findAllByBkBoard(postId);
        List<CommentResDto> commentResDtoList = new ArrayList<>();

        for(Comment comment: findComments){
            commentResDtoList.add(
                    CommentResDto.builder()
                            .id(comment.getId())
                            .commentTxt(comment.getCommentTxt())
                            .nickname(comment.getMember().getNickname())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }
        return ResponseEntity.ok(commentResDtoList);
    }

    @Transactional
    public ResponseEntity<?> updateComment(Long postId, CommentReqDto commentReqDto) {
        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        customExceptionHandler.getNotFoundCommentStatusOrgetComment(bkBoard);
        Comment findComment = (Comment) bkBoard.getComments();

        findComment.update(commentReqDto);
        commentRepository.save(findComment);

        log.info("댓글 수정 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> deleteComment(Long postId) {
        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        customExceptionHandler.getNotFoundCommentStatusOrgetComment(bkBoard);
        Comment findComment = (Comment) bkBoard.getComments();

        commentRepository.delete(findComment);

        log.info("댓글 삭제 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }
}
