package com.ezen.jjjw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : com.connectiontest.test.dto.response
 * fileName       : ReviewResponseDto.java
 * author         : won
 * date           : 2023-06-07
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-07        won       최초 생성
 */

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long postId;
//    private List<String> fileUrlList;
    private String reviewContent;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private MultipartFile boardFile; // save.html -> Controller 파일 담는 용도
    private String originalFileName; // 원본 파일 이름
    private String storedFileName; // 서버 저장용 파일 이름
}

