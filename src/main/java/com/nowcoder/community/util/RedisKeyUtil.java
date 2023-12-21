package com.nowcoder.community.util;

import java.util.HashMap;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";


    private static final String[] likeEntity = {"", "post", "comment"};


    // like:entity:entityType:entityId -> set(userId)

    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + likeEntity[entityType] + SPLIT +  entityId;
    }

    // 某个用户的赞
    // like:user:userId
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体(用户，帖子)
    // followee:userId:entityType -> zset(entityId, timestamp)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体(用户、帖子、主题)的粉丝
    // follower:entityType:entityId -> zset(userId, timestamp)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}
