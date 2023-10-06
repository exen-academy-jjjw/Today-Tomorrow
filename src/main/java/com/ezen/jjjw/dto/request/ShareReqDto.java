package com.ezen.jjjw.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.ezen.jjjw.dto.request
 * fileName       : ShareReqDto
 * author         : wldk9
 * date           : 2023-10-06
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-10-06        sonjia       최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareReqDto {
    private Long postId;
    private int share;
}
