package com.rrk.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.constants.EsContants;
import com.rrk.dao.OmsOrderMapper;
import com.rrk.dto.OrderDayDto;
import com.rrk.dto.OrderStaticticDto;
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
        //首先从es中获取
        TermQueryBuilder query = QueryBuilders.termQuery("orderstatus", 3);
        //进行聚合
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("product_brand").field("productbrand.keyword")
                .subAggregation(AggregationBuilders.sum("sum_amount").field("totalamount"))
                .subAggregation(AggregationBuilders.sum("sum_num").field("productnum"));

        staticticOrders = ElasticsearchUtil.getStaticticOrders(EsContants.ORDER_INDEX, aggregationBuilder, query);
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
    public List<OrderDayDto> getStatisticsByDay(String startTime, String endTime)  {
        List<OrderDayDto> list = new ArrayList<>();
        try {
            // Date start = DateUtil.parse(startTime, "yyyy-MM-dd hh:mm:ss");
            //  Date end = DateUtil.parse(endTime, "yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start  = sdf.parse(startTime);
            Date end  = sdf.parse(endTime);
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
