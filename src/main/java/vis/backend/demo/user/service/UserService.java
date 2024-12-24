package vis.backend.demo.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vis.backend.demo.global.api_payload.ErrorCode;
import vis.backend.demo.global.exception.GeneralException;
import vis.backend.demo.global.utils.Constants;
import vis.backend.demo.user.converter.UserConverter;
import vis.backend.demo.user.domain.User;
import vis.backend.demo.user.dto.UserRequestDto.UserLoginReqDto;
import vis.backend.demo.user.dto.UserRequestDto.UserRegisterReqDto;
import vis.backend.demo.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserRegisterReqDto userReqDto) {
        if (userRepository.findByEmail(userReqDto.getEmail()).isPresent()) {
            throw new GeneralException(ErrorCode.ALREADY_REGISTERED);
        }

        checkEmail(userReqDto.getEmail());
        checkPassword(userReqDto.getPassword());
        checkNickname(userReqDto.getNickname());

        String encodedPassword = passwordEncoder.encode(userReqDto.getPassword());

        User user = UserConverter.toUser(userReqDto.getEmail(), encodedPassword, userReqDto.getNickname());
        user.addAuthority(UserConverter.makeAuthority(user));
        userRepository.save(user);

        log.info("회원가입 완료 pk:{}, email:{}", user.getId(), user.getEmail());
    }

    public User login(UserLoginReqDto userReqDto, HttpServletRequest request) {
        User user = authenticate(userReqDto.getEmail(), userReqDto.getPassword());
        setSession(user, request);
        return user;
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false: 세션이 없으면 null 반환
        if (session != null) {
            session.invalidate(); // session 속 모든 속성값 삭제
        }
    }

    private void checkEmail(String email) {
        Pattern emailPattern = Pattern.compile(Constants.EMAIL_REGEX);
        if (!emailPattern.matcher(email).matches()) {
            throw new GeneralException(ErrorCode.WRONG_EMAIL_FORMAT);
        }
        if (userRepository.existsByEmail(email)) {
            throw new GeneralException(ErrorCode.ALREADY_EXISTED_EMAIL);
        }
    }

    private void checkPassword(String password) {
        Pattern passwordPattern = Pattern.compile(Constants.PASSWORD_REGEX);
        if (!passwordPattern.matcher(password).matches()) {
            throw new GeneralException(ErrorCode.WRONG_PASSWORD_FORMAT);
        }
    }

    private void checkNickname(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new GeneralException(ErrorCode.ALREADY_EXISTED_NICKNAME);
        }
    }

    private User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new GeneralException(ErrorCode.PASSWORD_MISMATCH);
        }

        return user;
    }

    private void setSession(User user, HttpServletRequest request) {
        request.getSession().invalidate(); // 세션을 생성하기 전에 기존의 세션 파기
        HttpSession session = request.getSession(true); // true: 세션이 없으면 새로 생성
        session.setAttribute("userEmail", user.getEmail()); // 생성된 세션은 클라이언트에 JSESSIONID 쿠키로 전달
        session.setMaxInactiveInterval(60 * 30); // 30분 동안 비활성 상태가 지속되면 세션이 만료
    }

}
