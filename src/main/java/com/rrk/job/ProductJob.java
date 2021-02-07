//package com.rrk.job;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.convert.Convert;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rrk.entity.OpsProduct;
//import com.rrk.service.IOpsProductService;
//import com.rrk.utils.HttpUtils;
//import com.xxl.job.core.biz.model.ReturnT;
//import com.xxl.job.core.handler.IJobHandler;
//import com.xxl.job.core.handler.annotation.JobHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
///**
// * 商品定时任务
// */
//@Component
//@JobHandler(value = "ProductJob")
//@Slf4j
//public class ProductJob extends IJobHandler {
//
//
//
//
//    private final static String PRODUCT_URL = "https://search.jd.com/Search?keyword=海尔&wq=海尔&s=104&click=1&page=";
//
//
//    @Autowired
//    private HttpUtils httpUtils;
//
//    private ObjectMapper MAPPER =  new ObjectMapper();
//    @Autowired
//    private IOpsProductService opsProductService;
//
//    @Override
//    public ReturnT<String> execute(String s) throws Exception {
//        log.info("拉取京东商品列表的定时器执行了-----------------------");
//        //PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//        // cm.setMaxTotal(100);
//        // cm.setDefaultMaxPerRoute(10);
//        //  CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
//        // Integer i =
//        Integer i = new Random(0).nextInt(10);
//        Integer j = 20+new Random().nextInt(50);
//        for (i = 1; i < 10; i = i +1) {
//            String html = httpUtils.doGetHtml(PRODUCT_URL+1);
//            if (html != null) {
//                this.parse(html);
//                //System.out.println("获取京东的数据："+spuEles);
//            }
//
//        }
//        log.info("拉取京东商品列表的执行器执行完成--------------------");
//        return ReturnT.SUCCESS;
//    }
//
//    private void parse(String html) throws Exception {
//        //  解析HTML获取Document
//        Document doc = Jsoup.parse(html);
//        Elements spuEles = doc.select("div#J_goodsList > ul > li");
//        //TbSku sku = null;
//        List<OpsProduct> list = new ArrayList<>();
//        for (Element spuEle : spuEles) {
//            String attr = spuEle.attr("data-spu");
//            long spuId = Long.parseLong(attr.equals("")?"0":attr);
//            //  获取sku信息
//            Elements skuEles = spuEle.select("li.ps-item");
//            for (Element skuEle : skuEles) {
//                long skuId = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));
//
//                //  商品图片
//                String picUrl = skuEle.select("img[data-sku]").first().attr("data-lazy-img");
//                //	图片路径可能会为空的情况
//                if(!StrUtil.isNotBlank(picUrl)){
//                    picUrl =skuEle.select("img[data-sku]").first().attr("data-lazy-img-slave");
//                }
//                picUrl ="https:"+picUrl.replace("/n7/","/n1/");	//	替换图片格式
//                String picName = this.httpUtils.doGetImage(picUrl);
//                //  商品价格
//                String priceJson = this.httpUtils.doGetHtml("https://p.3.cn/prices/mgets?skuIds=J_" + skuId);
//                double price = MAPPER.readTree(priceJson).get(0).get("p").asDouble();
//                //图片
//                String itemUrl = "https://item.jd.com/"+skuId+".html";
//                //  商品标题
//                String itemInfo = this.httpUtils.doGetHtml(itemUrl);
//                String title = Jsoup.parse(itemInfo).select("div.sku-name").text();
//                System.out.println("商品名称："+title);
//                // item.setTitle(title);
//                //String imageName = downloadImage(imgUrl);
//                //  获取sku
//                OpsProduct ops = opsProductService.getOne(new QueryWrapper<OpsProduct>().eq("id", skuId));
//                if (ObjectUtil.isNull(ops)) {
//                    if (!StrUtil.isBlank(title)&& !StrUtil.isBlank(picName)) {
//                        ops = new OpsProduct();
//                        ops.setId(skuId);
//                        ops.setAmount(Convert.toBigDecimal(price));
//                        ops.setProductBrand("海尔");
//                        ops.setProductName(title);
//                        ops.setProductPrice(Convert.toBigDecimal(price));
//                        ops.setProductNo("ops"+new Random(4).nextInt(9));
//                        ops.setProductSrc(picName);
//                        ops.setStock(1000);
//                        ops.setSaleCount(0);
//                        list.add(ops);
//                    }
//
//                }
//                //log.info("获取的商品数据："+list);
//                if (CollUtil.isNotEmpty(list)) {
//                    opsProductService.saveBatch(list);
//                }
//            }
//
//        }
//    }
//}
