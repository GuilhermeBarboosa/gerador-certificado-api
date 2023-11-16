package com.guilherme.basicapi.core.cors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private final String ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK = "Access-Control-Allow-Private-Network";
    private final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    private final String ALL_HEADER_TYPES = "Content-Type, User-Agent, Accept, X-Requested-With, Accept-Language, Accept-Encoding, Origin, Referer, Access-Control-Request-Method, Access-Control-Request-Headers, Connection, Pragma, Cache-Control, Authorization, AuthToken, clientid";
    private final String ASTERISK = "*";
    private final String HTTP_ONLY_SECURE_SAME_SITE_STRICT = "HttpOnly;Secure;SameSite=Strict";
    private final String OPTIONS = "OPTIONS";
    private final String POST_GET_PUT_DELETE_OPTIONS_HEAD = "POST, GET, PUT, DELETE, OPTIONS, HEAD";
    private final String SET_COOKIE = "Set-Cookie";
    private final String TRUE = "true";
    private final String THREE_THOUSAND_SIX_HUNDRED = "3600";

    /**
     * Init.
     *
     * @param filterConfig FilterConfig
     * @throws ServletException
     * @see Override
     * @see FilterConfig
     * @see ServletException
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Do filter.
     *
     * @param servletRequest  ServletRequest
     * @param servletResponse ServletResponse
     * @param chain           FilterChain
     * @see ServletRequest
     * @see ServletResponse
     * @see FilterChain
     * @see HttpServletRequest
     * @see HttpServletResponse
     */
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ASTERISK);
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, TRUE);
        response.setHeader(ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK, TRUE);
        response.setHeader(SET_COOKIE, HTTP_ONLY_SECURE_SAME_SITE_STRICT);
        response.setHeader(ACCESS_CONTROL_MAX_AGE, THREE_THOUSAND_SIX_HUNDRED);
        response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, POST_GET_PUT_DELETE_OPTIONS_HEAD);
        response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, ALL_HEADER_TYPES);
        if (request.getMethod().equals(OPTIONS)) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Destroy.
     *
     * @see Override
     */
    @Override
    public void destroy() {
    }
}