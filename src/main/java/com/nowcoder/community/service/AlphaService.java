package com.nowcoder.community.service;


import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AlphaService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    // 事务隔离级别 可重复读
    // 传播机制 业务方法A调用业务方法B
    // REQUIRED: 支持当前事务
    // REQUIRED_NEW: 创建一个新事务，并且暂停当前事务
    // NESTED:
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public Object save1(){

        //新增用户
        User user = new User();
        user.setUsername("xxd123456");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setSalt(CommunityUtil.md5("123") + user.getSalt());
        user.setEmail("xuxiaodong765@gmail.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新用户报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        // 出错执行事务回滚
        Integer.valueOf("abc");

        return "ok";
    }
}
