package com.ddudu.common.annotation;

import static com.ddudu.auth.exception.AuthErrorCode.INVALID_AUTHENTICATION;

import com.ddudu.auth.jwt.JwtAuthToken;
import com.ddudu.common.exception.InvalidAuthenticationException;
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
    boolean hasLoginAnnotation = parameter.getParameterAnnotation(Login.class) != null;
    boolean hasLongType = parameter.getParameterType()
        .equals(Long.class);

    return hasLoginAnnotation && hasLongType;
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory
  ) throws InvalidAuthenticationException {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();

    if (authentication != null && authentication instanceof JwtAuthToken) {
      JwtAuthToken jwtAuthToken = (JwtAuthToken) authentication;
      return jwtAuthToken.getUserId();
    }

    throw new InvalidAuthenticationException(INVALID_AUTHENTICATION);
  }

}
