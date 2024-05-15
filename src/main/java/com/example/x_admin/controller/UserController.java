package com.example.x_admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.vo.Result;
import com.example.x_admin.entity.User;
import com.example.x_admin.service.IUserRoleService;
import com.example.x_admin.service.IUserService;
import freemarker.template.utility.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
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
//跨域注解
//@CrossOrigin
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("X-Token") String token){
        userService.logout(token);
        return Result.success("退出登陆成功");
    }

    @GetMapping("/list")
    public Result<?> getUserListPage(@RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "phone", required = false) String phone,
                                     @RequestParam("pageNo") Long pageNo,
                                     @RequestParam("pageSize") Long pageSize){
/*        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.hasLength(username), User::getUsername, username);
        wrapper.eq(StringUtils.hasLength(phone),User::getPhone, phone);
        wrapper.orderByDesc(User::getId);*/
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (username != null){
            wrapper.eq("username", username);
        }

        if (phone != null){
            wrapper.eq("phone", phone);
        }

        wrapper.orderByDesc("id");
        Page<User> page = new Page<>(pageNo, pageSize);

        userService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }
    @PostMapping("/addUser")
    public Result<?> addUser(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return Result.success("添加用户成功");
    }

    //@PutMapping
    @PostMapping("/updateUser")
    public Result<?> updateUser(@RequestBody User user){
        user.setPassword(null );
        userService.updateById(user);
        return Result.success("修改用户成功");
    }

    @GetMapping("/getUserById/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer id){
        User user=userService.getById(id);
        return Result.success(user);
    }

    @DeleteMapping("/deleteUserById")
    public Result<User> deleteUserById(@RequestParam("deleteUserId") Integer id){
        userService.removeById(id);
        return Result.success("删除用户成功");
    }
}