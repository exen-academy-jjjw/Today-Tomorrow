package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.ReviewResponseDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * packageName    : com.RSMboard.RSMboard.service
 * fileName       : ReviewServiceTest
 * author         : won
 * date           : 2024-04-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-02        won              최초 생성
 */

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BkBoardRepository bkBoardRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private CustomExceptionHandler customExceptionHandler;

    @Test
    void createReview() throws IOException {
        // given
        Long postId = 1L;
        BkBoard mockBoard = Mockito.mock(BkBoard.class);;

        String reviewTxt = "리뷰1";
        List<MultipartFile> mockMultipartFiles = Collections.emptyList();
        List<String> mockImageUrls = new ArrayList<>();

        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(mockBoard));
        when(reviewRepository.save(any())).thenReturn(new Review());
        when(imageService.uploadImages(anyList(), anyString(), any())).thenReturn(mockImageUrls);

        // when
        ResponseEntity<Integer> response = reviewService.createReview(postId, reviewTxt, mockMultipartFiles);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewRepository).save(any(Review.class));
        verify(imageService).uploadImages(anyList(), anyString(), any(Review.class));
    }

    @Test
    void findReviewByPostId() {
        // given
        Long postId = 1L;
        BkBoard mockBoard = Mockito.mock(BkBoard.class);

        String reviewTxt = "리뷰1";
        Review review = Review.builder()
                .id(1L)
                .bkBoard(mockBoard)
                .reviewContent(reviewTxt)
                .build();
        List<String> mockImageUrls = new ArrayList<>();

        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(mockBoard));
        when(mockBoard.getReview()).thenReturn(review);
        when(imageService.findImageUrlsByReviewId(review.getId())).thenReturn(mockImageUrls);

        // when
        ResponseEntity<?> response = reviewService.findReviewByPostId(postId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ReviewResponseDto);

        ReviewResponseDto responseBody = (ReviewResponseDto) response.getBody();
        assertEquals(review.getReviewContent(), responseBody.getReviewContent());
    }

    @Test
    void updateSave() throws IOException {
        // given
        Long postId = 1L;
        BkBoard mockBoard = Mockito.mock(BkBoard.class);

        String reviewTxt = "리뷰1";
        String updatedTxt = "리뷰 수정";
        Review review = Review.builder()
                .id(1L)
                .bkBoard(mockBoard)
                .reviewContent(reviewTxt)
                .build();

        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setReviewContent(updatedTxt);
        List<MultipartFile> mockMultipartFiles = Collections.emptyList();

        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(mockBoard));
        when(mockBoard.getReview()).thenReturn(review);
        when(reviewRepository.save(any())).thenReturn(review);

        // When
        ResponseEntity<?> response = reviewService.updateSave(postId, requestDto, mockMultipartFiles);

        // Then
        assertEquals(ResponseEntity.ok(HttpServletResponse.SC_OK).getStatusCode(), response.getStatusCode());
        assertEquals(updatedTxt, review.getReviewContent(), updatedTxt);

        verify(reviewRepository).save(review);
        verify(imageService).updateImagesForReview(eq(review.getId()), eq(mockMultipartFiles), eq(review));
    }

    @Test
    void deleteByReviewId() {
        // given
        Long postId = 1L;
        BkBoard mockBoard = Mockito.mock(BkBoard.class);
        Review mockReview = Mockito.mock(Review.class);

        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(mockBoard));
        when(mockBoard.getReview()).thenReturn(mockReview);
        when(mockReview.getId()).thenReturn(1L);

        // when
        ResponseEntity<Integer> response = reviewService.deleteByReviewId(postId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewRepository).delete(mockReview);
        verify(imageService).deleteImagesByReviewId(mockReview.getId());
        verify(bkBoardRepository).save(mockBoard);
    }
}