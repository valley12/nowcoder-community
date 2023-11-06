package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 首页查询不用userId, 个人主页用到userId
    // 分页查询 offset起始行 limit 每页行数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //Param注解给参数取别名
    int selectDiscussPostRows(@Param("userId") int userId);
}
