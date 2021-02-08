package com.rrk.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rrk.entity.TbUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dinghao
 * @since 2021-02-08
 */
public interface ITbUserService extends IService<TbUser> {

    Integer addUser(TbUser user);
}
