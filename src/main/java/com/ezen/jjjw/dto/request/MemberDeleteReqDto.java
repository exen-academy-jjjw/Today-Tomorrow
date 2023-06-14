package com.ezen.jjjw.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * packageName    : com.member.member_jwt.dto.request
 * fileName       : MemberDeleteReqDto
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                회원 탈퇴를 요청할 때 비밀번호를 입력받기 위한 dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDeleteReqDto {

    @NotBlank
    private String password;
}
