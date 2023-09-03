package io.github.lunasaw.mapper;

import io.github.lunasaw.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (D_Users)表数据库访问层
 */
@Mapper
public interface IUserDao {
    UserEntity findUserById(int id);

    List<UserEntity> findUserAll();
}