package com.example.hz.sso.core.login;

import com.example.hz.sso.core.UserModel.SsoUser;

/**
 * @author huzhong
 * @date 2022/4/1
 * @apiNote
 */
public class SsoLoginUtil {

    /**
     * 生成cookie的值
     * @param user
     * @return
     */
    public static String makeCookieValue(SsoUser user) {
        return user.getUserId().concat("#").concat(user.getVersion());
    }

    /**
     * 从cookie的值里面取出当前的userId
     * @param cookieValue
     * @return
     */
    public static String getUserIdFromCookieValue(String cookieValue) {
        if (cookieValue != null && cookieValue.contains("#")) {
            String[] strings = cookieValue.split("#");
            if (strings.length == 2
                    && strings[0] != null
                    && strings[0].trim().length() > 0) {
                return strings[0].trim();
            }
        }
        return null;
    }

    public static String getUserVersionFromCookieValue(String cookieValue) {
        if (cookieValue != null && cookieValue.contains("#")) {
            String[] strings = cookieValue.split("#");
            if (strings.length == 2
                    && strings[1] != null
                    && strings[1].trim().length() > 0) {
                return strings[1].trim();
            }
        }
        return null;
    }
}
