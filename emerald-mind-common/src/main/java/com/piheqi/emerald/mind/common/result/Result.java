package com.piheqi.emerald.mind.common.result;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Result<T> {

    @Schema(name = "返回响应码")
    private int code;

    @Schema(name = "返回响应信息")
    private String msg;

    @Schema(name = "返回响应数据")
    private T data;

    public Result(T t) {
        this.data = t;
        this.code = 200;
        this.msg = "操作成功";
    }
}
