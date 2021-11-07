package com.itmo.microservices.shop.common.security;

import com.itmo.microservices.shop.user.impl.service.JwtTokenManager;
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth;
import lombok.AllArgsConstructor;
import org.assertj.core.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    private final JwtTokenManager tokenManager;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        var roles = new ArrayList<SimpleGrantedAuthority>();
        for (var i = 0; i < customUser.roles().length; i++) {
            roles.add(new SimpleGrantedAuthority(customUser.roles()[i]));
        }
        UserAuth user = new UserAuth(
                UUID.fromString(customUser.uuid()),
                customUser.username(),
                customUser.password(),
                roles
        );

        Authentication auth =
                new UsernamePasswordAuthenticationToken(user, tokenManager.generateToken(user), user.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }

    public static List<Object> generateDefaultRoleString() {
        return Arrays.asList("ACCESS");
    }
}