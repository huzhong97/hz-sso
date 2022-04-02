package com.example.hz.sso.core.filter;

import com.example.hz.sso.core.UserModel.SsoUser;
import com.example.hz.sso.core.login.SsoServerLogin;
import com.example.hz.sso.core.util.CONF;
import com.example.hz.sso.core.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        ssoServer = filterConfig.getInitParameter(CONF.SSO_SERVER);
        logout = filterConfig.getInitParameter(CONF.SSO_SERVER_LOGOUT);
        log.info("HzSsoWebFilter init success!");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String url = req.getServletPath();

        //logout
        if (logout != null && logout.trim().length() > 0 && logout.trim().equals(url)) {
            CookieUtil.remove(req, resp, CONF.SSO_SESSIONID);
            String ssoLogoutPath = ssoServer.concat(CONF.SSO_SERVER_LOGOUT);
            resp.sendRedirect(ssoLogoutPath);
            return;
        }

        SsoUser user = SsoServerLogin.loginCheck(req, resp);
        if (user == null) {
            String clientUrl = req.getRequestURL().toString();
            String ssoLoginPath = ssoServer.concat(CONF.SSO_SERVER_LOGIN)
                        + "?" + CONF.REDIRECT_URL + "=" + clientUrl;
            resp.sendRedirect(ssoLoginPath);
            return;
        }


        filterChain.doFilter(req, resp);
        return;
    }
}
