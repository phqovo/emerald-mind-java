package com.piheqi.emerald.mind.gpt.controller;

import com.piheqi.emerald.mind.common.redis.RedisUtil;
import com.piheqi.emerald.mind.gpt.manager.UserAccountManager;
import com.piheqi.emerald.mind.gpt.model.from.UserLoginRequest;
import com.piheqi.emerald.mind.gpt.model.result.UserLoginResult;
import com.piheqi.emerald.mind.common.constants.AppConstant;
import com.piheqi.emerald.mind.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: PiHeQi
 * @Date: 2024/1/8 16:45
 * @Description: UserAccountController
 */
@RestController
@RequestMapping(AppConstant.CHAT + "/user")
public class UserAccountController {

    @Autowired
    private UserAccountManager userAccountManager;
    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/login")
    @Operation(tags = "用户登录", description = "用户登录")
    public Result<UserLoginResult> login(@Valid @RequestBody UserLoginRequest request) {
        redisUtil.set("sss","ddd");
        return new Result<>(new UserLoginResult());
    }

}
