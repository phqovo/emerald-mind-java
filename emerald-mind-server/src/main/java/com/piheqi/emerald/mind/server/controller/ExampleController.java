package com.piheqi.emerald.mind.server.controller;


import com.piheqi.emerald.mind.common.constants.AppConstant;
import com.piheqi.emerald.mind.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @Author: PiHeQi
 * @Date: 2023/12/27 09:52
 * @Description: ExampleController
 */
@RestController
@Tag(name = "示例 Controller")
@Slf4j
@RequestMapping(AppConstant.PATH)
public class ExampleController {

    @Operation(summary = "测试方法", description = "测试描述")
    @GetMapping("/example/get")
    public Result<String> get() {
        log.info("info:{}","日志...");
        return new Result<>("获取成功:"+ LocalDateTime.now());
    }
    @Operation(summary = "测试方法", description = "测试描述")
    @GetMapping("/get")
    public Result<String> get1() {
        log.info("info:{}","日志...");
        return new Result<>("获取成功:"+ LocalDateTime.now());
    }
}
