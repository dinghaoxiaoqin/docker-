package com.rrk.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rrk.dto.OrderDayDto;
import com.rrk.dto.OrderStaticticDto;
import com.rrk.dto.OrderUserDto;
import com.rrk.entity.OmsOrder;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
public interface IOmsOrderService extends IService<OmsOrder> {

    List<OrderStaticticDto> getStaticticOrders();

    List<OrderDayDto> getStatisticsByDay(String startTime, String endTime);

    List<OrderUserDto> getTotalPay(String startTime, String endTime);
}
