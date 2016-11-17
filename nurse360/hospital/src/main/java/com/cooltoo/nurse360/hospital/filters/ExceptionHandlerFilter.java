package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zhaoyi0113 on 13/11/2016.
 */
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("exception handler filter");
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            BadRequestException badRequestException = null;
            if (e instanceof BadRequestException) {
                badRequestException = (BadRequestException) e;

            } else if (e instanceof ServletException && ((ServletException) e).getCause() instanceof BadRequestException) {
                badRequestException = (BadRequestException) e.getCause();
            }
            // custom error response class used across my project
            int errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            if (badRequestException != null) {
                errorCode = badRequestException.getErrorCode().getCode();
            }
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
            response.getWriter().close();
            response.setStatus(errorCode);
        }
    }
}
