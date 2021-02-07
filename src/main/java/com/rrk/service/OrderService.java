package com.rrk.service;

import com.rrk.entity.TbOrder;

public interface OrderService {

    void handle(TbOrder order);

    void handle1(TbOrder order);
}
