package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Review;
import com.ezen.jjjw.domain.entity.ReviewFile;
import com.ezen.jjjw.dto.request.ReviewRequestDto;
import com.ezen.jjjw.dto.response.FileResponseDto;
import com.ezen.jjjw.dto.response.ReviewResponseDto;
import com.ezen.jjjw.repository.BkBoardRepository;
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
//    @Transactional
//    public ResponseEntity<Integer> createReview(Long postId, String reviewContent,
////                                                List<MultipartFile> multipartFiles
//                                                ReviewResponseDto reviewDto) throws IOException {
//        BkBoard bkBoard = isPresentPost(postId);
//        if (bkBoard == null) {
//            log.info("존재하지 않는 게시글");
//            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
//        }
//
//        int existReview = bkBoard.getExistReview();
//        if (existReview == 1) {
//            log.info("리뷰가 존재하는 게시글");
//            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
//        }
//
////        Review review = Review.builder()
////                .reviewContent(reviewContent)
////                .bkBoard(bkBoard)
////                .build();
////        bkBoard.updateExistReview(bkBoard);
////        bkBoardRepository.save(bkBoard);
////        reviewRepository.save(review);
////
////        log.info("리뷰 작성 성공");
//
//        // file
////        if (multipartFiles != null && !multipartFiles.isEmpty()) {
////            String folderPath = "C:/tt/";
////            File localFolder = new File(folderPath);
////            if (!localFolder.exists() && !localFolder.mkdirs()) {
////                log.info("폴더 생성 실패");
////                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
////            }
////
////            List<String> saveFiles = new ArrayList<>();
////            for (MultipartFile multipartFile : multipartFiles) {
////                String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
////                log.info("fileName : ", fileName);
////                File savefile = new File(localFolder, fileName);
////                try {
////                    multipartFile.transferTo(savefile);
////                    String fileUrl = localFolder + "/" + fileName;
////                    saveFiles.add(fileUrl);
////                } catch (IOException e) {
////                    log.info("파일 업로드 실패");
////                    return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
////                }
////            }
////
////            for (String fileUrl : saveFiles) {
////                log.info(fileUrl);
////                ReviewFile reviewFile = ReviewFile.builder()
////                        .review(review)
////                        .fileUrl(fileUrl)
////                        .build();
////                fileRepository.save(reviewFile);
////            }
////        }
//
//        // 추가
//        if(reviewDto.getBoardFile().isEmpty()) {
//            Review review = Review.builder()
//                    .reviewContent(reviewContent)
//                    .bkBoard(bkBoard)
//                    .build();
//            bkBoard.updateExistReview(bkBoard);
//            bkBoardRepository.save(bkBoard);
//            reviewRepository.save(review);
//
//            log.info("리뷰 작성 성공");
//        } else {
//            MultipartFile boardFile = reviewDto.getBoardFile(); // 1.
//            String originalFilename = boardFile.getOriginalFilename(); // 2.
//            String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3.
//            String savePath = "C:/upload/" + storedFileName; // 4. C:/springboot_img/9802398403948_내사진.jpg
//            boardFile.transferTo(new File(savePath)); // 5.
//            Review boardEntity = Review.toSaveFileEntity(reviewDto);
//            Long savedId = reviewRepository.save(boardEntity).getId();
//            Review board = reviewRepository.findById(savedId).get();
//
//            ReviewFile boardFileEntity = ReviewFile.toBoardFileEntity(board, originalFilename, storedFileName);
//            fileRepository.save(boardFileEntity);
//        }
//
//
//
//        log.info("파일 저장 성공");
//        return ResponseEntity.ok(HttpServletResponse.SC_OK);
//    }

    @Transactional
    public ResponseEntity<Integer> createReview(Long postId, String reviewContent, ReviewResponseDto reviewDto) throws IOException {
        BkBoard bkBoard = isPresentPost(postId);
        if (bkBoard == null) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        int existReview = bkBoard.getExistReview();
        if (existReview == 1) {
            log.info("리뷰가 존재하는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (reviewDto.getBoardFile().isEmpty()) {
            Review review = Review.builder()
                    .reviewContent(reviewContent)
                    .bkBoard(bkBoard)
                    .build();
            bkBoard.updateExistReview(bkBoard);
            bkBoardRepository.save(bkBoard);
            reviewRepository.save(review);

            log.info("리뷰 작성 성공");
        } else {
            MultipartFile boardFile = reviewDto.getBoardFile();
            String originalFilename = boardFile.getOriginalFilename();
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
            String savePath = "C:/upload/" + storedFileName;
            boardFile.transferTo(new File(savePath));
            Review boardEntity = Review.toSaveFileEntity(reviewDto);
            Long savedId = reviewRepository.save(boardEntity).getId();
            Review board = reviewRepository.findById(savedId).get();

            ReviewFile boardFileEntity = ReviewFile.toBoardFileEntity(board, originalFilename, storedFileName);
            fileRepository.save(boardFileEntity);
        }

        log.info("파일 저장 성공");
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
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        int existReview = bkBoard.getExistReview();
        if (existReview == 0) {
            log.info("존재하지 않는 리뷰");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        Review findReview = bkBoard.getReview();
        List<ReviewFile> reviewFIle = fileRepository.findAllByReviewId(findReview.getId());

//        List<String> imageList = new ArrayList<>();
//        for (ReviewFile image : reviewFIle) {
//            imageList.add(image.getFileUrl());
//        }
//
//        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
//                .id(findReview.getId())
//                .postId(findReview.getBkBoard().getPostId())
//                .fileUrlList(imageList)
//                .reviewContent(findReview.getReviewContent())
//                .createdAt(findReview.getCreatedAt())
//                .modifiedAt(findReview.getModifiedAt())
//                .build();

        // file
        List<ReviewFile> allReviewFiles = fileRepository.findByReviewIdOrderByModifiedAtDesc(findReview.getId());
        if (null == allReviewFiles) {
            log.info("존재하지 않는 파일");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        List<FileResponseDto> ReviewResponseDtoList = new ArrayList<>();
        for (ReviewFile reviewFile : allReviewFiles) {
            ReviewResponseDtoList.add(
                    FileResponseDto.builder()
                            .id(reviewFile.getId())
                            .reviewId(reviewFile.getReview().getId())
                            .originalFileName(reviewFile.getOriginalFileName())
                            .storedFileName(reviewFile.getStoredFileName())
//                            .fileUrl(reviewFile.getFileUrl())
                            .createdAt(reviewFile.getCreatedAt())
                            .modifiedAt(reviewFile.getModifiedAt())
                            .build()
            );
        }

//        return ResponseEntity.ok(reviewResponseDto);
        return null;
    }

    // 리뷰 게시글 수정 PUT /review/update/{postId}
    @Transactional
    public ResponseEntity<?> updateSave(Long postId, ReviewRequestDto reviewRequestDto, List<MultipartFile> multipartFiles) {

        // 게시글 유효성 검증
        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        // 리뷰 유효성 검증
        int existReview = bkBoard.getExistReview();
        if (existReview == 0) {
            log.info("존재하지 않는 리뷰");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        // reviewContent 업데이트
        Review findReview = bkBoard.getReview();
        findReview.update(reviewRequestDto.getReviewContent());
        reviewRepository.save(findReview.getBkBoard().getReview());
        log.info("리뷰 수정 성공");

        // 게시글로부터 타고 들어가 뽑아온 리뷰 파일 리스트
        List<ReviewFile> reviewFileList = bkBoard.getReview().getReviewFileList();

        // file 관련 코드
        // 사용자로부터 받은 파일이 null이 아닐 경우에만 다음 로직 실행
        if (multipartFiles != null && !multipartFiles.isEmpty()) {

            // DB 속의 리뷰 파일 리스트와 사용자로부터 받은 파일 리스트의 파일명 비교
            for (ReviewFile oriFile : reviewFileList) {
                // 프론트가 보내준 파일명 리스트가 위의 oriFile을 포함하고 있지 않다면
//                if (!reviewRequestDto.getFileUrlList().contains(oriFile.getFileUrl())) {
//                    String filePath = oriFile.getFileUrl();
//                    File deleteFile = new File(filePath);
//                    deleteFile.delete();
//                    fileRepository.delete(oriFile);
//                }
            }

            // 사용자가 새로 보내는 사진 중에 위와 겹치는 게 없다면 해당 파일은 저장
            String folderPath = "C:/upload/" + findReview.getId();
            File localFolder = new File(folderPath);

//            try {
//                if (!localFolder.exists() && !localFolder.mkdirs()) {
//                    log.info("폴더 생성 실패");
//                    return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
//                }
//
//                for (MultipartFile newFile : multipartFiles) {
//                    // 새로 들어온 파일명 앞에 UUID 포함해 다시 저장
//                    String fileName = UUID.randomUUID() + "_" + newFile.getOriginalFilename();
//                    String fileUrl = folderPath + "/" + fileName;
//                    // 새로 보내는 파일이 DB에 이미 존재하는지 확인
//                    boolean existsInDB = false;
//                    for (ReviewFile oriFile : reviewFileList) {
//                        if (oriFile.getFileUrl().equals(fileUrl)) {
//                            existsInDB = true;
//                            break;
//                        }
//                    }
                    // DB에 없는 파일만 저장
//                    if (!existsInDB) {
//                        File saveFile = new File(localFolder, fileName);
//                        newFile.transferTo(saveFile);
//                        // DB에 저장
//                        ReviewFile upreviewFile = ReviewFile.builder()
//                                .review(findReview)
//                                .fileUrl(fileUrl)
//                                .build();
//                        fileRepository.save(upreviewFile);
//                    }
//                }
//            } catch (IOException e) {
//                log.info("파일 업로드 실패");
//                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
//            }
//        } else {
//            for (ReviewFile oriFile : reviewFileList) {
//                // 프론트가 보내준 파일명 리스트가 위의 oriFile을 포함하고 있지 않다면
//                if (!reviewRequestDto.getFileUrlList().contains(oriFile.getFileUrl())) {
//                    fileRepository.delete(oriFile);
//                }
//            }
        }
        log.info("파일 수정 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }


    // 리뷰 게시글 삭제 DELETE /review/delete/{postId}
    @Transactional
    public ResponseEntity<Integer> deleteByReviewId(Long postId) {

        BkBoard bkBoard = isPresentPost(postId);
        if (null == bkBoard) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        int existReview = bkBoard.getExistReview();
        if (existReview == 0) {
            log.info("존재하지 않는 리뷰");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        Review findReview = bkBoard.getReview();
        reviewRepository.delete(findReview);
        log.info("리뷰 삭제 성공");

        // 리뷰에 파일이 존재한다면 로컬에 있는 파일 폴더 삭제
        String folderPath = "C:/upload/" + findReview.getId();
        File localFolder = new File(folderPath);
        if (localFolder.exists()) {
            try {
                FileUtils.deleteDirectory(localFolder);
            } catch (IOException e) {
                log.info("폴더 삭제 실패");
                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        log.info("폴더 삭제 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }
}

