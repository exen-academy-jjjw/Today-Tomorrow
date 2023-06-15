package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.domain.entity.ReviewFile;
import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.ReviewResponseDto;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.FileRepository;
import com.ezen.jjjw.repository.ReviewRepository;
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
    private final BkBoardRepository bkBoardRepository;
    private final FileRepository fileRepository;

    // 리뷰 게시글 작성 POST /review/create/{postId}
    @Transactional
    public ResponseEntity<Integer> createReview(Long postId, ReviewRequestDto reviewRequestDto) {

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        Review findReview = bkBoard.getReview();
        if(findReview != null) {
//            throw new CustomException(ErrorCode.EXIST_REVIEW);
            log.info("리뷰가 존재하는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        Review review = Review.builder()
                .reviewContent(reviewRequestDto.getReviewContent())
                .bkBoard(bkBoard)
                .build();
        reviewRepository.save(review);

//        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
//                .id(review.getId())
//                .postId(review.getBkBoard().getPostId())
//                .reviewContent(review.getReviewContent())
//                .createdAt(review.getCreatedAt())
//                .modifiedAt(review.getModifiedAt())
//                .build();
//
//        return ResponseEntity.ok(reviewResponseDto);
        log.info("리뷰 작성 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional(readOnly = true)
    public BkBoard isPresentPost(Long id) {
        Optional<BkBoard> optionalPost = bkBoardRepository.findById(id);
        return optionalPost.orElse(null);
    }

    // 리뷰 게시글 상세 GET /review/detail/{postId}
    @Transactional(readOnly = true)
    public ResponseEntity<?> findByPostId(Long postId) {

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        Review findReview = bkBoard.getReview();
        if(null == findReview) {
//            throw new CustomException(ErrorCode.NOT_FOUND_REVIEW);
            log.info("존재하지 않는 리뷰");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        List<ReviewFile> reviewFIle = fileRepository.findAllByReviewId(findReview.getId());

        List<String> imageList = new ArrayList<>();
        for(ReviewFile image : reviewFIle){
            imageList.add(image.getFileUrl());
        }

        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                .id(findReview.getId())
                .postId(findReview.getBkBoard().getPostId())
                .fileUrlList(imageList)
                .reviewContent(findReview.getReviewContent())
                .createdAt(findReview.getCreatedAt())
                .modifiedAt(findReview.getModifiedAt())
                .build();

        return ResponseEntity.ok(reviewResponseDto);
    }

    // 리뷰 게시글 수정 PUT /review/update/{postId}
    @Transactional
    public ResponseEntity<Integer> updateSave(Long postId, ReviewRequestDto requestDto) {

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        Review findReview = bkBoard.getReview();
        if(findReview == null) {
//            throw new CustomException(ErrorCode.NOT_FOUND_REVIEW);
            log.info("존재하지 않는 리뷰");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        findReview.update(requestDto);
        reviewRepository.save(findReview.getBkBoard().getReview());

//        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
//                .id(findReview.getId())
//                .postId(findReview.getBkBoard().getPostId())
//                .reviewContent(findReview.getReviewContent())
//                .createdAt(findReview.getCreatedAt())
//                .modifiedAt(findReview.getModifiedAt())
//                .build();
//
//        return ResponseEntity.ok(reviewResponseDto);
        log.info("리뷰 수정 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    // 리뷰 게시글 삭제 DELETE /review/delete/{postId}
    @Transactional
    public ResponseEntity<Integer> deleteByReviewId(Long postId) {

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        Review findReview = bkBoard.getReview();
        if(findReview == null) {
//            throw new CustomException(ErrorCode.NOT_FOUND_REVIEW);
            log.info("존재하지 않는 리뷰");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        reviewRepository.delete(findReview);
//        return ResponseEntity.ok("delete success");
        log.info("리뷰 삭제 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }
}
