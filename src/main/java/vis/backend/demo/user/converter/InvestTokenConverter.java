package vis.backend.demo.user.converter;

import lombok.NoArgsConstructor;
import vis.backend.demo.user.domain.InvestToken;
import vis.backend.demo.user.domain.User;

@NoArgsConstructor
public class InvestTokenConverter {

    public static InvestToken toInvestToken(User user, String accessToken, String tokenExpiration) {
        return InvestToken.builder()
                .user(user)
                .accessToken(accessToken)
                .expireTime(tokenExpiration)
                .build();
    }
}
