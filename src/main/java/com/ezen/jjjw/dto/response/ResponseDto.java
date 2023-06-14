package com.ezen.jjjw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : com.member.member_jwt.dto.res
 * fileName       : ResponseDto
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                reponse로 보낼 데이터를 좀 더 예쁘게 보내기 위한 클래스
 *                                앞으로 모든 데이터는 해당 클래스에 한번 더 감싸진 채 보낼 예정
 */
@Getter
@AllArgsConstructor
public class ResponseDto<T> {

    // 성공 여부, 실제 보내고자 하는 데이터, 에러 여부를 필드로 선언
    private boolean success;
    private T data;
    private Error error;

    // 데이터를 제대로 보내지 못할 경우 error 필드에 담을 코드와 메세지 필드를 이너 클래스로 선언
    @Getter
    @AllArgsConstructor
    static class Error {
        private String code;
        private String message;
    }

    // 데이터를 성공적으로 보내게 될 경우
    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, data, null);
    }

    // 데이터를 보내지 못할 경우
    public static <T> ResponseDto<T> fail(String code, String message) {
        return new ResponseDto<>(false, null, new Error(code, message));
    }
}
