package com.ddudu.api.user.auth.resolver;

import com.ddudu.api.user.auth.jwt.JwtAuthToken;
import com.ddudu.bootstrap.common.annotation.Login;
import com.ddudu.common.exception.AuthErrorCode;
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
      MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory
  ) {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();

    if (authentication instanceof JwtAuthToken jwtAuthToken) {
      return jwtAuthToken.getUserId();
    }

    throw new UnsupportedOperationException(AuthErrorCode.BAD_TOKEN_CONTENT.getCodeName());
  }

}
