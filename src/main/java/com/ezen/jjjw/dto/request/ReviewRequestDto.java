package com.ezen.jjjw.dto.request;

import com.ezen.jjjw.domain.entity.BkBoard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.RSMboard.RSMboard.dto
 * fileName       : ReviewDTO.java
 * author         : won
 * date           : 2023-06-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        won       최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

    private String reviewContent;

    private BkBoard bkBoard;

}
