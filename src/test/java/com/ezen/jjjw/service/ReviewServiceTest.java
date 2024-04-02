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
        String reviewTxt = "리뷰1";
        BkBoard mockbkBoard = new BkBoard();

        ReviewRequestDto reviewReqDto = new ReviewRequestDto();
        reviewReqDto.setReviewContent(reviewTxt);

        List<MultipartFile> mockMultipartFiles = Collections.emptyList();
        List<String> mockImageUrls = new ArrayList<>();

        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(mockbkBoard));
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
        String reviewTxt = "리뷰1";

        BkBoard mockbkBoard = Mockito.mock(BkBoard.class);
        Review mockreview = Review.builder()
                .id(1L)
                .bkBoard(mockbkBoard)
                .reviewContent(reviewTxt)
                .build();
        List<String> mockImageUrls = new ArrayList<>();

        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(mockbkBoard));
        when(mockbkBoard.getReview()).thenReturn(mockreview);
        when(imageService.findImageUrlsByReviewId(mockreview.getId())).thenReturn(mockImageUrls);

        // when
        ResponseEntity<?> response = reviewService.findReviewByPostId(postId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ReviewResponseDto);

        ReviewResponseDto responseBody = (ReviewResponseDto) response.getBody();
        assertNotNull(responseBody);
        assertEquals(mockreview.getReviewContent(), responseBody.getReviewContent());
    }

    // TODO update test 수정
    @Test
    void updateSave() throws IOException {
        // given
        Long postId = 1L;
        String updatedTxt = "리뷰 수정";
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setReviewContent(updatedTxt);
        List<MultipartFile> mockMultipartFiles = Collections.emptyList();

        BkBoard mockbkBoard = Mockito.mock(BkBoard.class);
        Review mockreview = Mockito.mock(Review.class);

        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(mockbkBoard));
        when(mockbkBoard.getReview()).thenReturn(mockreview);
        when(reviewRepository.save(any())).thenReturn(mockreview);

        // When
        ResponseEntity<?> response = reviewService.updateSave(postId, requestDto, mockMultipartFiles);

        // Then
        assertEquals(ResponseEntity.ok(HttpServletResponse.SC_OK).getStatusCode(), response.getStatusCode());

        verify(mockreview).update(updatedTxt);
        verify(reviewRepository).save(mockreview);
        verify(imageService).updateImagesForReview(eq(mockreview.getId()), eq(mockMultipartFiles), eq(mockreview));
    }

    // TODO delete test 작성
    @Test
    void deleteByReviewId() {
        // given

        // when

        // then

    }
}