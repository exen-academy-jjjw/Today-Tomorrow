package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.domain.entity.ReviewFile;
import com.ezen.jjjw.dto.response.FileResponseDto;
import com.ezen.jjjw.dto.response.ResponseDto;
import com.ezen.jjjw.repository.FileRepository;
import com.ezen.jjjw.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

@RequiredArgsConstructor
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final ReviewRepository reviewRepository;

    // 리뷰 파일 첨부 POST /file/create/{reviewId}
    public ResponseDto<?> createFile(Long reviewId, List<MultipartFile> multipartFiles, HttpServletRequest request) {

        Review review = isPresentReview(reviewId);
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 리뷰 id 입니다.");
        }

        // 파일 저장 경로
        String folderPath = "C:/upload/" + reviewId;
        File localFolder = new File(folderPath);
        if (!localFolder.exists()) {
            if (localFolder.mkdirs()) {
                ResponseDto.success("폴더 생성 성공");
            } else {
                return ResponseDto.fail("ERROR", "폴더 생성 실패");
            }
        }

        List<String> saveFiles = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {

            // 파일명 생성
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            // 파일 저장
            File savefile = new File(localFolder, fileName);
            try {
                multipartFile.transferTo(savefile);
                // ReviewFile 생성 및 저장
                String fileUrl = localFolder + "/" + fileName;
                saveFiles.add(fileUrl);
            } catch (IOException e) {
                return ResponseDto.fail("ERROR", "파일 업로드에 실패하였습니다.");
            }
        }
        for (String fileUrl : saveFiles) {
            ReviewFile reviewFile = ReviewFile.builder()
                    .review(review)
                    .fileUrl(fileUrl)
                    .build();
            fileRepository.save(reviewFile);
        }
        return ResponseDto.success("저장 성공");
    }

    @Transactional(readOnly = true)
    public Review isPresentReview(Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        return optionalReview.orElse(null);
    }

    // 리뷰 파일 상세 GET /file/detail/{reviewId}
    public ResponseDto<?> findReviewId(Long reviewId, HttpServletRequest request) {

        Review review = isPresentReview(reviewId);
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 리뷰 id 입니다.");
        }

        List<ReviewFile> allReviewFiles = fileRepository.findByReviewIdOrderByModifiedAtDesc(review.getId());
        if (null == allReviewFiles) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 파일 입니다.");
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
        return ResponseDto.success(ReviewResponseDtoList);
    }

    // 리뷰 파일 수정 PUT /file/update/{reviewId}
    public ResponseDto<?> updateFile(Long reviewId, List<MultipartFile> multipartFiles, HttpServletRequest request) {

        Review review = isPresentReview(reviewId);
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 리뷰 id 입니다.");
        }

        List<ReviewFile> allReviewFiles = fileRepository.findAllByReviewId(review.getId());
        if (null == allReviewFiles) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 파일 입니다.");
        }

        List<String> imageList = new ArrayList<>();
        for(ReviewFile image : allReviewFiles){
            imageList.add(image.getFileUrl());
        }

        for(ReviewFile ori : allReviewFiles){
            for (MultipartFile multipartFile : multipartFiles) {
                // 수정된 기존 파일 삭제
                if(!ori.equals(multipartFile)) {
                    fileRepository.delete(ori);
                    String filePath = ori.getFileUrl();
                    File deleteFile = new File(filePath);
                    deleteFile.delete();
                }
            }
        }

        // 파일 저장 경로
        String folderPath = "C:/upload/" + reviewId;
        File localFolder = new File(folderPath);

//        List<String> updateFiles = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            for(ReviewFile image : allReviewFiles) {
                if(!multipartFile.equals(image)) {
                    // 파일명 생성
                    String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
                    // 파일 저장
                    File savefile = new File(localFolder, fileName);
                    try {
                        multipartFile.transferTo(savefile);
                        // ReviewFile 생성 및 저장
                        String fileUrl = localFolder + "/" + fileName;
                        imageList.add(fileUrl);
                    } catch (IOException e) {
                        return ResponseDto.fail("ERROR", "파일 업로드에 실패하였습니다.");
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
        return ResponseDto.success("수정 성공");
    }

    // 리뷰 파일 삭제 POST /file/delete/{reviewId}
    public ResponseDto<?> deleteByFileId(Long reviewId, List<MultipartFile> multipartFiles, HttpServletRequest request) {

        Review review = isPresentReview(reviewId);
        if (null == review) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 리뷰 id 입니다.");
        }

        List<ReviewFile> allReviewFiles = fileRepository.findByReviewIdOrderByModifiedAtDesc(review.getId());
        if (null == allReviewFiles) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 파일 입니다.");
        }

        for (ReviewFile existfile : allReviewFiles) {
            fileRepository.delete(existfile);
        }

        // 파일 저장 경로
        String folderPath = "C:/upload/" + reviewId;
        File localFolder = new File(folderPath);
        if (localFolder.exists()) {
            try {
                FileUtils.deleteDirectory(localFolder);
                ResponseDto.success("폴더 삭제 성공");
            } catch (IOException e) {
                return ResponseDto.fail("ERROR", "폴더 삭제 실패");
            }
        }
        return ResponseDto.success("delete success");
    }
}
