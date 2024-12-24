package vis.backend.demo.user.converter;

import lombok.NoArgsConstructor;
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
}
