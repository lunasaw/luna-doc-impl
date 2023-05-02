package io.github.lunasaw.log.service;

import io.github.lunasaw.log.entity.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

/**
 * @author luna
 * @date 2023/5/2
 */
@Service("userService")
@Slf4j
public class UserService {


    public Long add(UserDO userDO) {
        return RandomUtils.nextLong();
    }

    public void delete(UserDO userDO) {
    }

    public void update(UserDO userDO) {
    }
}
