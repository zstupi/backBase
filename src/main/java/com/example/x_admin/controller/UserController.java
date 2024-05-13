package com.example.x_admin.controller;

import com.example.common.vo.Result;
import com.example.x_admin.entity.User;
import com.example.x_admin.service.IUserRoleService;
import com.example.x_admin.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author cainiao
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/all")
    public Result<List<User>> getAllUser() {
        List<User> list = userService.list();
        return Result.success(list, "登陆成功");
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {

        Map<String, Object> data = userService.login(user);
        if (data != null) {
            return  Result.success(data);
        }

        return Result.fail(20002, "用户名或密码不正确");
    }
    @GetMapping("/info")
    public Result<Map<String,Object>> gstUserInfo(@RequestParam("token") String token){
        //根据token获取用户信息
        Map<String,Object> data = userService.getUserInfo(token);
        if(data != null){
            return Result.success(data);
        }
        return Result.fail(20003,"用户信息获取失败");
    }
}