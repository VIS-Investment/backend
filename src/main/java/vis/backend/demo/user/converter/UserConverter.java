package vis.backend.demo.user.converter;

import lombok.NoArgsConstructor;
import vis.backend.demo.user.domain.Authority;
import vis.backend.demo.user.domain.AuthorityType;
import vis.backend.demo.user.domain.User;

@NoArgsConstructor
public class UserConverter {
    public static User toUser(String email, String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .username(email)
                .build();
    }

    public static Authority makeAuthority(User user) {
        return Authority.builder()
                .user(user)
                .authorityType(AuthorityType.USER)
                .build();
    }
}
