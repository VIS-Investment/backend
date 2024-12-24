package vis.backend.demo.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vis.backend.demo.global.api_payload.ErrorCode;
import vis.backend.demo.global.exception.GeneralException;
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
        // 이미 존재하는 유저인지 확인
        if (userRepository.findByEmail(userReqDto.getEmail()).isPresent()) {
            throw new GeneralException(ErrorCode.USER_ALREADY_REGISTERED);
        }

        // 비밀번호 암호화
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

}
