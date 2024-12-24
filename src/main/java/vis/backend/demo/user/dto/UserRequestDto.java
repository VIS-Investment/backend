package vis.backend.demo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@NoArgsConstructor
public class UserRequestDto {
    @Schema(description = "SimpleReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserSimpleReqDto {

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "비밀번호")
        private String password;

    }
}