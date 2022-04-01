package com.example.hz.sso.core.UserModel;

import java.io.Serializable;

/**
 * @author huzhong
 * @date 2022/3/30
 * @apiNote
 */
public class SsoUser implements Serializable {
    private static final long serialVersionUID = 42L;

    private String userId;
    private String userName;

    private String version;
    private int expireMinutes;
    private long expireFreshTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getExpireMinutes() {
        return expireMinutes;
    }

    public void setExpireMinutes(int expireMinutes) {
        this.expireMinutes = expireMinutes;
    }

    public long getExpireFreshTime() {
        return expireFreshTime;
    }

    public void setExpireFreshTime(long expireFreshTime) {
        this.expireFreshTime = expireFreshTime;
    }
}
