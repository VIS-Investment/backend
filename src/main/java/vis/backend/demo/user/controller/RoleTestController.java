package vis.backend.demo.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "권한", description = "ADMIN 권한 테스팅 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/debug")
public class RoleTestController {
    @GetMapping("/roles")
    public List<String> debugRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @GetMapping("/context")
    public String debugSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return "No authentication found";
        }
        return "Principal: " + auth.getPrincipal() + ", Authorities: " + auth.getAuthorities();
    }
}
