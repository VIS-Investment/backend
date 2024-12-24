package vis.backend.demo.user.converter;

import lombok.NoArgsConstructor;
import vis.backend.demo.user.domain.Authority;
import vis.backend.demo.user.domain.AuthorityType;
import vis.backend.demo.user.domain.User;

@NoArgsConstructor
public class UserConverter {
    public static User toUser(String email, String encodedPassword, String nickname) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();
    }

    public static Authority makeAuthority(User user) {
        return Authority.builder()
                .user(user)
                .authorityType(AuthorityType.USER)
                .build();
    }
}
