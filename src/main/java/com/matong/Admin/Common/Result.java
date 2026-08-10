package com.matong.Admin.Common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public static<T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setCode(200);
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.code = 200;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String msg, String paramErrorMsg, String message){
        Result<T> result = new Result<T>();
        result.code = 500;
        result.msg = msg;
        return result;
    }

}
