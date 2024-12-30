package vis.backend.demo.user.converter;

import lombok.NoArgsConstructor;
import vis.backend.demo.user.domain.RoleType;
import vis.backend.demo.user.domain.User;

@NoArgsConstructor
public class UserConverter {
    public static User toUser(String email, String encodedPassword, String nickname) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .role(RoleType.USER)
                .build();
    }

}
