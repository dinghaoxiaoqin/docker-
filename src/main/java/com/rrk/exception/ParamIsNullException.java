package com.rrk.exception;

/**
 * 自定义验证参数为空的异常
 */
public class ParamIsNullException extends RuntimeException {

    private final String parameterName;
    private final String parameterType;
    private final String msg;

    public ParamIsNullException(String parameterName, String parameterType, String msg) {
        super();
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return "请求参数类型：" + this.parameterType + "，参数名： \'" + this.parameterName + msg;
    }

    public final String getParameterName() {
        return this.parameterName;
    }

    public final String getParameterType() {
        return this.parameterType;
    }
}
