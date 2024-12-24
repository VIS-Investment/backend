package vis.backend.demo.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vis.backend.demo.global.api_payload.ApiResponse;
import vis.backend.demo.global.api_payload.ErrorCode;
import vis.backend.demo.global.api_payload.SuccessCode;
import vis.backend.demo.global.exception.GeneralException;
import vis.backend.demo.user.domain.User;
import vis.backend.demo.user.dto.UserRequestDto.UserSimpleReqDto;
import vis.backend.demo.user.service.UserService;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "회원가입하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2011", description = "회원가입 되었습니다."),
    })
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserSimpleReqDto userReqDto) {
        userService.register(userReqDto);
        return ApiResponse.onSuccess(SuccessCode.USER_SIGN_IN_SUCCESS, "회원가입 성공");
    }

    @Operation(summary = "로그인", description = "로그인하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2001", description = "로그인 되었습니다."),
    })
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody UserSimpleReqDto userReqDto, HttpServletRequest request) {
        User user = userService.login(userReqDto, request);

        return ApiResponse.onSuccess(SuccessCode.USER_LOGIN_SUCCESS, user.getEmail());
    }

    @Operation(summary = "로그아웃", description = "로그아웃하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2002", description = "로그아웃 되었습니다."),
    })
    @DeleteMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, "로그아웃 성공");
    }

    @Operation(summary = "로그인 상태 확인", description = "현재 로그인 상태를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2003", description = "로그인 상태가 유효합니다.."),
    })
    @GetMapping("/logged-check")
    public ApiResponse<String> validLoggedInAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 기존 세션이 있는지 확인
        if (session == null || session.getAttribute("userEmail") == null) {
            throw new GeneralException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        String emailAtttribute = (String) session.getAttribute("userEmail");

        return ApiResponse.onSuccess(SuccessCode.USER_LOGGED_CHECK_SUCCESS, emailAtttribute);
    }

}