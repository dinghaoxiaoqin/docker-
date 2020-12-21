package com.rrk.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.rrk.dto.OrderDayDto;
import com.rrk.dto.OrderStaticticDto;
import com.rrk.entity.EsEntity;
import com.rrk.entity.EsPageEntiry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.WeightedAvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ElasticsearchUtil<T> {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static RestHighLevelClient client;

    /**
     * agg类型聚合函数分析
     *
     * @param indexName
     * @param pageNo
     * @param pageSize
     * @param aggregationBuilder
     * @param sortField
     * @return
     */
    public static Long getAggMatchProduct(String indexName, Integer pageNo, Integer pageSize, WeightedAvgAggregationBuilder aggregationBuilder, QueryBuilder query, String sortField) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        //searchRequest.indices("aggregations_index02");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索超时的时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //排序字段
        if (StringUtils.isNotBlank(sortField)) {
            searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(SortOrder.DESC));
        }
        searchSourceBuilder.size(10);
        searchSourceBuilder.query(query);
        searchSourceBuilder.aggregation(aggregationBuilder);
        //分页处理
//        searchSourceBuilder
//                //设置from确定结果索引的选项以开始搜索。默认为0
//                .from((pageNo - 1) * pageSize)
//                //设置size确定要返回的搜索匹配数的选项。默认为10
//                .size(pageSize);
        //打印的内容可以在kibana上使用
        log.info("执行过程：searchSourceBuilder->{}", searchSourceBuilder);
        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            //搜索结果
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("搜索到的结果：" + searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //一共查到多少条数据
        long totalHits = searchResponse.getHits().getTotalHits().value;
        log.info("搜索的结果编码：resultCode->{}", searchResponse.status().getStatus());
        if (searchResponse.status().getStatus() == 200) {
           Aggregation aggregation = searchResponse.getAggregations().get("weighted_salecount");
            System.out.println("aggregation:" + aggregation);
            Map<String, Object> metaData = aggregation.getMetaData();
            System.out.println(metaData);
            final String name = aggregation.getName();
            final String type = aggregation.getType();
            final List<Aggregation> aggregations = searchResponse.getAggregations().asList();

            //  aggregation.getMetaData()
            System.out.println(name);
            System.out.println(type);

        }
        return null;
    }

    /**
     * 订单按照品牌分类统计
     */
    public static List<OrderStaticticDto> getStaticticOrders(String orderIndex, TermsAggregationBuilder aggregationBuilder, TermQueryBuilder query) {
        List<OrderStaticticDto> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(orderIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        // 绑定条件
        searchSourceBuilder.query(query);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            //搜索结果
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("按照品牌分类订单结果：" + searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (searchResponse.status().getStatus() == 200) {
            ParsedStringTerms aggregation = searchResponse.getAggregations().get("product_brand");
            List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
            if (CollUtil.isNotEmpty(buckets)) {
                for (Terms.Bucket bucket : buckets) {
                    OrderStaticticDto staticticDto = new OrderStaticticDto();
                    staticticDto.setBrand(bucket.getKey().toString());
                    staticticDto.setOrderCount(Convert.toInt(bucket.getDocCount()));
                    ParsedSum sumAmount = bucket.getAggregations().get("sum_amount");
                    staticticDto.setAmount(Convert.toBigDecimal(sumAmount.getValue()));
                    staticticDto.setOrderCount(Convert.toInt(bucket.getDocCount()));
                    ParsedSum sumAgg = bucket.getAggregations().get("sum_num");
                    staticticDto.setNums(Convert.toInt(sumAgg.getValue()));
                    list.add(staticticDto);
                }
            }
        }
        return list;
    }
    /**
     * 按照天来统计订单数据
     * @param orderIndex
     * @param aggregationBuilder
     * @param builder
     * @return
     */
    public static List<OrderDayDto> getStaticticOrderByDay(String orderIndex, DateHistogramAggregationBuilder aggregationBuilder, BoolQueryBuilder builder) {
    List<OrderDayDto> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(orderIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        // 绑定条件
        searchSourceBuilder.query(builder);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            //搜索结果
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("按照天统计查询订单结果：" + searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (searchResponse.status().getStatus() == 200) {

            ParsedDateHistogram aggregation = searchResponse.getAggregations().get("product_per_day");
            List<? extends Histogram.Bucket> buckets = aggregation.getBuckets();
            if (CollUtil.isNotEmpty(buckets)) {
                for (Histogram.Bucket bucket : buckets) {
                    OrderDayDto orderDayDto = new OrderDayDto();
                    orderDayDto.setCreateTime(bucket.getKey().toString());
                    orderDayDto.setOrderCount(Convert.toInt(bucket.getDocCount()));
                    ParsedSum sumAmount = bucket.getAggregations().get("sum_amount");
                    orderDayDto.setAmount(Convert.toBigDecimal(sumAmount.getValue()));
                    orderDayDto.setOrderCount(Convert.toInt(bucket.getDocCount()));
                    ParsedSum sumAgg = bucket.getAggregations().get("sum_num");
                    orderDayDto.setNums(Convert.toInt(sumAgg.getValue()));
                    list.add(orderDayDto);
                }
            }
        }
        return list;
    }

    /**
     * @param
     * @param
     * @param
     * @return
     */


//    public static List<String> searchDataLun(String index, int startPage, int pageSize, FunctionScoreQueryBuilder query, String fields, String sortField, String highlightField) {
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        //设置一个可选的超时，控制允许搜索的时间
//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        // 需要显示的字段，逗号分隔（缺省为全部字段）
//        if (StringUtils.isNotBlank(fields)) {
//            searchSourceBuilder.fetchSource(fields, null);
//        }
//        //排序字段
//        if (StringUtils.isNotBlank(sortField)) {
//            String[] split = sortField.split(",");
//            // searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(SortOrder.DESC));
//            searchSourceBuilder.query(query).sort(split[0],SortOrder.DESC).sort(split[1],SortOrder.DESC);
//        }
//
//        // 设置是否按查询匹配度排序
//        searchSourceBuilder.explain(true);
//        if (startPage <= 0) {
//            startPage = 0;
//        }
//        //浅度分页
//        searchSourceBuilder.query(query);
//        // 分页应用
//        searchSourceBuilder
//                //设置from确定结果索引的选项以开始搜索。默认为0
//                .from((startPage - 1) * pageSize)
//                //设置size确定要返回的搜索匹配数的选项。默认为10
//                .size(pageSize);
//        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
//        // LOGGER.info("\n{}", searchSourceBuilder);
//        // 执行搜索,返回搜索响应信息
//        searchRequest.source(searchSourceBuilder);
//        SearchResponse searchResponse = null;
//        try {
//            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
//        if (searchResponse.status().getStatus() == 200) {
//            List<String> list = new ArrayList<>();
//            // 解析对象
//            // List<EsEntity> sourceList = setSearchResponse(searchResponse, highlightField);
//            for (SearchHit hit : searchResponse.getHits().getHits()) {
//                String sourceAsString = hit.getSourceAsString();
//                UserActionEntity userActionEntity = JSON.parseObject(sourceAsString, UserActionEntity.class);
//                //EsEntity resultMap = getResultMap(searchHit, highlightField);
//                list.add(userActionEntity.getProductName());
//            }
//            return list;
//        }
//
//        return null;
//    }

    public static EsPageEntiry searchNameDataPage(String mysqlToIndex, Integer startPage, Integer pageSize, QueryBuilder query, Integer saleOrder, Integer priceOrder,String highlightField) {
        SearchRequest searchRequest = new SearchRequest(mysqlToIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置一个可选的超时，控制允许搜索的时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //排序字段
        if (saleOrder !=-1) {
            //1 倒序排, 2正序排
            if (saleOrder == 1) {
                searchSourceBuilder.query(query).sort("salecount",SortOrder.DESC);
            } else {
                searchSourceBuilder.query(query).sort("salecount",SortOrder.ASC);
            }
        }
        if (priceOrder !=-1) {
            if (priceOrder == 1) {
                searchSourceBuilder.query(query).sort("saleprice",SortOrder.ASC);
            } else {
                searchSourceBuilder.query(query).sort("saleprice",SortOrder.DESC);
            }
        }
        // 设置是否按查询匹配度排序
        searchSourceBuilder.explain(true);
        if (startPage <= 0) {
            startPage = 0;
        }
        //如果 pageSize是10 那么startPage>9990 (10000-pagesize) 如果 20  那么 >9980 如果 50 那么>9950
        //深度分页  TODO
        if (startPage > (10000 - pageSize)) {
            searchSourceBuilder.query(query);
            searchSourceBuilder
                    // .setScroll(TimeValue.timeValueMinutes(1))
                    .size(10000);
            //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
            // LOGGER.info("\n{}", searchSourceBuilder);
            // 执行搜索,返回搜索响应信息
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = null;
            try {
                searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long totalHits = searchResponse.getHits().getTotalHits().value;
            if (searchResponse.status().getStatus() == 200) {
                //使用scrollId迭代查询
                List<EsEntity> result = disposeScrollResult(searchResponse, highlightField);
                List<EsEntity> sourceList = result.stream().parallel().skip((startPage - 1 - (10000 / pageSize)) * pageSize).limit(pageSize).collect(Collectors.toList());
                return new EsPageEntiry(startPage, pageSize, (int) totalHits, sourceList);
            }
        } else {//浅度分页
            searchSourceBuilder.query(query);
            // 分页应用
            searchSourceBuilder
                    //设置from确定结果索引的选项以开始搜索。默认为0
                    .from((startPage - 1) * pageSize)
                    //设置size确定要返回的搜索匹配数的选项。默认为10
                    .size(pageSize);
            //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
            // LOGGER.info("\n{}", searchSourceBuilder);
            // 执行搜索,返回搜索响应信息
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = null;
            try {
                searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long totalHits = searchResponse.getHits().getTotalHits().value;
            long length = searchResponse.getHits().getHits().length;
            // LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
            if (searchResponse.status().getStatus() == 200) {
                // 解析对象
                List<EsEntity> sourceList = setSearchResponse(searchResponse, highlightField);
                return new EsPageEntiry(startPage, pageSize, (int) totalHits, sourceList);
            }
        }
        return null;
    }




    /**
     * spring容器初始化的时候执行该方法
     */
    @PostConstruct
    public void initClient() {
        client = this.restHighLevelClient;

    }

    /**
     * 数据添加
     *
     * @param content   要增加的数据
     * @param indexName 索引，类似数据库
     * @param id        id
     * @return String
     * @auther: LHL
     */
    public static String addData(XContentBuilder content, String indexName, String id) throws IOException {
        IndexResponse response = null;
        IndexRequest request = new IndexRequest(indexName).id(id).source(content);
        response = client.index(request, RequestOptions.DEFAULT);
        log.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());
        return response.getId();
    }

    /**
     * 数据修改
     *
     * @param content   要修改的数据
     * @param indexName 索引，类似数据库
     * @param id        id
     * @return String
     * @auther: LHL
     */
    public static String updateData(XContentBuilder content, String indexName, String id) throws IOException {
        UpdateResponse response = null;
        UpdateRequest request = new UpdateRequest(indexName, id).doc(content);
        response = client.update(request, RequestOptions.DEFAULT);
        log.info("updateData response status:{},id:{}", response.status().getStatus(), response.getId());
        return response.getId();
    }

    /**
     * 根据条件删除
     *
     * @param builder   要删除的数据  new TermQueryBuilder("userId", userId)
     * @param indexName 索引，类似数据库
     * @return
     * @auther: LHL
     */
    public static void deleteByQuery(String indexName, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除记录
     *
     * @param indexName
     * @param id
     * @auther LHL
     */
    public static void deleteData(String indexName, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        log.info("delete:{}", JSON.toJSONString(response));
    }

    /**
     * 清空记录
     *
     * @param indexName
     * @auther: LHL
     */
    public static void clear(String indexName) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        DeleteResponse response = null;
        try {
            response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("delete: {}", JSON.toJSONString(response));
    }

    /**
     * 使用分词查询  高亮 排序 ,并分页
     *
     * @param index          索引名称
     * @param startPage      当前页
     * @param pageSize       每页显示条数
     * @param query          查询条件
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightField 高亮字段
     * @return 结果
     */
    public static EsPageEntiry searchDataPage(String index, Integer startPage, Integer pageSize, QueryBuilder query, String fields, String sortField, String highlightField) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置一个可选的超时，控制允许搜索的时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 需要显示的字段，逗号分隔（缺省为全部字段）
        if (StringUtils.isNotBlank(fields)) {
            searchSourceBuilder.fetchSource(fields, null);
        }
        //排序字段
        if (StringUtils.isNotBlank(sortField)) {
            if (sortField.contains(",")) {
                String[] split = sortField.split(",");
                searchSourceBuilder.query(query).sort(split[0],SortOrder.DESC).sort(split[1],SortOrder.DESC);
            } else {
                searchSourceBuilder.query(query).sort(sortField,SortOrder.DESC);
            }
        }
        // 高亮（xxx=111,aaa=222）
        if (StringUtils.isNotBlank(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置前缀
            highlightBuilder.preTags("<span style='color:red' >");
            //设置后缀
            highlightBuilder.postTags("</span>");
            String[] split = highlightField.split(",");
            if (split.length > 0) {
                for (String s : split) {
                    HighlightBuilder.Field highlight = new HighlightBuilder.Field(s);
                    //荧光笔类型
                    highlight.highlighterType("unified");
                    //TODO
                    highlight.fragmentSize(150);
                    //从第3个分片开始获取高亮片段
                    highlight.numOfFragments(3);
                    // 设置高亮字段
                    highlightBuilder.field(highlight);
                }
            }
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        // 设置是否按查询匹配度排序
        searchSourceBuilder.explain(true);
        if (startPage <= 0) {
            startPage = 0;
        }
        //如果 pageSize是10 那么startPage>9990 (10000-pagesize) 如果 20  那么 >9980 如果 50 那么>9950
        //深度分页  TODO
        if (startPage > (10000 - pageSize)) {
            searchSourceBuilder.query(query);
            searchSourceBuilder
                    // .setScroll(TimeValue.timeValueMinutes(1))
                    .size(10000);
            //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
            // LOGGER.info("\n{}", searchSourceBuilder);
            // 执行搜索,返回搜索响应信息
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = null;
            try {
                searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long totalHits = searchResponse.getHits().getTotalHits().value;
            if (searchResponse.status().getStatus() == 200) {
                //使用scrollId迭代查询
                List<EsEntity> result = disposeScrollResult(searchResponse, highlightField);
                List<EsEntity> sourceList = result.stream().parallel().skip((startPage - 1 - (10000 / pageSize)) * pageSize).limit(pageSize).collect(Collectors.toList());
                return new EsPageEntiry(startPage, pageSize, (int) totalHits, sourceList);
            }
        } else {//浅度分页
            searchSourceBuilder.query(query);
            // 分页应用
            searchSourceBuilder
                    //设置from确定结果索引的选项以开始搜索。默认为0
                    .from((startPage - 1) * pageSize)
                    //设置size确定要返回的搜索匹配数的选项。默认为10
                    .size(pageSize);
            //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
            // LOGGER.info("\n{}", searchSourceBuilder);
            // 执行搜索,返回搜索响应信息
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = null;
            try {
                searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long totalHits = searchResponse.getHits().getTotalHits().value;
            long length = searchResponse.getHits().getHits().length;
            // LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
            if (searchResponse.status().getStatus() == 200) {
                // 解析对象
                List<EsEntity> sourceList = setSearchResponse(searchResponse, highlightField);
                return new EsPageEntiry(startPage, pageSize, (int) totalHits, sourceList);
            }
        }
        return null;
    }

    public static EsPageEntiry getMatchProduct(String indexName, Integer pageNo, Integer pageSize, QueryBuilder query, String sortField) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索超时的时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //排序字段
        if (StringUtils.isNotBlank(sortField)) {
            searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(SortOrder.DESC));
        }
        searchSourceBuilder.query(query);
        //分页处理
        searchSourceBuilder
                //设置from确定结果索引的选项以开始搜索。默认为0
                .from((pageNo - 1) * pageSize)
                //设置size确定要返回的搜索匹配数的选项。默认为10
                .size(pageSize);
        //打印的内容可以在kibana上使用
        log.info("执行过程：searchSourceBuilder->{}", searchSourceBuilder);
        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            //搜索结果
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
           e.printStackTrace();
        }
        //一共查到多少条数据
        long totalHits = searchResponse.getHits().getTotalHits().value;
        log.info("搜索的结果编码：resultCode->{}", searchResponse.status().getStatus());
        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            List<EsEntity> sourceList = setSearchResponse(searchResponse, null);
            return new EsPageEntiry(pageNo, pageSize, (int) totalHits, sourceList);
        }
        return null;
    }

    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse 搜索的结果集
     * @param highlightField 高亮字段
     */
    private static List<EsEntity> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<EsEntity> sourceList = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            EsEntity resultMap = getResultMap(searchHit, highlightField);
            sourceList.add(resultMap);
        }
        return sourceList;
    }

    /**
     * 获取高亮结果集
     *
     * @param: [hit, highlightField]
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @auther: LHL
     */
    private static EsEntity getResultMap(SearchHit hit, String highlightField) {
        String sourceAsString = hit.getSourceAsString();
        EsEntity esProductEntity = JSON.parseObject(sourceAsString, EsEntity.class);
        esProductEntity.setId(Convert.toLong(hit.getId()));
        //  hit.getSourceAsMap().put("id", hit.getId());
        if (StringUtils.isNotBlank(highlightField)) {
            String[] split = highlightField.split(",");
            if (split.length > 0) {
                for (String str : split) {
                    HighlightField field = hit.getHighlightFields().get(str);
                    if (null != field) {
                        Text[] text = field.getFragments();
                        String hightStr = null;
                        //TODO
                        if (text != null) {
                            StringBuffer stringBuffer = new StringBuffer();
                            for (Text st : text) {
                                String s = st.string();
                                //s.replaceAll("<p>", "<span>").replaceAll("</p>", "</span>");
                                stringBuffer.append(s);
                            }
                            hightStr = stringBuffer.toString();
                            //遍历 高亮结果集，覆盖 正常结果集
                            //hit.getSourceAsMap().put(str, hightStr);
                            esProductEntity.setHighLight(hightStr);
                        }
                    }
                }
            }
        }
        return esProductEntity;
    }

    public static <T> List<T> search(String index, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理scroll结果
     *
     * @param: [response, highlightField]
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @auther: LHL
     */
    private static List<EsEntity> disposeScrollResult(SearchResponse response, String highlightField) {
        List<EsEntity> sourceList = new ArrayList<>();
        //使用scrollId迭代查询
        while (response.getHits().getHits().length > 0) {
            String scrollId = response.getScrollId();
            try {
                response = client.scroll(new SearchScrollRequest(scrollId), RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits.getHits()) {
                EsEntity resultMap = getResultMap(hit, highlightField);
                sourceList.add(resultMap);
            }
        }
        ClearScrollRequest request = new ClearScrollRequest();
        request.addScrollId(response.getScrollId());
        try {
            client.clearScroll(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourceList;
    }

//    public static List<SuggesterDto> searchSuggester(String index, Integer startPage, Integer pageSize, QueryBuilder query, String fields, String sortField, String highlightField) {
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        //设置一个可选的超时，控制允许搜索的时间
//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        // 需要显示的字段，逗号分隔（缺省为全部字段）
//        if (StringUtils.isNotBlank(fields)) {
//            searchSourceBuilder.fetchSource(fields, null);
//        }
//        //排序字段
//        if (StringUtils.isNotBlank(sortField)) {
//            searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(SortOrder.DESC));
//        }
//        // 高亮（xxx=111,aaa=222）
//        if (StringUtils.isNotBlank(highlightField)) {
//            HighlightBuilder highlightBuilder = new HighlightBuilder();
//            //设置前缀
//            highlightBuilder.preTags("<span style='color:red' >");
//            //设置后缀
//            highlightBuilder.postTags("</span>");
//            String[] split = highlightField.split(",");
//            if (split.length > 0) {
//                for (String s : split) {
//                    HighlightBuilder.Field highlight = new HighlightBuilder.Field(s);
//                    //荧光笔类型
//                    highlight.highlighterType("unified");
//                    //TODO
//                    highlight.fragmentSize(150);
//                    //从第3个分片开始获取高亮片段
//                    highlight.numOfFragments(3);
//                    // 设置高亮字段
//                    highlightBuilder.field(highlight);
//                }
//            }
//            searchSourceBuilder.highlighter(highlightBuilder);
//        }
//        // 设置是否按查询匹配度排序
//        searchSourceBuilder.explain(true);
//        if (startPage <= 0) {
//            startPage = 0;
//        }
//        searchSourceBuilder.query(query);
//        // 分页应用
//        searchSourceBuilder
//                //设置from确定结果索引的选项以开始搜索。默认为0
//                .from((startPage - 1) * pageSize)
//                //设置size确定要返回的搜索匹配数的选项。默认为10
//                .size(pageSize);
//        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
//        // LOGGER.info("\n{}", searchSourceBuilder);
//        // 执行搜索,返回搜索响应信息
//        searchRequest.source(searchSourceBuilder);
//        SearchResponse searchResponse = null;
//        try {
//            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        long totalHits = searchResponse.getHits().getTotalHits().value;
//        long length = searchResponse.getHits().getHits().length;
//        // LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
//        if (searchResponse.status().getStatus() == 200) {
//            // 解析对象
//            List<SuggesterDto> sourceList = setSearchSuggestData(searchResponse, highlightField);
//            return sourceList;
//        }
//        return null;
//    }
//
//    private static List<SuggesterDto> setSearchSuggestData(SearchResponse searchResponse, String highlightField) {
//        List<SuggesterDto> sourceList = new ArrayList<>();
//        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
//            SuggesterDto resultMap = getResultSugger(searchHit, highlightField);
//            sourceList.add(resultMap);
//        }
//        return sourceList;
//    }
//
//    private static SuggesterDto getResultSugger(SearchHit hit, String highlightField) {
//        String sourceAsString = hit.getSourceAsString();
//        SuggesterDto esProductEntity = JSON.parseObject(sourceAsString, SuggesterDto.class);
//
//        if (StringUtils.isNotBlank(highlightField)) {
//            String hightStr = "<span style='color:#0080FF'>" + highlightField + "</span>";
//            if (esProductEntity.getPrename().contains(highlightField)) {
//                String name = esProductEntity.getPrename().replaceAll(highlightField,hightStr);
//                esProductEntity.setPrename(name);
//            }
//        }
//        return esProductEntity;
//    }
}
