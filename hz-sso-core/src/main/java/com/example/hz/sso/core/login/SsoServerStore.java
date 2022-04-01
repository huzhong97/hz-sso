package com.example.hz.sso.core.login;

import com.example.hz.sso.core.UserModel.SsoUser;
import com.example.hz.sso.core.util.CONF;
import com.example.hz.sso.core.util.JedisUtil;

/**
 * @author huzhong
 * @date 2022/4/1
 * @apiNote 服务端登录状态存储
 */
public class SsoServerStore {
    //24h
    private static int redisExpireMinute = 1440;
    public static void setRedisExpireMinute(int expireMinute) {
        if (expireMinute < 60) {
            expireMinute = 60;
        }
        SsoServerStore.redisExpireMinute = expireMinute;
    }
    public static int getRedisExpireMinute() {
        return SsoServerStore.redisExpireMinute;
    }

    /**
     * 获取服务端redis里存储的已登录用户
     * @param userId
     * @return
     */
    public static SsoUser get(String userId) {
        String redisKey = makeRedisKey(userId);
        Object obj = JedisUtil.getObj(redisKey);
        if (obj != null){
            SsoUser user = (SsoUser) obj;
            return user;
        }
        return null;
    }

    /**
     * 删除服务端redis里存储的已登录用户
     * @param userId
     */
    public static void remove(String userId) {
        String redisKey = makeRedisKey(userId);
        JedisUtil.del(redisKey);
    }

    public static void put(String userId, SsoUser user) {
        String redisKey = makeRedisKey(userId);
        // 保存登陆状态24h
        JedisUtil.setObj(redisKey, user, redisExpireMinute * 60);
    }

    private static String makeRedisKey(String userId) {
        return CONF.SSO_SESSIONID.concat("#").concat(userId);
    }


}
