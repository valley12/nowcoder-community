package com.nowcoder.community.controller;

import co.elastic.clients.elasticsearch._types.query_dsl.Like;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path="/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){

        List<DiscussPost> postList = elasticSearchService.searchDiscussPost(keyword,
                page.getCurrent() - 1, page.getLimit());

        List<Map<String, Object>> searchVo = new ArrayList<>();
        for(DiscussPost post : postList){
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            map.put("user",userService.findUserById(post.getUserId()));
            map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
            searchVo.add(map);
        }
        model.addAttribute("searchVo", searchVo);
        model.addAttribute("keyword", keyword);

        page.setPath("/search?keyword=" + keyword);
        page.setRows((int)elasticSearchService.searchDiscussPostRows(keyword));

        return "/site/search";
    }


}
