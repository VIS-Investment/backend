package vis.backend.demo.user.service;

import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vis.backend.demo.global.api_payload.ErrorCode;
import vis.backend.demo.global.exception.GeneralException;
import vis.backend.demo.global.utils.Constants;
import vis.backend.demo.user.converter.UserConverter;
import vis.backend.demo.user.domain.RoleType;
import vis.backend.demo.user.domain.User;
import vis.backend.demo.user.dto.UserRequestDto.UserSimpleReqDto;
import vis.backend.demo.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserSimpleReqDto userReqDto) {
        if (userRepository.findByEmail(userReqDto.getEmail()).isPresent()) {
            throw new GeneralException(ErrorCode.USER_ALREADY_REGISTERED);
        }

        checkEmail(userReqDto.getEmail());

        String encodedPassword = passwordEncoder.encode(userReqDto.getPassword());

        User user = UserConverter.toUser(userReqDto.getEmail(), encodedPassword);
        userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword())) // 해싱 후 비교
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_PASSWORD_MISMATCH));

    }

    public RoleType getRoleType(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));
        return user.getRole();
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

}
