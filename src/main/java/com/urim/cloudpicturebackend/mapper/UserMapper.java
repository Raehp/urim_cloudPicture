package com.urim.cloudpicturebackend.mapper;

import com.urim.cloudpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
* @author zhl20
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2025-10-04 15:43:56
 * @Entity com.urim.cloudpicturebackend.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
}




