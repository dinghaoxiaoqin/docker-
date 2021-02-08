package com.rrk.exception;

public class ResultBody {

    /**
     * 错误编码
     */
    private String code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private Object data;

    public ResultBody() {
    }


    /**
     * 成功
     *
     * @return
     */
    public static ResultBody success() {
        return success(null);
    }

    /**
     * 成功
     * @param data
     * @return
     */
    public static ResultBody success(Object data) {
        ResultBody rb = new ResultBody();
        rb.setCode(CommonEnum.SUCCESS.getResultCode());
        rb.setMsg(CommonEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        return rb;
    }

    /**
     * 业务失败异常
     * @param code
     * @param msg
     * @return
     */
    public static ResultBody error(String code, String msg) {
        ResultBody body = new ResultBody();
        body.setCode(code);
        body.setMsg(msg);
        body.setData(null);
        return body;

    }

    public static ResultBody error(BaseErrorInfoInterface errorInfo) {
        ResultBody rb = new ResultBody();
        rb.setCode(errorInfo.getResultCode());
        rb.setMsg(errorInfo.getResultMsg());
        rb.setData(null);
        return rb;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
