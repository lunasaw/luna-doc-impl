package io.github.lunasaw.session;

/**
 * @author weidian
 * @date 2023/7/13
 */
public interface UserService {
    void login(String username);

    String getSessionId();

}
