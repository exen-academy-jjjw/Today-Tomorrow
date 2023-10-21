package com.ezen.jjjw.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MypageResponseDto {
    private String nickname;
    private Integer totalCount;
    private Integer completeCount;
    private Integer unCompleteCount;
}
