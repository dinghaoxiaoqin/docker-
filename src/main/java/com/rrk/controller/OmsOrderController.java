package com.rrk.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rrk.dto.OrderDayDto;
import com.rrk.dto.OrderStaticticDto;
import com.rrk.dto.OrderUserDto;
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
        //对数据进行处理
//        OrderBrandDto brandDto = new OrderBrandDto();
//        List<String> brands = new ArrayList<>();
//        List<Integer> counts = new ArrayList<>();
//        List<Integer> nums = new ArrayList<>();
//        List<BigDecimal> amounts = new ArrayList<>();
//        for (OrderStaticticDto dto : list) {
//           brands.add(dto.getBrand());
//           counts.add(dto.getOrderCount());
//           nums.add(dto.getNums());
//           amounts.add(dto.getAmount());
//        }
//        brandDto.setBrandName(brands);
//        brandDto.setOrderCount(counts);
//        brandDto.setNums(nums);
//        brandDto.setAmount(amounts);
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

    /**
     * 每日付费人数（总人数，新人，老用户人数）
     */
    @GetMapping(value = "/getTotalPay")
    @ApiOperation(value = "每日付费的总人数（记得要去重）",httpMethod = "GET")
    public Result getTotalPay(@RequestParam(value = "startTime") String startTime,
                              @RequestParam(value = "endTime") String endTime){
        List<OrderUserDto> dtos = omsOrderService.getTotalPay(startTime, endTime);
        return new Result(200, "操作成功", dtos);
    }

}

