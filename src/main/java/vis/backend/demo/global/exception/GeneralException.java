package vis.backend.demo.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vis.backend.demo.global.api_payload.ErrorCode;
import vis.backend.demo.global.api_payload.ReasonDto;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final ErrorCode code;

    public static GeneralException of(ErrorCode code) {
        return new GeneralException(code);
    }

    public ReasonDto getReason() {
        return this.code.getReason();
    }

}
