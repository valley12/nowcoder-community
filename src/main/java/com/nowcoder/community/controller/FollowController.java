package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        // 当前关注只设置了关注用户
        // entityId 和 entityUserId 相同
        // 可以关注更多的
        Event event =new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);

        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,"已关注!");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(),entityType, entityId);

        return CommunityUtil.getJSONString(0,"已取消关注！");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFolloweePage(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        int followeeCount = (int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER);

         // 分页显示,每页关注列表长度为10
         page.setLimit(10);
         page.setRows(followeeCount);
         page.setPath("/followees/" + userId);


         List<Map<String, Object>> followeeList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
         if(followeeList != null){
             for(Map<String, Object> map : followeeList){
                 User followeeUser = (User) map.get("user");
                 map.put("hasFollowed", hasFollowed(followeeUser.getId()));
             }
         }
         model.addAttribute("followeeList", followeeList);

        return "/site/followee";
    }

    public boolean hasFollowed(int userId){
        if(hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowerPage(@PathVariable("userId") int userId, Page page, Model model){

        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        int followerCount = (int)followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        // 分页显示,每页关注列表长度为10
        page.setLimit(10);
        page.setRows(followerCount);
        page.setPath("/followers/" + userId);

        List<Map<String, Object>> followerList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(followerList != null){
            for(Map<String, Object> map : followerList){
                User followerUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(followerUser.getId()));
            }
        }
        model.addAttribute("followerList", followerList);

        return "/site/follower";
    }

}
