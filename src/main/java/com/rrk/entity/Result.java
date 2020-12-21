package com.rrk.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {

    private Integer code;

    private String msg;

    private Object data;

    public Result(){

    }

    public Result(Integer code,String msg,Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;

    }
}
