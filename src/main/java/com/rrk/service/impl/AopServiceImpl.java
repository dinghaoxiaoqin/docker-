package com.rrk.service.impl;

import com.rrk.entity.Detail;
import com.rrk.exception.ResultBody;
import com.rrk.handle.LogAnnotation;
import com.rrk.handle.ParamAnnotation;
import com.rrk.service.AopService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AopServiceImpl implements AopService {


    @Override
    public int add(int a, int b) {
        int c = a + b;
        System.out.println("加法运算的结果：" + c);
        return c;
    }

    /**
     * 获取mains
     *
     * @param type
     * @return
     */

    @LogAnnotation(key = "#type", needLog = true)
    @Override
    public List<Detail> getMainList(String type) {
        List<Detail> list = new ArrayList<>();
        Detail detail1 = new Detail();
        detail1.setProNo("11111");
        detail1.setProName("测试1");
        detail1.setProBrand("品牌1");
        detail1.setMainId(1L);
        // int i = 1/0;
        Detail detail2 = new Detail();
        detail2.setProNo("22222");
        detail2.setProName("测试2");
        detail2.setProBrand("品牌2");
        detail2.setMainId(2L);
        list.add(detail1);
        list.add(detail2);
        return list;
    }

    @Override
    @ParamAnnotation
    public ResultBody getParamCheck(String orderNo, Long userId, BigDecimal amount) {

        return ResultBody.success();

    }
}
