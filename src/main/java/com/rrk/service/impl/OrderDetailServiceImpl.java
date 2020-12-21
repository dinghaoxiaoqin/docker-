package com.rrk.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.dao.OrderDetailMapper;
import com.rrk.entity.OrderDetail;
import com.rrk.service.IOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单商品表 服务实现类
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {

}
