package com.nowcoder.community.service;

import com.nowcoder.community.dao.elascticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate template;

    // 增加帖子
    public void saveDiscussPost(DiscussPost discussPost){
        discussPostRepository.save(discussPost);
    }

    // 删除帖子
    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    public long searchDiscussPostRows(String keyword){
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .build();
        return template.search(nativeSearchQuery,DiscussPost.class).getTotalHits();
    }

    public List<DiscussPost> searchDiscussPost(String keyword, int current, int limit){
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )
                .build();
        SearchHits<DiscussPost> searchHits = template.search(nativeSearchQuery, DiscussPost.class);
        List<DiscussPost> discussPostList = new ArrayList<>();
        if(searchHits.getTotalHits() > 0){
            searchHits.forEach(discussPostSearchHit -> {
                DiscussPost discussPost = discussPostSearchHit.getContent();
                // 高亮显示
                List<String> titleField = discussPostSearchHit.getHighlightField("title");
                if(titleField.size()>0){
                    discussPost.setTitle(titleField.get(0));
                }
                List<String> contentField = discussPostSearchHit.getHighlightField("content");
                if(contentField.size()>0){
                    discussPost.setContent(contentField.get(0));
                }
                discussPostList.add(discussPost);
            });
        }
        return discussPostList;
    }
}
