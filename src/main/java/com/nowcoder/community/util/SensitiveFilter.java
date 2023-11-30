package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    // 根节点初始化
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){

        // try-with-resources
        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                // 添加到前缀树
                this.addKeyword(keyword);
            }


        } catch (Exception e) {
            logger.error("加载敏感词文件失败： " + e.getMessage());
        }

    }

    // 将敏感词添加到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0;i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指向子结点,进入下一轮循环
            tempNode = subNode;
            // 设置结束标识
            if(i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootNode;
            }else{
                position++;
            }

        }
        sb.append(text.substring(begin));
        return sb.toString();


    }

    private boolean isSymbol(Character c){
        // 0x2E80~0x9FFF 东亚文字范围 中文, 日文, 韩文 CJK
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    // 前缀树
    private class TrieNode{

        // 关键词结尾
        private boolean isKeywordEnd = false;

        private Map<Character, TrieNode> subnodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TrieNode node){
            subnodes.put(c, node);
        }

        public TrieNode getSubNode(Character c){
            return subnodes.get(c);
        }

    }



}
