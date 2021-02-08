package com.rrk.exception;

/**
 * 自定义业务异常类
 */
public class MyException extends RuntimeException{



    /**
     * 错误码
     */
    protected String code;
    /**
     * 错误信息
     */
    protected String msg;

    public MyException() {
        super();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultCode());
        this.code = errorInfoInterface.getResultCode();
        this.msg = errorInfoInterface.getResultMsg();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getResultCode(), cause);
        this.code = errorInfoInterface.getResultCode();
        this.msg = errorInfoInterface.getResultMsg();
    }

    public MyException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public MyException(String code, String msg) {
        super(code);
        this.code = code;
        this.msg = msg;
    }

    public MyException(String code, String msg, Throwable cause) {
        super(code, cause);
        this.code = code;
        this.msg = msg;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
