package com.itmo.microservices.shop.common.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String uuid() default "224ec6ce-3fea-11ec-9356-0242ac130003";

    String username() default "TestUser";

    String password() default "TestPassword";

    String[] roles() default {"ACCESS"};
}