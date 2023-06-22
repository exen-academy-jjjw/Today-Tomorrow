package com.ezen.jjjw.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /* 400 Bad Request */
    INVALID_POST_TITLE(HttpStatus.BAD_REQUEST, "400", "제목은 1자 이상 50자 이하여야 합니다."),
    INVALID_POST_CONTENT(HttpStatus.BAD_REQUEST, "400", "내용은 300자 미만이어야 합니다."),
    EXIST_USERNAME(HttpStatus.BAD_REQUEST, "400", "이미 존재하는 아이디입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "400", "패스워드가 일치하지 않습니다."),
    /*에러 추가*/
    NOT_CREATE_FOLDER(HttpStatus.BAD_REQUEST, "400", "폴더 생성에 실패했습니다."),
    NOT_UPLOAD_FILE(HttpStatus.BAD_REQUEST, "400", "파일 업로드에 실패했습니다."),
    NOT_DELETE_FILE(HttpStatus.BAD_REQUEST, "400", "파일 삭제에 실패했습니다."),
    EXIST_REVIEW(HttpStatus.BAD_REQUEST, "400", "이미 존재하는 리뷰입니다."),

    /* 401 Bad Request */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "401", "유효하지 않은 토큰입니다."),

    /* 404 Not Found */
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "404", "존재하지 않는 게시글입니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "404", "존재하지 않는 코멘트입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404", "존재하지 않는 사용자입니다."),
    INVALID_USER(HttpStatus.NOT_FOUND, "404", "잘못된 사용자입니다."),
    /*에러 추가*/
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "404", "존재하지 않는 파일입니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "404", "존재하지 않는 리뷰입니다."),

    /* 500 Server Error */
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버가 원활하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
