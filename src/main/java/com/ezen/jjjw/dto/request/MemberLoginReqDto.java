package com.ezen.jjjw.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * packageName    : com.member.member_jwt.dto.request
 * fileName       : MemberLoginReqDto
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                로그인을 시도할 때 필요한 memberId와 password 정보만 담으면 된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginReqDto {

    @NotBlank
    private String memberId;

    @NotBlank
    private String password;

}