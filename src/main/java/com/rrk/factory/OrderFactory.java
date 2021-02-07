package com.rrk.factory;

import com.rrk.entity.TbOrder;
import com.rrk.handle.OrderHandleType;
import com.rrk.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderFactory {

    private Map<String,OrderService> orderServiceMap;

    @Autowired
    public void setOrderServiceMap(List<OrderService> orderServices){
      //注入各种类型的订单处理类
        orderServiceMap = orderServices.stream().collect(Collectors.toMap(
                o -> AnnotationUtils.findAnnotation(o.getClass(),OrderHandleType.class).source(),
                v->v,(v1,v2)->v1));
    }

    public void orderHandle(TbOrder order) {
        // ...一些前置处理

        // 通过订单来源确定对应的handler
        OrderService orderService = orderServiceMap.get(order.getSource());
        orderService.handle(order);
       // orderService.handle1(order);

        // ...一些后置处理
    }


    public void orderHandle1(TbOrder order) {
        // ...一些前置处理

        // 通过订单来源确定对应的handler
        OrderService orderService = orderServiceMap.get(order.getSource());
        //orderService.handle(order);
        orderService.handle1(order);

        // ...一些后置处理
    }
}
