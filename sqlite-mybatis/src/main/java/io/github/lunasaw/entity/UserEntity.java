package io.github.lunasaw.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author weidian
 */
@Getter
@Setter
@ToString
public class UserEntity {
    private int    id;
    private String userName;
    private String password;
    private int    start;
    private String foundTime;
}