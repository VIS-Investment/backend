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

    // User
    ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "USER_400", "이미 회원가입을 하셨습니다. 로그인해주세요."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "USER_400", "아이디와 비밀번호를 다시 확인해주세요."),
    USER_NOT_LOGGED_IN(HttpStatus.BAD_REQUEST, "USER_400", "유저의 세션 ID가 쿠키에 없습니다."),

    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER_400", "비밀번호가 틀렸습니다. 다시 입력해주세요"),
    WRONG_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "USER_400", "올바른 이메일 형식으로 입력하세요."),
    ALREADY_EXISTED_EMAIL(HttpStatus.BAD_REQUEST, "USER_400", "이미 존재하는 이메일입니다."),

    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "사용자가 없는 이메일입니다.");

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
