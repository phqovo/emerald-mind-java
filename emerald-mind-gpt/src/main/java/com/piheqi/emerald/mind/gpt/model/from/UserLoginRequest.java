package com.piheqi.emerald.mind.gpt.model.from;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: PiHeQi
 * @Date: 2024/1/8 16:47
 * @Description: UserLoginRequest
 */
@Data
@Schema(name = "用户登录请求")
public class UserLoginRequest {

    @Schema(title = "账户名")
    private String account;

    @Schema(title = "密码")
    private String password;

    @Schema(title = "邀请码")
    private String inviteCode;

}
