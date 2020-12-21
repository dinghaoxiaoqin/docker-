package com.rrk.test;

import com.rrk.dao.CitiesMapper;
import com.rrk.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JdTest {


    @Autowired
    private HttpUtils httpUtils;


    @Test
    public void test01() {
        System.out.println("1111");
    }


    @Test
    public void testJd() {

        //  声明需要解析的初始地址
        String url = "https://fd17.xyz/code/JJGDBN";
        //  遍历页面对手机的搜索进行遍历结果
        for (int i = 1; i < 10; i = i + 2) {
            String html = this.httpUtils.doGetHtml(url);
            //  解析页面，获取商品数据并存储
            if (html != null) {
                Elements spuEles = this.parse(html);
                System.out.println("获取京东的数据：" + spuEles);
            }

        }
        System.out.println("手机数据抓取完成！！！");

    }


    /**
     * 解析页面，获取商品数据并存储
     *
     * @param html
     */
    private Elements parse(String html) {
        //  解析HTML获取Document
        Document doc = Jsoup.parse(html);
        //  获取spu
        Elements spuEles = doc.select("div#J_goodsList > ul > li");

//
//        //        //  遍历获取spu数据
//        for (Element spuEle : spuEles) {
//            String attr = spuEle.attr("data-spu");
//            long spu = Long.parseLong(attr.equals("") ? "0" : attr);
////            //  获取spu
////            String attr = spuEle.attr("data-spu");
////            long spu = Long.parseLong(attr.equals("") ? "0" : attr);
////            //  获取sku信息
////            Elements skuEles = spuEle.select("li.ps-item");
////            for (Element skuEle : skuEles) {
////                //  获取sku
////                long sku = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));
////                //  根据sku查询商品数据
////                Item item = new Item();
////                item.setSku(sku);
////                List<Item> list = this.itemService.findAll(item);
////                if (list.size() > 0) {
////                    //如果商品存在，就进行下一个循环，该商品不保存，因为已存在
////                    continue;
////                }
////                //  设置商品的spu
////                item.setSpu(spu);
////
////                //  获取商品的详情信息
////                String itemUrl = "https://item.jd.com/" + sku + ".html";
////                item.setUrl(itemUrl);
////
////                //  商品图片
////                String picUrl = skuEle.select("img[data-sku]").first().attr("data-lazy-img");
////                //	图片路径可能会为空的情况
////                if (!StringUtils.isNotBlank(picUrl)) {
////                    picUrl = skuEle.select("img[data-sku]").first().attr("data-lazy-img-slave");
////                }
////                picUrl = "https:" + picUrl.replace("/n7/", "/n1/");    //	替换图片格式
////                String picName = this.httpUtils.doGetImage(picUrl);
////                item.setPic(picName);
////
////                //  商品价格
////                String priceJson = this.httpUtils.doGetHtml("https://p.3.cn/prices/mgets?skuIds=J_" + sku);
////                double price = MAPPER.readTree(priceJson).get(0).get("p").asDouble();
////                item.setPrice(price);
////
////                //  商品标题
////                String itemInfo = this.httpUtils.doGetHtml(item.getUrl());
////                String title = Jsoup.parse(itemInfo).select("div.sku-name").text();
////                item.setTitle(title);
////
////                //  商品创建时间
////                item.setCreated(new Date());
////                //  商品修改时间
////                item.setUpdated(item.getCreated());
////
////                //  保存商品数据到数据库中
////                this.itemService.save(item);
//        }
        return spuEles;
    }


    @Autowired
    private CitiesMapper citiesMapper;

//    @Test
//    public void test06(){
//        RegionDto regionDto = citiesMapper.getCity(9);
//        System.out.println(regionDto);
//    }
}
