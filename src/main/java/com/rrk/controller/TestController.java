package com.rrk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(value = "/testA")
    public String testA(){
        return "第一次采用dockerFile制作jar的镜像，多一点支持哈";
    }

    @GetMapping(value = "/testB")
    public String testB(){
        return "-----------------------testB";
    }
}
