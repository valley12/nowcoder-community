package com.nowcoder.community.util;

public interface CommunityConstant {

   //用户状态
   int USER_ACTIVATED = 1;

   int USER_NOT_ACTIVATED = 0;
   int ACTIVATION_SUCCESS = 0;

   int ACTIVATION_REPEAT = 1;

   int ACTIVATION_FAILURE = 2;

   /**
    * 默认状态的登录凭证超时时间
    */
   int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

   /**
    * 记住状态的登录凭证超时时间
    */
   int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

   // 实体类型：帖子
   int ENTITY_TYPE_POST = 1;
   // 实体类型：评论
   int ENTITY_TYPE_COMMENT = 2;
   // 实体类型：用户
   int ENTITY_TYPE_USER = 3;

   // 点赞类型
   int LIKE_STATUS = 1;
   int NONE_LIKE_STATUS = 0;
   int DISLIKE_STATUS = -1;

   //
   // 登录状态:登录有效状态
   int LOGIN_TICKET_VALID = 0;
   // 登录状态:登录状态失效
   int LOGIN_TICKET_INVALID = 1;

   // 主题：评论
   String TOPIC_COMMENT = "comment";
   // 主题：点赞
   String TOPIC_LIKE = "like";
   // 主题：关注
   String TOPIC_FOLLOW = "follow";

   //主题：发帖
   String TOPIC_PUBLISH = "publish";

   // 系统用户ID=1
   int SYSTEM_USER_ID = 1;






}
