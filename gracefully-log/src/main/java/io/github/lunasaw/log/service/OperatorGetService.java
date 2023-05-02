package io.github.lunasaw.log.service;


/**
 * @author luna
 */
public interface OperatorGetService {

    /**
     * 可以在里面外部的获取当前登陆的用户
     *
     * @return String 操作用户名称
     */
    String getUser();
}