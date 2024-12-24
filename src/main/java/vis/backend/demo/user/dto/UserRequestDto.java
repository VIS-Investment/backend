package vis.backend.demo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserRequestDto {
    @Schema(description = "UserRegisterReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserRegisterReqDto {

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "비밀번호")
        private String password;

        @Schema(description = "닉네임")
        private String nickname;

    }

    @Schema(description = "UserLoginReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginReqDto {

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "비밀번호")
        private String password;

    }
}