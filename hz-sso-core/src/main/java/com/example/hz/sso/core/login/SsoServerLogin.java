package com.example.hz.sso.core.login;

import com.example.hz.sso.core.UserModel.SsoUser;
import com.example.hz.sso.core.util.CONF;
import com.example.hz.sso.core.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author huzhong
 * @date 2022/4/1
 * @apiNote 服务端登录/登出
 */
public class SsoServerLogin {

    /**
     * 用户在sso服务端登录了,将user存入redis,并将userId存入cookie的值中
     * @param response
     * @param cookieValue
     * @param user
     */
    public static void login(HttpServletResponse response,
                             String cookieValue,
                             SsoUser user) {
        String userId = user.getUserId();
        SsoServerStore.put(userId, user);
        CookieUtil.set(response, CONF.SSO_SESSIONID, cookieValue);
    }

    /**
     * 用户登出,将user从redis删除,并移除cookie
     * @param request
     * @param response
     */
    public static void logout(HttpServletRequest request,
                              HttpServletResponse response) {
        String cookieValue = CookieUtil.getValue(request, CONF.SSO_SESSIONID);
        if (cookieValue == null) {
            return;
        }
        String userId = SsoLoginUtil.getUserIdFromCookieValue(cookieValue);
        if (userId != null) {
            SsoServerStore.remove(userId);
        }
        CookieUtil.remove(request, response, CONF.SSO_SESSIONID);
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    public static SsoUser loginCheck(HttpServletRequest request, HttpServletResponse response) {
        String cookieValue = CookieUtil.getValue(request, CONF.SSO_SESSIONID);

        String userId = SsoLoginUtil.getUserIdFromCookieValue(cookieValue);

        SsoUser user = getUserByUserId(userId);
        if (user != null) {
            return user;
        }
        //从sso服务端重定向回客户端，此时客户端顶级域名下还没有cookie
        CookieUtil.remove(request, response, CONF.SSO_SESSIONID);
        userId = SsoLoginUtil.getUserIdFromCookieValue(request.getParameter(CONF.SSO_SESSIONID));
        user = getUserByUserId(userId);

        if (user != null) {
            //在客户端顶级域名下存入cookie
            CookieUtil.set(response, CONF.SSO_SESSIONID, SsoLoginUtil.makeCookieValue(user));
            return user;
        }
        return null;
    }

    /**
     *
     * @param userId
     * @return
     */
    private static SsoUser getUserByUserId(String userId) {
        if (userId == null) {
            return null;
        }
        SsoUser user = SsoServerStore.get(userId);
        return user;
    }
}


