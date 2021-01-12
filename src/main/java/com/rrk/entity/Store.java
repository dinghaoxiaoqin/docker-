package com.rrk.entity;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 创建一个仓库实体
 */
public class Store {

    //初始化仓库容量
   // private static final Integer STORE_MAX = 5;
    //仓库存储的内容（书）
  //  private List<Book> list = new ArrayList<>();

    private LinkedBlockingQueue<Book> list = new LinkedBlockingQueue<>(10);

    /**
     * 生产者搬运书到仓库
     */
    public void produce() throws InterruptedException {
        synchronized (list) {
            // while (list.size() +1 > STORE_MAX){
            //仓库已满了，无法装入更多的书  （这个线程就应该等待，直到仓库有存储空间有位置在执行）
//                System.out.println("【生产者" + Thread.currentThread().getName()
//                        + "】仓库已满");
//                try {
//                    //线程等待
//                    list.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            //   }
            Book book = new Book();
            book.setBootName("书" + new Random().nextInt(100));
            list.put(book);
            System.out.println("【生产者" + Thread.currentThread().getName()
                    + "】生产一个产品，现库存" + list.size());
            //唤醒消费者线程
           // list.notifyAll();
            // }
        }
    }


    /**
     * 消费者从仓库运走书
     */
    public void consume(){
        synchronized (list){
           // while (list.size() == 0){
//                //当仓库没有货时，需要等待生产者生产
//                System.out.println("【消费者" + Thread.currentThread().getName()
//                        + "】仓库为空");
//                try {
//                    //消费者等待
//                    list.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
              //  list.remove(list.size()-1);
            try {
                list.take();
                System.out.println("【消费者" + Thread.currentThread().getName()
                        + "】消费一个产品，现库存" + list.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

                //唤醒生产者
               // list.notifyAll();
            }
        }
    //}

}
