package com.rrk.controller;

import com.rrk.exception.ResultBody;
import com.rrk.service.AopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/aop")
@Slf4j
@CrossOrigin
public class AopController {

    @Autowired
    private AopService aopService;

    @GetMapping(value = "/add")
    public ResultBody add(){
        String orderNo = "";
        Long userId = 2233L;
        BigDecimal amount = BigDecimal.ZERO;
        ResultBody body = aopService.getParamCheck(orderNo,userId,amount);
        return body;
    }
}
