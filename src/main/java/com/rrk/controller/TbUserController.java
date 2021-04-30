package com.rrk.controller;


import com.rrk.entity.TbUser;
import com.rrk.exception.ResultBody;
import com.rrk.service.IDetailService;
import com.rrk.service.ITbUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dinghao
 * @since 2021-02-08
 */
@Api(value = "用户")
@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j
public class TbUserController {

    @Autowired
    private ITbUserService userService;

    @Autowired
    private IDetailService detailService;

    @ApiOperation(value = "添加用户",httpMethod = "POST")
    @PostMapping(value = "/addUser")
    @ResponseBody
    public ResultBody addUser(@RequestBody TbUser user){
        detailService.addDetail();
        Integer result = userService.addUser(user);
        return ResultBody.success();
    }

}

