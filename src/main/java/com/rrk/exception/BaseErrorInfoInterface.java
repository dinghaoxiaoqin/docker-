package com.rrk.exception;

/**
 * 此接口用于返回码枚举使用
 */
public interface BaseErrorInfoInterface {

    /** 错误码*/
    String getResultCode();

    /** 错误描述*/
    String getResultMsg();
}
