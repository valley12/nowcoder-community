package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
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

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired LikeService likeService;

    @RequestMapping(path="/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403, "您还没有登录!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0, "发布成功！");

    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        // 帖子点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
        int likeStatus = hostHolder.getUser() == null ? NONE_LIKE_STATUS :
                likeService.findEntityLikeStatus(user.getId(),ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页信息
        // 每页5条评论
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论: 帖子的评论
        // 回复: 评论的评论
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, post.getId(),page.getOffset(),page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                //帖子评论
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                likeStatus = hostHolder.getUser() == null ? NONE_LIKE_STATUS :
                        likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                //评论回复列表
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply: replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        likeStatus = likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        replyVo.put("likeStatus", likeStatus);
                        // 回复的目标对象
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }

        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }


}
