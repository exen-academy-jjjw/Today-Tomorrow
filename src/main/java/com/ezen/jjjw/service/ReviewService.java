package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.domain.entity.ReviewFile;
import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.ResponseDto;
import com.ezen.jjjw.dto.response.ReviewResponseDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.FileRepository;
import com.ezen.jjjw.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.RSMboard.RSMboard.service
 * fileName       : ReviewService.java
 * author         : won
 * date           : 2023-06-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        won              최초 생성
 */


@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final TokenProvider tokenProvider;
    private final BkBoardRepository bkBoardRepository;
    private final FileRepository fileRepository;

    // 리뷰 게시글 작성 POST /review/create/{postId}
    public ResponseDto<?> createReview(Long postId, ReviewRequestDto reviewRequestDto, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Review findReview = bkBoard.getReview();
        if(findReview != null) {
            return ResponseDto.fail("NOT_FOUND", "이미 리뷰가 존재합니다.");
        }

        Review review = Review.builder()
                .reviewContent(reviewRequestDto.getReviewContent())
                .bkBoard(bkBoard)
                .build();
        reviewRepository.save(review);

        return ResponseDto.success(
                ReviewResponseDto.builder()
                        .id(review.getId())
                        .postId(review.getBkBoard().getPostid())
                        .reviewContent(review.getReviewContent())
                        .createdAt(review.getCreatedAt())
                        .modifiedAt(review.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Authorization"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    @Transactional(readOnly = true)
    public BkBoard isPresentPost(Long id) {
        Optional<BkBoard> optionalPost = bkBoardRepository.findById(id);
        return optionalPost.orElse(null);
    }

    // 리뷰 게시글 상세 GET /review/detail/{postId}
    @Transactional(readOnly = true)
    public ResponseDto<?> findByPostId(Long postId, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Review findReview = bkBoard.getReview();
        if(null == findReview) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 리뷰 id 입니다.");
        }

        List<ReviewFile> reviewFIle = fileRepository.findAllByReviewId(findReview.getId());

        List<String> imageList = new ArrayList<>();
        for(ReviewFile image : reviewFIle){
            imageList.add(image.getFileUrl());
        }

            return ResponseDto.success(
                    ReviewResponseDto.builder()
                            .id(findReview.getId())
                            .postId(findReview.getBkBoard().getPostid())
                            .fileUrlList(imageList)
                            .reviewContent(findReview.getReviewContent())
                            .createdAt(findReview.getCreatedAt())
                            .modifiedAt(findReview.getModifiedAt())
                            .build()
            );
    }

    // 리뷰 게시글 수정 PUT /review/update/{postId}
    public ResponseDto<?> updateSave(Long postId, ReviewRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Review findReview = bkBoard.getReview();
        if(findReview == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 리뷰 id 입니다.");
        }

        if (findReview.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        findReview.update(requestDto);
        reviewRepository.save(findReview.getBkBoard().getReview());

        return ResponseDto.success(
                ReviewResponseDto.builder()
                        .id(findReview.getId())
                        .postId(findReview.getBkBoard().getPostid())
                        .reviewContent(findReview.getReviewContent())
                        .createdAt(findReview.getCreatedAt())
                        .modifiedAt(findReview.getModifiedAt())
                        .build()
        );
    }

    // 리뷰 게시글 삭제 DELETE /review/delete/{postId}
    public ResponseDto<?> deleteByReviewId(Long postId, ReviewRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Review findReview = bkBoard.getReview();
        if(findReview == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 리뷰 id 입니다.");
        }

        if (findReview.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        reviewRepository.delete(findReview);
        return ResponseDto.success("delete success");
    }
}
