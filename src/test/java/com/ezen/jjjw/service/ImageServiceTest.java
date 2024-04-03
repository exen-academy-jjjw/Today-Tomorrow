package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.domain.entity.ReviewFile;
import com.ezen.jjjw.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * packageName    : com.RSMboard.RSMboard.service
 * fileName       : ImageServiceTest
 * author         : won
 * date           : 2024-04-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-02        won              최초 생성
 */

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private S3Uploader amazonS3Service;
    @Mock
    private MultipartFile file1;
    @Mock
    private MultipartFile file2;
    @Mock
    private MultipartFile fileToUpdate;

    @Test
    void uploadImages() throws IOException {
        // given
        Review mockReview = Mockito.mock(Review.class);

        String directory = "bucket";
        String uploadedUrl1 = "https://example.com/bucket/image1.jpg";
        String uploadedUrl2 = "https://example.com/bucket/image2.jpg";
        List<MultipartFile> multipartFiles = Arrays.asList(file1, file2);
        List<String> uploadedUrls = Arrays.asList(uploadedUrl1, uploadedUrl2);

        when(amazonS3Service.upload(multipartFiles, directory)).thenReturn(uploadedUrls);

        // when
        List<String> result = imageService.uploadImages(multipartFiles, directory, mockReview);

        // then
        assertEquals(2, result.size());
        assertEquals(uploadedUrl1, result.get(0));
        assertEquals(uploadedUrl2, result.get(1));

        ArgumentCaptor<List<ReviewFile>> captor = ArgumentCaptor.forClass(List.class);
        verify(fileRepository).saveAll(captor.capture());
    }

    @Test
    void findImageUrlsByReviewId() {
        // given
        Long reviewId = 1L;
        Review mockReview = Mockito.mock(Review.class);

        String uploadedUrl1 = "https://example.com/bucket/image1.jpg";
        String uploadedUrl2 = "https://example.com/bucket/image2.jpg";
        ReviewFile reviewFile1 =  ReviewFile.builder()
                .review(mockReview)
                .fileUrl(uploadedUrl1)
                .build();
        ReviewFile reviewFile2 =  ReviewFile.builder()
                .review(mockReview)
                .fileUrl(uploadedUrl2)
                .build();

        List<ReviewFile> reviewFileList = Arrays.asList(reviewFile1, reviewFile2);

        when(fileRepository.findAllByReviewId(reviewId)).thenReturn(reviewFileList);

        // when
        List<String> result = imageService.findImageUrlsByReviewId(reviewId);

        // then
        assertEquals(2, result.size());
    }

    @Test
    void updateImagesForReview() throws IOException {
        // given
        Long reviewId = 1L;
        Review mockReview = Mockito.mock(Review.class);

        String directory = "bucket";
        String uploadedUrl1 = "https://example.com/bucket/image1.jpg";
        String uploadedUrl2 = "https://example.com/bucket/image2.jpg";
        String updateUploadedUrl = "https://example.com/bucket/image3.jpg";
        List<MultipartFile> newImages = Arrays.asList(file1, file2, fileToUpdate);
        List<String> uploadedUrls = Arrays.asList(uploadedUrl1, uploadedUrl2, updateUploadedUrl);

        when(amazonS3Service.upload(newImages, directory)).thenReturn(uploadedUrls);

        // when
        imageService.updateImagesForReview(reviewId, newImages, mockReview);

        // then
        ArgumentCaptor<List<ReviewFile>> captor = ArgumentCaptor.forClass(List.class);
        verify(fileRepository).saveAll(captor.capture());
        List<String> savedFiles = captor.getValue().stream().map(ReviewFile::getFileUrl).toList();
        List<String> expectedUrls = new ArrayList<>(uploadedUrls);

        assertEquals(expectedUrls, savedFiles);
    }

    @Test
    void deleteImagesByReviewId() {
        // given
        Long reviewId = 1L;
        List<ReviewFile> mockReviewFiles = List.of(Mockito.mock(ReviewFile.class), Mockito.mock(ReviewFile.class));

        when(mockReviewFiles.get(0).getFileUrl()).thenReturn("bucket/image1.jpg");
        when(mockReviewFiles.get(1).getFileUrl()).thenReturn("bucket/image2.jpg");

        when(fileRepository.findAllByReviewId(reviewId)).thenReturn(mockReviewFiles);

        // when
        imageService.deleteImagesByReviewId(reviewId);

        // then
        for (ReviewFile file : mockReviewFiles) {
            verify(fileRepository).delete(file);
            verify(amazonS3Service).deleteFile(file.getFileUrl());
        }
    }
}