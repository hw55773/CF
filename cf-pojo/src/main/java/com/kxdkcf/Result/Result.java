package com.kxdkcf.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.Result
 * Author:              wenhao
 * CreateTime:          2024-12-27  14:25
 * Description:         TODO
 * Version:             1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Integer code;// 0表示失败，1表示成功
    private String msg;//返回信息
    private Object data;//返回数据

    public static Result success() {
        return new Result(1, "success", null);
    }

    public static Result success(Object data) {
        return new Result(1, "success", data);
    }

    public static Result error(String msg) {
        return new Result(0, msg, null);
    }

}
