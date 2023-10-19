package com.ezen.jjjw.dto.response;

import lombok.*;

//@Getter
//@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MypageResponseDto {
    private String nickname;
    private Integer totalCount;
    private Integer completeCount;
    private Integer unCompleteCount;

//    @Builder
//    public MypageResponseDto(final String nickname, final int totalCount, final int completeCount, final int unCompleteCount) {
//        this.nickname = nickname;
//        this.totalCount = totalCount;
//        this.completeCount = completeCount;
//        this.unCompleteCount = unCompleteCount;
//    }
}
