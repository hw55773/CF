package com.kxdkcf.service;

import com.kxdkcf.Result.Result;
import com.kxdkcf.dto.PageTopicDTO;
import com.kxdkcf.dto.ReplyDTO;
import com.kxdkcf.dto.TopicDTO;
import com.kxdkcf.enity.Topic;
import com.kxdkcf.vo.PageTopicBean;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service
 * Author:              wenhao
 * CreateTime:          2025-01-18  15:01
 * Description:         TODO
 * Version:             1.0
 */
public interface ITopicService {
    Result add(Topic topic);

    Result updateById(List<TopicDTO> topicDTOs);

    Result queryTopicDetails(Long topicId);

    Result postReply(ReplyDTO reply);

    ResponseEntity<?> updateReply(Long id);

    ResponseEntity<?> deleteReplyById(Long replyId);

    PageTopicBean queryCollected(PageTopicDTO pageTopicDTO);
}
