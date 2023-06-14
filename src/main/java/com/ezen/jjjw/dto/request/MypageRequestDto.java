package com.ezen.jjjw.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageRequestDto {

    private String nickname;
    private String password;
    private String newPassword;

}
