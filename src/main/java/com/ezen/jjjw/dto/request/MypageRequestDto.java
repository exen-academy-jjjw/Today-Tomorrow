package com.ezen.jjjw.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
public class MypageRequestDto {

    private String nickname;
    private String password;
    private String newPassword;

}
