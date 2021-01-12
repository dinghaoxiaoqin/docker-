package com.rrk.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.constants.EsContants;
import com.rrk.dao.OmsOrderMapper;
import com.rrk.dto.OrderDayDto;
import com.rrk.dto.OrderStaticticDto;
import com.rrk.dto.OrderUserDto;
import com.rrk.entity.OmsOrder;
import com.rrk.service.IOmsOrderService;
import com.rrk.utils.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements IOmsOrderService {

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    /**
     * 按照订单品牌统计数据
     *
     * @return
     */
    @Override
    public List<OrderStaticticDto> getStaticticOrders() {
        List<OrderStaticticDto> staticticOrders = new ArrayList<>();
//        //首先从es中获取
        RangeQueryBuilder range = QueryBuilders.rangeQuery("orderstatus").gte(1).lte(3);
        //TermQueryBuilder query = QueryBuilders.termQuery("orderstatus", 3);
        //进行聚合
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("product_brand").field("productbrand.keyword").size(25)
                .subAggregation(AggregationBuilders.sum("sum_amount").field("totalamount"))
                .subAggregation(AggregationBuilders.sum("sum_num").field("productnum"));

        staticticOrders = ElasticsearchUtil.getStaticticOrders(EsContants.ORDER_INDEX, aggregationBuilder, range);
        if (CollUtil.isEmpty(staticticOrders)) {
            staticticOrders = omsOrderMapper.getStaticticOrders();
        }
        return staticticOrders;
    }

    /**
     * 按照天来统计订单数据
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<OrderDayDto> getStatisticsByDay(String startTime, String endTime) {
        List<OrderDayDto> list = new ArrayList<>();
        try {
            // Date start = DateUtil.parse(startTime, "yyyy-MM-dd hh:mm:ss");
            //  Date end = DateUtil.parse(endTime, "yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            //从es获取按照天统计数据
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            List<QueryBuilder> must = builder.must();
            TermQueryBuilder termQuery = QueryBuilders.termQuery("orderstatus", 3);
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("createtime").gte(startTime).lt(endTime).format("yyyy-MM-dd");
            must.add(termQuery);
            must.add(rangeQueryBuilder);
            DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("product_per_day").calendarInterval(new DateHistogramInterval("day")).field("createtime")
                    .subAggregation(AggregationBuilders.sum("sum_amount").field("totalamount"))
                    .subAggregation(AggregationBuilders.sum("sum_num").field("productnum"));
            list = ElasticsearchUtil.getStaticticOrderByDay(EsContants.ORDER_INDEX, aggregationBuilder, builder);
            if (CollUtil.isEmpty(list)) {
                list = omsOrderMapper.getStatisticsByDay(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 按照天统计付费的人数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<OrderUserDto> getTotalPay(String startTime, String endTime) {
        List<OrderUserDto> dtos = new ArrayList<>();
        try {
            Date start = DateUtil.parse(startTime, "yyyy-MM-dd hh:mm:ss");
            Date end = DateUtil.parse(endTime, "yyyy-MM-dd hh:mm:ss");
            //每天的总人数
            List<Map<String, Object>> totalList = omsOrderMapper.getTotalPay(start, end);
            //每天的新人
            List<Map<String, Object>> newList = omsOrderMapper.getTotalNewPay(start, end);
            //老用户
            List<Map<String, Object>> oldList = omsOrderMapper.getTotalOldPay(start, end);
            //数据处理
            dtos = handler(dtos, totalList, newList, oldList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dtos;
    }

    /**
     * 每天用户量统计处理
     *
     * @param dtos
     * @param totalList
     * @param newList
     * @param oldList
     * @return
     */
    private List<OrderUserDto> handler(List<OrderUserDto> dtos, List<Map<String, Object>> totalList, List<Map<String, Object>> newList, List<Map<String, Object>> oldList) {
        if (CollUtil.isNotEmpty(totalList)) {
            for (Map<String, Object> map : totalList) {
                OrderUserDto userDto = new OrderUserDto();
                userDto.setCreateTime(map.get("createTime").toString());
                userDto.setUserTotalCount(Convert.toInt(map.get("userCount")));
                dtos.add(userDto);
            }
        }
        if (CollUtil.isNotEmpty(newList)) {
            dtos.stream()
                    .map(l -> newList.stream().filter(n -> n.get("createTime").toString().equals(l.getCreateTime()))
                            .findFirst().map(n -> {
                                l.setUserNewCount(Convert.toInt(n.get("userCount")));
                                return l;
                            }).orElse(null)).collect(Collectors.toList());
        }
        if (CollUtil.isNotEmpty(oldList)) {
            dtos.stream().map(l -> oldList.stream().filter(o -> o.get("createTime").toString().equals(l.getCreateTime()))
                    .findFirst().map(o -> {
                        l.setUserOldCount(Convert.toInt(o.get("userCount")));
                        return l;
                    }).orElse(null)).collect(Collectors.toList());
        }

//        List<TrendDto> trendDtos = new ArrayList<>();
//        if (CollUtil.isNotEmpty(totalList)) {
//            List<String> createTimes =  totalList.stream().map(t->t.get("createTime").toString()).collect(Collectors.toList());
//            List<Integer> userCount = totalList.stream().map(t -> Convert.toInt(t.get("userCount"))).collect(Collectors.toList());
//            TrendDto trendDto = new TrendDto();
//            trendDto.setName("总人数");
//            trendDto.setUserCounts(userCount);
//            trendDtos.add(trendDto);
//            dto.setCreateTimes(createTimes);
//        }
//        if (CollUtil.isNotEmpty(newList)) {
//            List<Integer> userCount = newList.stream().map(t -> Convert.toInt(t.get("userCount"))).collect(Collectors.toList());
//            TrendDto trendDto = new TrendDto();
//            trendDto.setName("新用户人数");
//            trendDto.setUserCounts(userCount);
//            trendDtos.add(trendDto);
//        }
//        if (CollUtil.isNotEmpty(oldList)) {
//            List<Integer> userCount = oldList.stream().map(t -> Convert.toInt(t.get("userCount"))).collect(Collectors.toList());
//            TrendDto trendDto = new TrendDto();
//            trendDto.setName("老用户人数");
//            trendDto.setUserCounts(userCount);
//            trendDtos.add(trendDto);
//        }
//        dto.setTrendDtos(trendDtos);
        return dtos;
    }
}
