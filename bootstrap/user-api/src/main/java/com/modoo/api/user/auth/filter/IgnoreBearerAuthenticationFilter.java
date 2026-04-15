package com.modoo.api.user.auth.filter;

import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class IgnoreBearerAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER = "Bearer";
  private static final String IGNORE = "Ignore";
  private static final RequestMatcher REFRESH_REQUEST_MATCHER =
      new AntPathRequestMatcher("/api/auth/token", HttpMethod.POST.name());

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (isIgnorable(request) && token != null && token.startsWith(BEARER)) {
      MutableRequest mutableRequest = new MutableRequest(request);

      mutableRequest.setHeader(HttpHeaders.AUTHORIZATION, token.replace(BEARER, IGNORE));
      filterChain.doFilter(mutableRequest, response);
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private boolean isIgnorable(HttpServletRequest request) {
    return REFRESH_REQUEST_MATCHER.matches(request);
  }

  private class MutableRequest extends HttpServletRequestWrapper {

    private final Map<String, String> customHeader;

    public MutableRequest(HttpServletRequest request) {
      super(request);
      this.customHeader = Maps.newHashMap();
    }

    public void setHeader(String name, String value) {
      this.customHeader.put(name, value);
    }

    @Override
    public String getHeader(String name) {
      if (this.customHeader.containsKey(name)) {
        return this.customHeader.get(name);
      }

      return ((HttpServletRequest) this.getRequest()).getHeader(name);
    }

  }

}
