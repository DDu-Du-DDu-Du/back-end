package com.ddudu.api.user.auth.resolver;

import com.ddudu.api.user.auth.jwt.AuthorityProxy;
import com.ddudu.bootstrap.common.annotation.Login;
import com.ddudu.common.exception.AuthErrorCode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean hasLoginAnnotation = Objects.nonNull(parameter.getParameterAnnotation(Login.class));
    boolean hasLongType = parameter.getParameterType()
        .equals(Long.class);

    return hasLoginAnnotation && hasLongType;
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory
  ) {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();

    // TODO: 불필요한 Resolver 대신 @AuthenticationPrincipal 고려 필요

    if (isGuest(authentication)) {
      return null;
    }

    if (isMember(authentication)) {
      return authentication.getPrincipal();
    }

    throw new UnsupportedOperationException(AuthErrorCode.BAD_TOKEN_CONTENT.getCodeName());
  }

  private boolean isGuest(Authentication authentication) {
    if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
      return false;
    }

    return authentication.getAuthorities()
        .contains(AuthorityProxy.GUEST);
  }

  private boolean isMember(Authentication authentication) {
    if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
      return false;
    }

    List<AuthorityProxy> memberAuthorities = List.of(AuthorityProxy.NORMAL, AuthorityProxy.ADMIN);

    return !Collections.disjoint(authentication.getAuthorities(), memberAuthorities);
  }

}
