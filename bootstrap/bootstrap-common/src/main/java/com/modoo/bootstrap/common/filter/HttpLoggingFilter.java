package com.modoo.bootstrap.common.filter;

import com.modoo.common.util.HttpLogAction;
import com.modoo.common.util.LogUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class HttpLoggingFilter extends OncePerRequestFilter {

  private static final String TRACE_ID = "traceId";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String traceId = UUID.randomUUID()
        .toString();
    long startMs = System.currentTimeMillis();
    String userAgent = request.getHeader("User-Agent");

    MDC.put(TRACE_ID, traceId);

    log.info(
        "{} method={} uri={} query={} contentLength={} contentType={} user-agent={}",
        HttpLogAction.REQ.prefix(),
        request.getMethod(),
        request.getRequestURI(),
        request.getQueryString(),
        request.getContentLength(),
        request.getContentType(),
        userAgent
    );

    try {
      filterChain.doFilter(request, response);
    } finally {
      long duration = System.currentTimeMillis() - startMs;
      String prefix = HttpLogAction.RES.prefix();

      if (LogUtil.isSlow(duration)) {
        prefix = HttpLogAction.SLOW.prefix();
      }

      if (LogUtil.isErrRes(response.getStatus())) {
        prefix = HttpLogAction.ERR.prefix();
      }

      log.info(
          "{} method={} uri={} status={} durationMs={} user-agent={}",
          prefix,
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          duration,
          userAgent
      );

      MDC.clear();
    }
  }

}
