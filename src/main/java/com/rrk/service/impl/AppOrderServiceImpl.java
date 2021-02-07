package com.rrk.service.impl;

import com.rrk.entity.TbOrder;
import com.rrk.handle.OrderHandleType;
import com.rrk.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@OrderHandleType(source = "app")
public class AppOrderServiceImpl implements OrderService {
    @Override
    public void handle(TbOrder order) {
        System.out.println("处理app的订单----------------");
    }

    @Override
    public void handle1(TbOrder order) {
        System.out.println("处理app的订单111111111111----------------");
    }
}
