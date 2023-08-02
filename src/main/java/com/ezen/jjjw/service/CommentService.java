package com.ezen.jjjw.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Comment;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.CommentReqDto;
import com.ezen.jjjw.dto.response.CommentResDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.CommentRepository;
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

        Comment comment = Comment.builder()
                .commentTxt(commentReqDto.getCommentTxt())
                .member(member)
                .bkBoard(bkBoard)
                .build();
        bkBoard.updateExistComment(bkBoard);
        bkBoardRepository.save(bkBoard);
        commentRepository.save(comment);

        log.info("댓글 작성 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> createReply(Long commentId, CommentReqDto commentReqDto, Member member) {
        Comment parent = isPresentParent(commentId);
        customExceptionHandler.getNotFoundCommentStatusOrgetComment(parent.getBkBoard());

        if (parent.getParent() != null) {
            log.info("댓글을 작성할 수 없습니다.");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        Comment reply = Comment.builder()
                .commentTxt(commentReqDto.getCommentTxt())
                .member(member)
                .bkBoard(parent.getBkBoard())
                .parent(parent)
                .build();
        commentRepository.save(reply);

        log.info("대댓글 작성 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional(readOnly = true)
    public BkBoard isPresentPost(Long id) {
        Optional<BkBoard> optionalPost = bkBoardRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional(readOnly = true)
    public Comment isPresentParent(Long commentId) {
        Optional<Comment> parent = commentRepository.findById(commentId);
        return parent.orElse(null);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> detailComment(Long postId) {
        BkBoard bkBoard = isPresentPost(postId);
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);
        customExceptionHandler.getNotFoundCommentStatusOrgetComment(bkBoard);

        List<Comment> findComments = commentRepository.findAllByBkBoardPostId(postId);
        List<CommentResDto> commentResDtoList = new ArrayList<>();

        for(Comment comment: findComments){
            if (comment.getParent() == null) {
                CommentResDto commentDto = buildCommentTree(comment, findComments);
                commentResDtoList.add(commentDto);
            }
        }
        return ResponseEntity.ok(commentResDtoList);
    }

    @Transactional(readOnly = true)
    public CommentResDto buildCommentTree(Comment comment, List<Comment> comments) {
        CommentResDto commentDto = CommentResDto.builder()
                .id(comment.getId())
                .commentTxt(comment.getCommentTxt())
                .nickname(comment.getMember().getNickname())
                .postId(comment.getBkBoard().getPostId())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();

        List<CommentResDto> children = new ArrayList<>();
        for (Comment child : comments) {
            if (child.getParent() != null && child.getParent().getId().equals(comment.getId())) {
                CommentResDto childDto = buildCommentTree(child, comments);
                children.add(childDto);
            }
        }
        commentDto.setChildren(children);
        return commentDto;
    }


    @Transactional
    public ResponseEntity<?> updateComment(Long commentId, CommentReqDto commentReqDto) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));

        findComment.update(commentReqDto);
        commentRepository.save(findComment);

        log.info("댓글 수정 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> deleteComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));

        BkBoard bkBoard = findComment.getBkBoard();
        if(findComment.getParent() == null){
            bkBoard.deleteExistComment(bkBoard);
            bkBoardRepository.save(bkBoard);
        }
        commentRepository.delete(findComment);

        log.info("댓글 삭제 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }
}
