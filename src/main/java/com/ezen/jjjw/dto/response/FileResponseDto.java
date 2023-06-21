package com.ezen.jjjw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * packageName    : com.connectiontest.test.dto.response
 * fileName       : FileResponseDto.java
 * author         : won
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        won       최초 생성
 */

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {
    private Long id;
    private Long reviewId;
//    private String fileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    private MultipartFile boardFile; // save.html -> Controller 파일 담는 용도
    private String originalFileName; // 원본 파일 이름
    private String storedFileName; // 서버 저장용 파일 이름
}
