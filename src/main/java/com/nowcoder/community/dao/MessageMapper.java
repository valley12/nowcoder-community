package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表
    // 针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);


    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话列表所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信数量
    // 1.查询当前用户所有的未读私信数量
    // 2.查询当前会话的未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);



}
