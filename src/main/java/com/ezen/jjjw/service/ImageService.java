package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.domain.entity.ReviewFile;
import com.ezen.jjjw.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.RSMboard.RSMboard.service
 * fileName       : ImageService.java
 * author         : won
 * date           : 2024-04-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-02        won              최초 생성
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {
    private final FileRepository fileRepository;

    private final S3Uploader amazonS3Service;

    @Transactional
    public List<String> uploadImages(List<MultipartFile> multipartFiles, String directory, Review review) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }
        // S3에 파일을 업로드하고, 반환된 URL 리스트를 받음
        List<String> uploadedUrls = amazonS3Service.upload(multipartFiles, "bucket");
        // 업로드된 파일 URL 을 사용하여 ReviewFile 객체를 생성하고 저장
        List<ReviewFile> saveImages = new ArrayList<>();
        for(String fileUrl : uploadedUrls){
            ReviewFile image = ReviewFile.builder()
                    .review(review)
                    .fileUrl(fileUrl)
                    .build();
            saveImages.add(image);
        }
        fileRepository.saveAll(saveImages);
        log.info("파일 저장 성공");
        return uploadedUrls;
    }

    @Transactional(readOnly = true)
    public List<String> findImageUrlsByReviewId(Long reviewId) {
        List<ReviewFile> reviewFiles = fileRepository.findAllByReviewId(reviewId);
        List<String> imageUrls = new ArrayList<>();
        for (ReviewFile reviewFile : reviewFiles) {
            imageUrls.add(reviewFile.getFileUrl());
        }
        return imageUrls;
    }

    @Transactional
    public void updateImagesForReview(Long reviewId, List<MultipartFile> newImages, Review review) throws IOException {
        // 이미지 추가
        if (newImages != null && !newImages.isEmpty()) {
            // 업로드된 이미지 정보를 바탕으로 ReviewFile 엔티티 생성 및 저장
            uploadImages(newImages, "bucket", review);
        }
    }

    @Transactional
    public void deleteImagesByReviewId(Long reviewId) {
        List<ReviewFile> reviewFiles = fileRepository.findAllByReviewId(reviewId);
        for (ReviewFile oriFile : reviewFiles) {
            log.info("파일 = {}",oriFile.getFileUrl());
            // S3저장소에서 삭제
            String name = URLDecoder.decode(oriFile.getFileUrl().substring(oriFile.getFileUrl().lastIndexOf("/") + 1), StandardCharsets.UTF_8);
            amazonS3Service.deleteFile("bucket/" + name);
            // DB 이미지 삭제
            fileRepository.delete(oriFile);
        }
        log.info("이미지 삭제 성공");
    }
}
