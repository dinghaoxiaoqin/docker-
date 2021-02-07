package com.rrk.test;

import cn.hutool.core.util.StrUtil;
import com.rrk.entity.Consumer;
import com.rrk.entity.Producer;
import com.rrk.entity.Store;
import com.rrk.entity.TbOrder;
import com.rrk.factory.OrderFactory;
import com.rrk.utils.HttpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JdTest {


    @Autowired
    private HttpUtils httpUtils;


    @Test
    public void test01() {
        System.out.println("1111");
    }


    /**
     * 生产者与消费者
     */
    @Test
    public void test02() {
        Store store = new Store();
        // 创建2个生产者
//        for (int i = 0; i < 100 ; i++) {
//           new Thread(new Producer(store)).start();
//        }
//        //创建8个消费者
//        for (int i = 0; i <10  ; i++) {
//            new Thread(new Consumer(store)).start();
//        }

        //Store store = new Store();
//        Thread p1 = new Thread(new Producer(store));
//        Thread p2 = new Thread(new Producer(store));
//        Thread p3 = new Thread(new Producer(store));
//
//        Thread c1 = new Thread(new Consumer(store));
//        Thread c2 = new Thread(new Consumer(store));
//        Thread c3 = new Thread(new Consumer(store));
//
//        p1.start();
//        p2.start();
//        p3.start();
//        c1.start();
//        c2.start();
//        c3.start();
    }

    /**
     * 采用阻塞队列来解决问题
     */
    @Test
    public void test03() throws Exception {
        // 声明一个容量为10的缓存队列
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10);


        Producer producer1 = new Producer(queue);
        Producer producer2 = new Producer(queue);
        Producer producer3 = new Producer(queue);
        Consumer consumer = new Consumer(queue);

        // 借助Executors
        // ExecutorService service = Executors.newCachedThreadPool();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        // 启动线程
        executor.execute(producer1);
        executor.execute(producer2);
        executor.execute(producer3);
        executor.execute(consumer);

        // 执行10s
        Thread.sleep(10 * 1000);
        producer1.stop();
        producer2.stop();
        producer3.stop();

        Thread.sleep(2000);
        // 退出Executor
        executor.shutdown();
    }

    /**
     * countdownlatch的使用
     */
    @Test
    public void test06() {
        // 线程安全的计数器
        AtomicInteger totalRows = new AtomicInteger(0);
        List<Integer> list = new ArrayList<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));

        // 初始化CountDownLatch，大小为3
        CountDownLatch countDownLatch = new CountDownLatch(3);
        List<Integer> list1 = getList1(executor, countDownLatch, totalRows);
        List<Integer> list2 = getList2(executor, countDownLatch, totalRows);
        List<Integer> list3 = getList3(executor, countDownLatch, totalRows);
        //关闭线程池
        executor.shutdown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list.addAll(list1);
        list.addAll(list2);
        list.addAll(list3);
        for (Integer integer : list) {
            System.out.println("数字：" + integer);
        }
        // 打印线程池运行状态
        System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
                executor.getQueue().size() + "，已执行结束的任务数目：" + executor.getCompletedTaskCount());
        System.out.println("集合大小：" + list.size());
        //打印次数
        System.out.println(totalRows.get());

    }

    private List<Integer> getList3(ThreadPoolExecutor executor, CountDownLatch countDownLatch, AtomicInteger totalRows) {
        List<Integer> list = new ArrayList<>();
        executor.execute(() -> {
            for (int i = 61; i < 90; i++) {
                list.add(i);
            }
            countDownLatch.countDown();
            totalRows.incrementAndGet();
            System.out.println("第三个线程执行完成");
        });
        return list;
    }

    private List<Integer> getList2(ThreadPoolExecutor executor, CountDownLatch countDownLatch, AtomicInteger totalRows) {
        List<Integer> list = new ArrayList<>();
        executor.execute(() -> {
            for (int i = 31; i < 60; i++) {
                list.add(i);
            }
            countDownLatch.countDown();
            totalRows.incrementAndGet();
            System.out.println("第二个线程执行完成");
        });
        return list;
    }

    private List<Integer> getList1(ThreadPoolExecutor executor, CountDownLatch countDownLatch, AtomicInteger totalRows) {
        List<Integer> list = new ArrayList<>();
        executor.execute(() -> {
            for (int i = 0; i < 30; i++) {
                list.add(i);
            }
            countDownLatch.countDown();
            totalRows.incrementAndGet();
            System.out.println("第一个线程执行完成");
        });
        return list;
    }


    @Autowired
    private OrderFactory orderFactory;

    @Test
    public void  test07(){
        TbOrder order = new TbOrder();
        order.setOrderNo("2222222");
        order.setSource("11111");
        if (StrUtil.isBlank(TbOrder.Source.getSourceType(order.getSource()))) {
            System.out.println("该来源的订单不存在");
        } else {
            orderFactory.orderHandle(order);
        }
    }


    @Test
    public void  test08(){
        TbOrder order = new TbOrder();
        order.setOrderNo("2222222");
        order.setSource("mini");
        if (StrUtil.isBlank(TbOrder.Source.getSourceType(order.getSource()))) {
            System.out.println("该来源的订单不存在");
        } else {
            orderFactory.orderHandle1(order);
        }
    }

}
