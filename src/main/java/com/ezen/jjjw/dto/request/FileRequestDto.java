package com.ezen.jjjw.dto.request;

import com.ezen.jjjw.domain.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * packageName    : com.connectiontest.test.dto.request
 * fileName       : FileRequestDto.java
 * author         : won
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        won       최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRequestDto {

    @NotBlank
    private MultipartFile fileUrl;

    private Review review;
}
