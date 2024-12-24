package vis.backend.demo.global.api_payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 서버 개발자에게 문의하세요."),
    INVALID_PARAMETER(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_5002", "잘못된 DAY Parameter 가 들어왔습니다. "),

    USER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "USER_4001", "이미 회원가입을 하셨었습니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER_4002", "비밀번호가 틀렸습니다."),
    USER_NOT_LOGGED_IN(HttpStatus.BAD_REQUEST, "USER_4003", "유저의 세션 ID가 쿠키에 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(false)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
