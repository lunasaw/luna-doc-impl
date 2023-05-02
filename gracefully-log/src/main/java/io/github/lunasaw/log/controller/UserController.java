package io.github.lunasaw.log.controller;

import com.google.common.collect.ImmutableMap;
import com.luna.common.dto.ResultDTO;
import com.luna.common.dto.ResultDTOUtils;
import io.github.lunasaw.log.anno.LogRecord;
import io.github.lunasaw.log.aspect.ExpressionEvaluatorContext;
import io.github.lunasaw.log.entity.UserDO;
import io.github.lunasaw.log.entity.UserReq;
import io.github.lunasaw.log.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author luna
 * @date 2023/5/1
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;


    @LogRecord(
            success = "#req.operateName + '-添加用户-' +  #req.userName + '-成功'",
            bizNo = "#req.userId",
            operator = "#req.operateName",
            category = "'用户管理'",
            fail = "#req.operateName + '-添加用户-' +  #req.userName + '-失败'"
    )
    @GetMapping("/add")
    public ResultDTO<Long> add(@RequestBody UserReq req) {
        return ResultDTOUtils.success(userService.add(req2do(req)));
    }

    @LogRecord(
            success = "#req.operateName + '-' + #operateLevel?.#operateLevel  + '-删除用户-' +  #req.userName + '-成功'",
            bizNo = "#req.userId",
            operator = "#req.operateName",
            category = "'用户管理'",
            fail = "#req.operateName + '-删除用户-' +  #req.userName + '-失败'"
    )
    @GetMapping("/delete")
    public ResultDTO<Void> delete(@RequestBody UserReq req) {
        ExpressionEvaluatorContext.putVariable(ImmutableMap.of("operateLevel", "高级"));
        userService.delete(req2do(req));
        return ResultDTOUtils.success();
    }

    public static UserDO req2do(UserReq req) {
        UserDO userDO = new UserDO();
        userDO.setUserName(req.getUserName());
        userDO.setUserId(req.getUserId());
        return userDO;
    }
}
