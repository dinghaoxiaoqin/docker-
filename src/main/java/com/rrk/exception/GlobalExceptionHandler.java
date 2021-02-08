package com.rrk.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = MyException.class)
    @ResponseBody
    public ResultBody bizExceptionHandler(HttpServletRequest req, MyException e){
        log.error("发生业务异常！原因是：e->{}",e.getMsg());
        return ResultBody.error(e.getCode(),e.getMsg());
    }


    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest req, NullPointerException e){
        log.error("发生空指针异常！原因是:e->{}",e);
        return ResultBody.error(CommonEnum.BODY_NOT_MATCH);
    }

    /**
     * 处理请求方法不支持的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultBody exceptionHandler2(HttpServletRequest req, HttpRequestMethodNotSupportedException e){
        log.error("发生请求方法不支持异常！原因是:",e);
        return ResultBody.error(CommonEnum.REQUEST_METHOD_SUPPORT_ERROR);
    }


    /**
     * 处理请求方法不支持的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = {ParamIsNullException.class,MissingServletRequestParameterException.class})
    @ResponseBody
    public ResultBody exceptionHandler3(HttpServletRequest req, Exception  e){
        log.error("参数为空！原因是:",e);
        return ResultBody.error(CommonEnum.SIGNATURE_NOT_MATCH.getResultCode(),e.getMessage());
    }




    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest req, Exception e){
        log.error("未知异常！原因是:",e);
        return ResultBody.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }
}
