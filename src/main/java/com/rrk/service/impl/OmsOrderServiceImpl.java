package com.rrk.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.constants.EsContants;
import com.rrk.dao.OmsOrderMapper;
import com.rrk.dto.*;
import com.rrk.entity.OmsOrder;
import com.rrk.service.IOmsOrderService;
import com.rrk.utils.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketSelectorPipelineAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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
             //Date start = DateUtil.parse(startTime, "yyyy-MM-dd hh:mm:ss");
            // Date end = DateUtil.parse(endTime, "yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            //从es获取按照天统计数据
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            List<QueryBuilder> must = builder.must();
            TermQueryBuilder termQuery = QueryBuilders.termQuery("orderstatus", 3);
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("createtime").gte(start).lt(end);
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
        //线程安全计数器
        long begin = System.currentTimeMillis();
        AtomicInteger totalTh = new AtomicInteger(0);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 5, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(512));
        CountDownLatch latch = new CountDownLatch(3);
        List<OrderUserDto> dtos = new ArrayList<>();
        try {
            Date start = DateUtil.parse(startTime, "yyyy-MM-dd hh:mm:ss");
            Date end = DateUtil.parse(endTime, "yyyy-MM-dd hh:mm:ss");
            //每天的总人数
            List<Map<String, Object>> totalList = getTotalCount(executor, latch, totalTh, start, end);
            //  List<Map<String, Object>> totalList = omsOrderMapper.getTotalPay(start, end);
            //每天的新人
            List<Map<String, Object>> newList = getNewList(executor,latch,totalTh,start,end);
            //List<Map<String, Object>> newList = omsOrderMapper.getTotalNewPay(start, end);
            //老用户
            List<Map<String, Object>> oldList = getOldList(executor,latch,totalTh,start,end);

           // List<Map<String, Object>> oldList = omsOrderMapper.getTotalOldPay(start, end);
            //关闭线程池

            latch.await();
            //所有子线程都执行完成，主线程执行
            //log.info("线程池运行情况："+totalTh.get());
            dtos = handler(dtos, totalList, newList, oldList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            System.out.println("线程池关闭");
        }
        long last = System.currentTimeMillis();
        System.out.println("用时-----------------------------：" + (last - begin));
        return dtos;
    }

    /**
     * 新用户按照时间统计首单购买的商品信息
     * @param startTime
     * @param endTime
     * @param productBrand
     * @return
     */
    @Override
    public List<OrderNewProductDto> getNewOrderPro(Integer pageNo,Integer pageSize,String startTime, String endTime, String productBrand) throws ParseException {
        long begin = System.currentTimeMillis();
        //从es中获取相应数据
        List<OrderNewProductDto> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse(startTime);
        Date end = sdf.parse(endTime);
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        RangeQueryBuilder statusBuilder = QueryBuilders.rangeQuery("orderstatus").gte(1).lte(3);
        RangeQueryBuilder timeBuilder = QueryBuilders.rangeQuery("createtime").lte(end);
        bool.must(statusBuilder);
        bool.must(timeBuilder);
        //进行聚合分析
        TermsAggregationBuilder builder = AggregationBuilders.terms("user_name_term").field("username").size(1000)
                .order(BucketOrder.aggregation("user_name_count", true))
                .subAggregation(AggregationBuilders.count("user_name_count").field("username"));
        //类似having的脚本
        //声明BucketPath，用于后面的bucket筛选
        Map<String, String> bucketsPathsMap = new HashMap<>(8);
        bucketsPathsMap.put("user_name_count", "user_name_count");
        Script script = new Script("params.user_name_count ==1");
        //构建bucket选择器
        BucketSelectorPipelineAggregationBuilder bs =
                PipelineAggregatorBuilders.bucketSelector("user_name_having", bucketsPathsMap, script);
        builder.subAggregation(bs);
        List<String> userNames = ElasticsearchUtil.getUserNames(EsContants.ORDER_INDEX,bool,builder);
        if (CollUtil.isNotEmpty(userNames)) {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            RangeQueryBuilder orderstatus = QueryBuilders.rangeQuery("orderstatus").gte(1).lte(3);
            RangeQueryBuilder createtime = QueryBuilders.rangeQuery("createtime").lte(end);
            TermQueryBuilder query = QueryBuilders.termQuery("username",userNames);
            boolQuery.must(orderstatus);
            boolQuery.must(createtime);
            boolQuery.must(query);
            list =  ElasticsearchUtil.getOrderProduct(EsContants.ORDER_INDEX,boolQuery,pageNo,pageSize);
        }
        //List<OrderNewProductDto> list = omsOrderMapper.getNewOrderPro(start,end,productBrand);
        long last = System.currentTimeMillis();
        System.out.println("用的时间："+(last-begin));
        return list;
    }

    /**
     * 各个省份下品牌销量前10的品牌数据
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<ProvinceDto> getProvinceBrand(String startTime, String endTime) {
        List<ProvinceDto> list = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            BoolQueryBuilder bool = QueryBuilders.boolQuery();
            List<QueryBuilder> must = bool.must();
            RangeQueryBuilder statusBuilder = QueryBuilders.rangeQuery("orderstatus").gte(1).lte(3);
            RangeQueryBuilder timeBuilder = QueryBuilders.rangeQuery("createtime").lte(end);
            must.add(statusBuilder);
            must.add(timeBuilder);
            TermsAggregationBuilder term = AggregationBuilders.terms("province_term").field("receiverprovince.keyword").size(32);
            TermsAggregationBuilder builder = AggregationBuilders.terms("brand_term").field("productbrand.keyword").size(10)
                    .order(BucketOrder.aggregation("brand_count", false))
                    .subAggregation(AggregationBuilders.sum("brand_count").field("productnum"));
            term.subAggregation(builder);
            list =  ElasticsearchUtil.getProvinceBrand(EsContants.ORDER_INDEX,bool,term,list);
        } catch (Exception e){
            log.error("各个省份下品牌销量前10的品牌异常：e->{}",e);
        }

        return list;
    }

    private List<Map<String,Object>> getOldList(ThreadPoolExecutor executor, CountDownLatch latch, AtomicInteger totalTh, Date start, Date end) {
        List<Map<String, Object>> list1 = new ArrayList<>();
        executor.execute(() -> {
            List<Map<String, Object>> list = omsOrderMapper.getTotalOldPay(start, end);
            list1.addAll(list);
            //System.out.println("第三个线程执行完成"+list.size());
            latch.countDown();

        });
        //计数
        totalTh.incrementAndGet();

        return list1;
    }

    private List<Map<String,Object>> getNewList(ThreadPoolExecutor executor, CountDownLatch latch, AtomicInteger totalTh, Date start, Date end) {
        List<Map<String, Object>> list1 = new ArrayList<>();
        executor.execute(() -> {
            List<Map<String, Object>> list = omsOrderMapper.getTotalNewPay(start, end);
            list1.addAll(list);
            latch.countDown();
            //System.out.println("第二个线程执行完成"+list.size());

        });
        //计数
        totalTh.incrementAndGet();

        return list1;
    }

    private List<Map<String, Object>> getTotalCount(ThreadPoolExecutor executor, CountDownLatch latch, AtomicInteger totalTh, Date start, Date end) {
        List<Map<String, Object>> list1 = new ArrayList<>();
        executor.execute(() -> {
            List<Map<String, Object>> list = omsOrderMapper.getTotalPay(start, end);
            list1.addAll(list);
            latch.countDown();
            //System.out.println("第一个线程执行完成"+list.size());

        });
        //计数
        totalTh.incrementAndGet();
        return list1;
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
