package vis.backend.demo.global.api_payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {

    // Common
    OK(HttpStatus.OK, "COMMON_200", "Success"),
    CREATED(HttpStatus.CREATED, "COMMON_201", "Created"),

    // User
    USER_SIGN_IN_SUCCESS(HttpStatus.CREATED, "USER_2011", "유저정보가 DB에 저장되었습니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "USER_2001", "유저 로그인이 성공했습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2002", "유저 로그아웃이 성공했습니다."),
    USER_LOGGED_CHECK_SUCCESS(HttpStatus.OK, "USER_2003", "로그인 상태가 유효합니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(true)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
