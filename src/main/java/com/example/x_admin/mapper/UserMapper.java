package com.example.x_admin.mapper;

import com.example.x_admin.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cainiao
 * @since 2024-05-13
 */
public interface UserMapper extends BaseMapper<User> {
    //不建议用@Select(),除非特别简单，例如单表查询

    public List<String> getRoleNamesByUserId(Integer userId);

}
