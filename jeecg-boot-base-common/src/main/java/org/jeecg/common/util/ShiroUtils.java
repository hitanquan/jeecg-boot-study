package org.jeecg.common.util;

import org.jeecg.common.system.vo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;

/**
 * shiro 工具类
 *
 * @author jiangjun
 */
@Slf4j
public class ShiroUtils {

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static boolean hasRole(String role) {
        return getSubject().hasRole(role);
    }

    public static boolean[] hasRoles(List<String> roles) {
        return getSubject().hasRoles(roles);
    }

    public static boolean hasAllRoles(Collection<String> roles) {
        return getSubject().hasAllRoles(roles);
    }

    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static void logout() {
        getSubject().logout();
    }

    public static LoginUser getUser() {
        LoginUser user = null;
        Object obj = getSubject().getPrincipal();
        if (oConvertUtils.isNotEmpty(obj)) {
            user = new LoginUser();
            try {
                BeanUtils.copyProperties(obj, user);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("failed to copy current login user from obj!");
            }
        }
        return user;
    }

    public static void setUser(LoginUser user) {
        Subject subject = getSubject();
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user, realmName);
        // 重新加载Principal
        subject.runAs(newPrincipalCollection);
    }

    public static String getUserId() {
        return getUser().getId();
    }

    public static String getLoginName() {
        LoginUser user = getUser();
        if (user == null) {
            log.error("获取登录用户信息失败，您可能没有登录。");
            return null;
        }
        return user.getUsername();
    }

    public static String getIp() {
        return getSubject().getSession().getHost();
    }

    public static String getSessionId() {
        return String.valueOf(getSubject().getSession().getId());
    }

    /**
     * 生成随机盐
     */
    public static String randomSalt() {
        // 一个Byte占两个字节，此处生成的3字节，字符串长度为6
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        String hex = secureRandom.nextBytes(3).toHex();
        return hex;
    }
}
