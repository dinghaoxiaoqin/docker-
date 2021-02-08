package com.rrk.service;

import com.rrk.entity.Detail;
import com.rrk.exception.ResultBody;

import java.math.BigDecimal;
import java.util.List;

public interface AopService {
    int add(int a, int b);

    List<Detail> getMainList(String type);

    ResultBody getParamCheck(String orderNo, Long userId, BigDecimal amount);
}
