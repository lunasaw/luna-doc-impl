package io.github.lunasaw;

import io.github.lunasaw.entity.UserEntity;
import io.github.lunasaw.mapper.IUserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author weidian
 * @date 2023/9/3
 */
@SpringBootTest(classes = Main.class)
public class AppTest {

    @Autowired
    private IUserDao iUserDao;

    @Test
    public void atest() {
        List<UserEntity> userAll = iUserDao.findUserAll();
        System.out.println(userAll);
    }
}
