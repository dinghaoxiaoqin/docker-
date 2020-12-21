package com.rrk.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rrk.dto.OrderDayDto;
import com.rrk.dto.OrderStaticticDto;
import com.rrk.entity.OmsOrder;
import com.rrk.entity.Result;
import com.rrk.service.IOmsOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
@RestController
@RequestMapping("/omsOrder")
@Slf4j
@CrossOrigin
public class OmsOrderController {

    @Autowired
    private IOmsOrderService omsOrderService;

    /**
     * 获取订单数据
     */
    @GetMapping(value = "/getList")
    @ApiOperation(value = "获取订单所有数据", httpMethod = "GET")
    public Result getList(@RequestParam(value = "orderNo") String orderNo) {
        IPage page = new Page<OmsOrder>(0, 10);
        IPage iPage = omsOrderService.page(page, new QueryWrapper<OmsOrder>().eq("order_no", orderNo));
        return new Result(200, "操作成功", iPage);
    }

    /**
     * 按照品牌来统计订单销量情况
     */
    @GetMapping(value = "/getStaticticOrders")
    @ApiOperation(value = "按照品牌来统计订单销量情况", httpMethod = "GET")
    public Result getStaticticOrders() {
        List<OrderStaticticDto> list = omsOrderService.getStaticticOrders();
        return new Result(200, "操作成功", list);
    }

    /**
     * 按照天来统计订单销量数据
     */
    @GetMapping(value = "/getStatisticsByDay")
    @ApiOperation(value = "按照天来统计订单量", httpMethod = "GET")
    public Result getStatisticsByDay(@RequestParam(value = "startTime") String startTime,
                                     @RequestParam(value = "endTime") String endTime) {
        List<OrderDayDto> list = omsOrderService.getStatisticsByDay(startTime, endTime);
        return new Result(200, "操作成功", list);

    }

}

