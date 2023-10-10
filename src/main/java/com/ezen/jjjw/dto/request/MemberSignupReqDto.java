package com.ezen.jjjw.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * packageName    : com.member.member_jwt.dto.req
 * fileName       : MemberSignupReqDto
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                회원가입을 진행할 때 데이터를 담아 보낼 역할을 할 dto
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupReqDto {

    /**
     * @NotBlank : 비울 수 없음
     * @Size(min = ?, max = ?) : 최소와 최대 길이 지정
     * @Pattern(regexp = "?") : 정규식을 이용해 패턴 지정
     */

    @NotBlank
    @Size(min = 4, max = 12)
    @Pattern(regexp = "[a-zA-Z\\d]*${3,12}") // 영어 대소문자와 숫자만을 이용하여 4~12자로 작성
    private String memberId;

    @NotBlank
    @Size(min = 1, max = 10)
    @Pattern(regexp = "[a-zA-Z가-힣\\d]*${0,12}") // 영어 대소문자와 숫자, 한글만을 이용하여 1~12자로 작성
    private String nickname;

    @NotBlank
    @Size(min = 4, max = 32)
    @Pattern(regexp = "[a-z\\d]*${3,32}") // 영어 대소문자와 숫자만을 이용하여 4~32자로 작성
    private String password;

    @NotBlank
    private String passwordConfirm;
}
