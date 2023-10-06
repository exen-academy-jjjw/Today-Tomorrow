package com.ezen.jjjw.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.ezen.jjjw.dto.request
 * fileName       : BkBoardUpdateReqDto
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
public class BkBoardUpdateReqDto {
    private String category; // 카테고리
    private String title;  // 제목
    private String content;  // 내용
    private int completion;
    private int share;
}
