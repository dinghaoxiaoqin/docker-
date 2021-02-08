package com.rrk.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rrk.dto.MainDto;
import com.rrk.dto.ProDto;
import com.rrk.entity.Main;
import com.rrk.entity.OpsProduct;
import com.rrk.entity.Result;
import com.rrk.service.IDetailService;
import com.rrk.service.IMainService;
import com.rrk.service.IOpsProductService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
@RestController
@RequestMapping("/opsProduct")
@Slf4j
@CrossOrigin
public class OpsProductController {

    @Autowired
    private IOpsProductService opsProductService;

    @Autowired
    private IMainService mainService;

    @Autowired
    private IDetailService detailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Integer flag = 0;

    private Object o = new Object();

    @Autowired
    private RedissonClient redissonClient;


    @PostMapping(value = "/addProduct")
    @ApiOperation(value = "新增商品", httpMethod = "POST")
    @ResponseBody
    public Result addProduct(@RequestBody OpsProduct opsProduct) {
        //判断是否重复
        OpsProduct productNo = opsProductService.getOne(new QueryWrapper<OpsProduct>().eq("product_no", opsProduct.getProductNo()));
        if (ObjectUtil.isNotNull(productNo)) {
            return new Result(400, "该商品已存在", null);
        }
        boolean save = opsProductService.save(opsProduct);
        if (save) {
            return new Result(200, "添加成功", null);
        } else {
            return new Result(200, "添加失败", null);
        }
    }

    @PostMapping(value = "/addMain")
    @ApiOperation(value = "新增test", httpMethod = "POST")
    @ResponseBody
    public Result addMain(@RequestBody Main main) {
        Main byId = mainService.getById(main.getId());
        if (ObjectUtil.isNull(byId)) {
            main.setCreateTime(new Date());
            boolean save = mainService.save(main);
            redisTemplate.opsForHash().put("main_key",main.getId().toString(),main.getIsSubmit().toString());
        } else {
            byId.setIsSubmit(byId.getIsSubmit()+main.getIsSubmit());
            mainService.updateById(byId);
            Object mainKey = redisTemplate.opsForHash().get("main_key", main.getId().toString());
            if (ObjectUtil.isNotNull(mainKey)) {
                Integer integer = Convert.toInt(mainKey);
                integer = integer +main.getIsSubmit();
                redisTemplate.opsForHash().put("main_key",main.getId().toString(),integer.toString());
            } else {
                redisTemplate.opsForHash().put("main_key",main.getId().toString(),main.getIsSubmit().toString());
            }

        }

        return new Result(200, "添加成功", null);

    }

    /**
     * 更新状态
     */
    @PostMapping(value = "/updateMain")
    @ApiOperation(value = "更新状态", httpMethod = "POST")
    @ResponseBody
    public Result updateMain(@RequestBody MainDto dto) {

            Integer result = mainService.updateMain(dto);
            return new Result(result, "更新结果", null);


    }
/**
 * 添加商品到redis
 */
@PostMapping(value = "/addRedis")
@ApiOperation(value = "添加商品到redis", httpMethod = "POST")
@ResponseBody
public Result addRedis(@RequestBody ProDto proDto){
    try {
        mainService.addRedis(proDto);
        return new Result(200,"添加成功",null);
    } catch (Exception e){
        log.info("前端异常；"+e);
        return new Result(400,"出现异常"+e,null);
    }

}

    /**
     * 模拟下单
     */
    @PostMapping(value = "/addOpsRedis")
    @ApiOperation(value = "模拟下单", httpMethod = "POST")
    @ResponseBody
    public Result addOpsRedis(@RequestBody ProDto proDto) {
        try {
            mainService.addOpsRedis(proDto);
            return new Result(200,"添加成功",null);
        } catch (Exception e){
            log.info("前端异常；"+e);
            return new Result(400,"出现异常:"+e,null);
        }
    }


//    /**
//     * rabbitmq发送消息
//     */
//    @PostMapping(value = "/addRabbit")
//    @ApiOperation(value = "rabbitmq发送消息测试")
//    @ResponseBody
//    public Result addRabbit(@ResponseBody ){
//
//    }




}

