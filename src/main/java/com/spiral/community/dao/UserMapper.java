package com.spiral.community.dao;

import com.spiral.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    // Select Method
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);

    //Insert Method
    int insertUser(User user);

    //Update Method
    int updateStatus(int id, int status);
    int updateHeadUrl(int id, String headUrl);
    int updatePassword(int id, String password);




}
