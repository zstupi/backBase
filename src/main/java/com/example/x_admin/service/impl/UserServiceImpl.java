package com.example.x_admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.x_admin.entity.User;
import com.example.x_admin.mapper.UserMapper;
import com.example.x_admin.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cainiao
 * @since 2024-05-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(User user) {
        //根据用户名查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.eq(User::getUsername,user.getUsername());

        User loginUser= this.baseMapper.selectOne(wrapper);
        //如果不为空并且密码匹配，生成token

        if(loginUser!=null && passwordEncoder.matches(user.getPassword(),loginUser.getPassword())) {
            // 暂时用UUID，最终方案jwt
            String key="user"+ UUID.randomUUID();
            //存入redis
            loginUser.setPassword(null);
            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);

            //返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", key);
            return data;
        }
        return null;
    }
//    @Override
//    public Map<String, Object> login(User user) {
//        //根据用户名和密码查询
//       LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
//        wrapper.eq(User::getUsername,user.getUsername());
//        wrapper.eq(User::getPassword,user.getPassword());
//        User loginUser= this.baseMapper.selectOne(wrapper);
//        //如果不为空，生成token
//        if(loginUser!=null) {
//            // 暂时用UUID，最终方案jwt
//            String key="user"+ UUID.randomUUID();
//            //存入redis
//            loginUser.setPassword(null);
//            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
//
//
//
//            //返回数据
//            Map<String, Object> data = new HashMap<>();
//            data.put("token", key);
//            return data;
//        }
//        return null;
//    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        //根据user拿到token
        Object obj=redisTemplate.opsForValue().get(token);
        if(obj!=null){
            User loginuser=JSON.parseObject(JSON.toJSONString(obj),User.class);
            Map<String, Object> data= new HashMap<>();
            data.put("name",loginuser.getUsername());
            data.put("avator",loginuser.getAvatar());

            //角色
            List<String> roleList = this.getBaseMapper().getRoleNamesByUserId(loginuser.getId());
            data.put("roles", roleList);
            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }

}
