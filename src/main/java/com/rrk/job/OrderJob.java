//package com.rrk.job;
//
//import cn.hutool.core.util.NumberUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.rrk.dao.CitiesMapper;
//import com.rrk.entity.OmsOrder;
//import com.rrk.entity.OpsProduct;
//import com.rrk.entity.OrderDetail;
//import com.rrk.service.IOmsOrderService;
//import com.rrk.service.IOpsProductService;
//import com.rrk.service.IOrderDetailService;
//import com.rrk.utils.NumberUtils;
//import com.xxl.job.core.biz.model.ReturnT;
//import com.xxl.job.core.handler.IJobHandler;
//import com.xxl.job.core.handler.annotation.JobHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//@Component
//@JobHandler(value = "OrderJob")
//@Slf4j
//public class OrderJob extends IJobHandler {
//
//    @Autowired
//    private IOmsOrderService omsOrderService;
//
//    @Autowired
//    private IOrderDetailService orderDetailService;
//
//    @Autowired
//    private IOpsProductService opsProductService;
//
//    @Autowired
//    private CitiesMapper citiesMapper;
//
//    /**
//     * 自动生成订单
//     */
//    @Override
//    public ReturnT<String> execute(String s) throws Exception {
//        log.info("-----------------------------自动生成订单开始执行------------------------");
//        List<OmsOrder> omsOrders = new ArrayList<>();
//        List<OrderDetail> orderDetails = new ArrayList<>();
//        for (int i = 0; i <300 ; i++) {
//            //随机生成商品id
//            OmsOrder omsOrder;
//            OrderDetail orderDetail;
//            BigDecimal amount = BigDecimal.ZERO;
//            omsOrder = new OmsOrder();
//            omsOrder.setOrderNo(NumberUtils.GetRandomString(10));
//            omsOrder.setAutoConfirmDay(7);
//            omsOrder.setCreateTime(new Date());
//            omsOrder.setUserName(NumberUtils.generateName());
//            omsOrder.setSourceType(0);
//            omsOrder.setReceiverName(NumberUtils.generateName());
//            //获取省市信息
//            int cityId = new Random().nextInt(344)+2;
//            Map<String,String> map  = citiesMapper.getCity(cityId);
//            omsOrder.setReceiverPhone(NumberUtils.GetRandomString(11));
//            omsOrder.setReceiverProvince(map.get("provinceName"));
//            omsOrder.setReceiverCity(map.get("cityName"));
//            //各种状态
//            int orderStatus = new Random().nextInt(6);
//            omsOrder.setOrderStatus(orderStatus);
//            omsOrder.setOrderType(0);
//            int payType = new Random().nextInt(2)+1;
//            if (orderStatus == 0) {
//                //待支付
//                omsOrder.setPayType(0);
//                omsOrder.setConfirmStatus(0);
//                omsOrder.setDeleteStatus(0);
//            } else if(orderStatus == 1){
//                //待发货
//                omsOrder.setPaymentTime(new Date());
//                omsOrder.setPayType(payType);
//                omsOrder.setConfirmStatus(0);
//                omsOrder.setDeleteStatus(0);
//            } else if(orderStatus == 2){
//                //已发货
//                omsOrder.setPaymentTime(new Date());
//                omsOrder.setPayType(payType);
//                omsOrder.setConfirmStatus(0);
//                omsOrder.setDeleteStatus(0);
//                omsOrder.setDeliveryTime(new Date());
//            } else if(orderStatus == 3){
//                //已完成
//                omsOrder.setPaymentTime(new Date());
//                omsOrder.setPayType(payType);
//                omsOrder.setConfirmStatus(1);
//                omsOrder.setDeleteStatus(0);
//                omsOrder.setDeliveryTime(new Date());
//                omsOrder.setReceiveTime(new Date());
//            } else if(orderStatus == 4){
//                //已关闭
//                omsOrder.setPaymentTime(new Date());
//                omsOrder.setPayType(payType);
//                omsOrder.setConfirmStatus(0);
//                omsOrder.setDeleteStatus(0);
//            } else if(orderStatus == 5){
//                //无效
//                omsOrder.setPaymentTime(new Date());
//                omsOrder.setPayType(payType);
//                omsOrder.setConfirmStatus(0);
//                omsOrder.setDeleteStatus(1);
//            }
//            int num = new Random().nextInt(3)+1;
//            for (int j = 0; j <num ; j++) {
//                int productId = new Random().nextInt(2000) + 180000;
//                OpsProduct opsProduct = opsProductService.getById(productId);
//                if (ObjectUtil.isNotNull(opsProduct)) {
//                    amount =  NumberUtil.add(amount,opsProduct.getAmount());
//                    //订单详情
//                    orderDetail = new OrderDetail();
//                    orderDetail.setOrderNo(omsOrder.getOrderNo());
//                    orderDetail.setProductId(opsProduct.getId());
//                    orderDetail.setProductBrand(opsProduct.getProductBrand());
//                    orderDetail.setProductName(opsProduct.getProductName());
//                    orderDetail.setProductNum(5);
//                    orderDetail.setProductPrice(opsProduct.getProductPrice());
//                    orderDetail.setProductSkuId(opsProduct.getId());
//                    orderDetail.setProductSrc(opsProduct.getProductSrc());
//                    orderDetail.setRealAmount(opsProduct.getAmount());
//                    //修改商品信息
//                    if (opsProduct.getStock() -orderDetail.getProductNum() > 0) {
//                        opsProduct.setSaleCount(opsProduct.getSaleCount()+orderDetail.getProductNum());
//                        opsProduct.setStock(opsProduct.getStock() -orderDetail.getProductNum());
//                    }
//                    orderDetails.add(orderDetail);
//                    //opsProductService.updateById(opsProduct);
//                }
//
//            }
//            //商品金额
//            omsOrder.setFreightAmount(amount);
//            omsOrder.setPayAmount(amount);
//            omsOrder.setTotalAmount(amount);
//            omsOrders.add(omsOrder);
//        }
//
//        omsOrderService.saveBatch(omsOrders);
//        orderDetailService.saveBatch(orderDetails);
//        log.info("----------------------------自动生成订单结束-----------------------");
//        return ReturnT.SUCCESS;
//    }
//
////    public static void main(String[] args) {
////        int i = new Random().nextInt(1000) + 2000;
////        System.out.println(i);
////        String numbet = NumberUtils.GetRandomString(10);
////        System.out.println("num:" + numbet);
////        String randomJianHan = NumberUtils.generateName();
////        System.out.println("姓名:" + randomJianHan);
////    }
//
//
//}
