package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.domain.entity.ReviewFile;
import com.ezen.jjjw.dto.response.FileResponseDto;
import com.ezen.jjjw.repository.FileRepository;
import com.ezen.jjjw.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.connectiontest.test.service
 * fileName       : FileServise.java
 * author         : won
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        won       최초 생성
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final ReviewRepository reviewRepository;

    // 리뷰 파일 첨부 POST /file/create/{reviewId}
    @Transactional
    public ResponseEntity<Integer> createFile(Long reviewId, List<MultipartFile> multipartFiles) {
        Review review = isPresentReview(reviewId);
        if (null == review) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        String folderPath = "C:/upload/" + reviewId;
        File localFolder = new File(folderPath);
        if(!localFolder.exists() && !localFolder.mkdirs()) {
//            throw new CustomException(ErrorCode.NOT_CREATE_FOLDER);
            log.info("폴더 생성 실패");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        List<String> saveFiles = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            File savefile = new File(localFolder, fileName);
            try {
                multipartFile.transferTo(savefile);
                String fileUrl = localFolder + "/" + fileName;
                saveFiles.add(fileUrl);
            } catch (IOException e) {
//                throw new CustomException(ErrorCode.NOT_UPLOAD_FILE);
                log.info("파일 업로드 실패");
                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        for (String fileUrl : saveFiles) {
            ReviewFile reviewFile = ReviewFile.builder()
                    .review(review)
                    .fileUrl(fileUrl)
                    .build();
            fileRepository.save(reviewFile);
        }
//        return ResponseEntity.ok("저장 성공");
        log.info("파일 저장 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional(readOnly = true)
    public Review isPresentReview(Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        return optionalReview.orElse(null);
    }

    // 리뷰 파일 상세 GET /file/detail/{reviewId}
    @Transactional
    public ResponseEntity<?> findReviewId(Long reviewId) {

        Review review = isPresentReview(reviewId);
        if (null == review) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        List<ReviewFile> allReviewFiles = fileRepository.findByReviewIdOrderByModifiedAtDesc(review.getId());
        if (null == allReviewFiles) {
//            throw new CustomException(ErrorCode.NOT_FOUND_FILE);
            log.info("존재하지 않는 파일");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        List<FileResponseDto> ReviewResponseDtoList = new ArrayList<>();
        for (ReviewFile reviewFile : allReviewFiles) {
            ReviewResponseDtoList.add(
                    FileResponseDto.builder()
                            .id(reviewFile.getId())
                            .reviewId(reviewFile.getReview().getId())
                            .fileUrl(reviewFile.getFileUrl())
                            .createdAt(reviewFile.getCreatedAt())
                            .modifiedAt(reviewFile.getModifiedAt())
                            .build()
            );
        }
        return ResponseEntity.ok(ReviewResponseDtoList);
    }

    // 리뷰 파일 수정 PUT /file/update/{reviewId}
    @Transactional
    public ResponseEntity<?> updateFile(Long reviewId, List<MultipartFile> multipartFiles) {

        Review review = isPresentReview(reviewId);
        if (null == review) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        List<ReviewFile> allReviewFiles = fileRepository.findAllByReviewId(review.getId());
        if (null == allReviewFiles) {
//            throw new CustomException(ErrorCode.NOT_FOUND_FILE);
            log.info("존재하지 않는 파일");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        List<String> imageList = new ArrayList<>();
        for(ReviewFile image : allReviewFiles){
            imageList.add(image.getFileUrl());
        }

        for(ReviewFile ori : allReviewFiles){
            for (MultipartFile multipartFile : multipartFiles) {
                if(!ori.equals(multipartFile)) {
                    fileRepository.delete(ori);
                    String filePath = ori.getFileUrl();
                    File deleteFile = new File(filePath);
                    deleteFile.delete();
                }
            }
        }

        String folderPath = "C:/upload/" + reviewId;
        File localFolder = new File(folderPath);

        for (MultipartFile multipartFile : multipartFiles) {
            for(ReviewFile image : allReviewFiles) {
                if(!multipartFile.equals(image)) {
                    String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
                    File savefile = new File(localFolder, fileName);
                    try {
                        multipartFile.transferTo(savefile);
                        String fileUrl = localFolder + "/" + fileName;
                        imageList.add(fileUrl);
                    } catch (IOException e) {
//                        throw new CustomException(ErrorCode.NOT_UPLOAD_FILE);
                        log.info("파일 업로드 실패");
                        return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
        }

        for (String fileUrl : imageList) {
            ReviewFile upreviewFile = ReviewFile.builder()
                    .review(review)
                    .fileUrl(fileUrl)
                    .build();
            fileRepository.save(upreviewFile);
        }

//        return ResponseEntity.ok("수정 성공");
        log.info("파일 수정 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    // 리뷰 파일 삭제 POST /file/delete/{reviewId}
    @Transactional
    public ResponseEntity<Integer> deleteByFileId(Long reviewId) {

        Review review = isPresentReview(reviewId);
        if (null == review) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        List<ReviewFile> allReviewFiles = fileRepository.findByReviewIdOrderByModifiedAtDesc(review.getId());
        if (null == allReviewFiles) {
//            throw new CustomException(ErrorCode.NOT_FOUND_FILE);
            log.info("존재하지 않는 파일");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        for (ReviewFile existfile : allReviewFiles) {
            fileRepository.delete(existfile);
        }

        String folderPath = "C:/upload/" + reviewId;
        File localFolder = new File(folderPath);
        if (localFolder.exists()) {
            try {
                FileUtils.deleteDirectory(localFolder);
            } catch (IOException e) {
//                throw new CustomException(ErrorCode.NOT_DELETE_FILE);
                log.info("파일 삭제 실패");
                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

//        return ResponseEntity.ok("삭제 성공");
        log.info("파일 삭제 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }
}
