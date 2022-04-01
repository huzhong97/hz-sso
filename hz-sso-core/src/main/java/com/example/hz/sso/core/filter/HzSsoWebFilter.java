package com.example.hz.sso.core.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;


/**
 * 2022年3月30日15点44分
 * 2022年4月1日
 */
@Slf4j
public class HzSsoWebFilter extends HttpServlet implements Filter {

    private String ssoServer;
    private String logout;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }
}
