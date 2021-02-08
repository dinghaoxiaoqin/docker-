package com.rrk.service.impl;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.dao.MainMapper;
import com.rrk.dto.MainDto;
import com.rrk.dto.ProDto;
import com.rrk.entity.Detail;
import com.rrk.entity.Main;
import com.rrk.entity.OpsProduct;
import com.rrk.service.IDetailService;
import com.rrk.service.IMainService;
import com.rrk.service.IOpsProductService;
import com.rrk.utils.RabbitmqUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dinghao
 * @since 2021-01-15
 */
@Service
@Slf4j
public class MainServiceImpl extends ServiceImpl<MainMapper, Main> implements IMainService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private IMainService mainService;

    @Autowired
    private IDetailService detailService;

    @Autowired
    private RabbitmqUtils rabbitmqUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IOpsProductService opsProductService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateMain(MainDto dto) {
        //synchronized (o) {
       // RLock lock = redissonClient.getLock(dto.getMainId().toString());

            // 防止正在请求过程再发起请求
          //  lock.lock(5, TimeUnit.SECONDS);
            Main main = mainService.getOne(new QueryWrapper<Main>().eq("id", dto.getMainId()));
            if (ObjectUtil.isNull(main)) {
                return 1;
            }
            if (main.getIsSubmit() == 1) {
                return 2;
            }

            main.setIsSubmit(1);
            mainService.updateById(main);
            //新增
            Detail detail = new Detail();
            detail.setCreateTime(new Date());
            detail.setMainId(main.getId());
            detail.setProBrand(dto.getProBrand());
            detail.setProName(dto.getProName());
            detail.setProNo(dto.getProNo());
            int i = 1/0;
           // rabbitmqUtils.sendDetail(detail);
           // ThreadPoolExecutor
            //加入延迟队列
           // rabbitmqUtils.sendDelay(detail);
            return 3;

    }

    @Transactional(noRollbackFor = IllegalMonitorStateException.class)
    @Override
    public void addOpsRedis(ProDto proDto) {
        Integer stock = 0;
        Integer preStock = 0;
        RLock lock = redissonClient.getLock(proDto.getProductId().toString());
        try {
            //尝试获取锁最多3s 获取锁后5s就释放
            boolean flag = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!flag) {
                log.info("重复获取锁失败：productId"+proDto.getProductId());
                throw new RuntimeException("重复获取锁出现异常：productId:" + proDto.getProductId());
            }
            Object o = redisTemplate.opsForHash().get("product_data", proDto.getProductId().toString());
             stock = Convert.toInt(o);
             preStock = Convert.toInt(o);
            if (stock > 0) {
                stock--;
                //线程休眠6秒
                Thread.sleep(8000);
                redisTemplate.opsForHash().put("product_data", proDto.getProductId().toString(),stock.toString());
                Main main = new Main();
                main.setIsSubmit(0);
                main.setCreateTime(new Date());
                mainService.save(main);
                //int i = 1/0;
                preStock --;
                log.info("下单成功，剩余库存："+stock);
            } else {
                log.info("已售罄："+stock);
            }
        } catch (Exception e) {
            log.info("出现异常："+e);
            throw new RuntimeException("下单出现异常");
        } finally {
            if (preStock.intValue() != stock.intValue()) {
                System.out.println("手动回滚redis库存开始执行------stock:"+stock+"预扣库存："+preStock);
                //不相等说明出现异常 手动回滚redis库存 数据库数据存在事务回滚，不用手动回滚
                stock++;
                //redisTemplate.opsForHash().put("product_data", proDto.getProductId().toString(),stock.toString());
                System.out.println("手动回滚redis库存执行完成------stock:"+stock+"预扣库存："+preStock);
            }
            //不管执行是否成功都释放锁
            System.out.println("最终redis库存执行完成------stock:"+stock+"预扣库存："+preStock);
            System.out.println("释放锁");
           // lock.unlock();
        }
    }

    @Override
    public void addRedis(ProDto proDto) {
        try {
            OpsProduct product = opsProductService.getOne(new QueryWrapper<OpsProduct>().eq("id", proDto.getProductId()));
            if (ObjectUtil.isNotNull(product)) {
                //redisTemplate.opsForHash().put("product_data", proDto.getProductId().toString(), 10+"");
            }
        } catch (Exception e){
           log.info("出现异常："+e);
        }

    }
}
