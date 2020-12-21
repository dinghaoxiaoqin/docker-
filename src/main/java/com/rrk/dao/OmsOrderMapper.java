package com.rrk.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rrk.dto.OrderDayDto;
import com.rrk.dto.OrderStaticticDto;
import com.rrk.entity.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
public interface OmsOrderMapper extends BaseMapper<OmsOrder> {

    List<OrderStaticticDto> getStaticticOrders();

    List<OrderDayDto> getStatisticsByDay(@Param("start") Date start, @Param("end") Date end);
}
