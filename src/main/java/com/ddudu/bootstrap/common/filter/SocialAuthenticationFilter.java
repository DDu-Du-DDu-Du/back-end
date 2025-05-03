package com.ddudu.bootstrap.common.filter;

import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SocialAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER = "Bearer";
  private static final String IGNORE = "Ignore";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
  ) throws ServletException, IOException {
    String requestURI = request.getRequestURI();
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (requestURI.contains("login") && token != null && token.startsWith(BEARER)) {
      MutableRequest mutableRequest = new MutableRequest(request);

      mutableRequest.setHeader(HttpHeaders.AUTHORIZATION, token.replace(BEARER, IGNORE));

      filterChain.doFilter(mutableRequest, response);
    } else {
      filterChain.doFilter(request, response);
    }
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
