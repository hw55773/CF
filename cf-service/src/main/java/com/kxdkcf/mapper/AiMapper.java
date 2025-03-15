package com.kxdkcf.mapper;

import com.kxdkcf.enity.Dialog;
import com.kxdkcf.enity.Message;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.mapper
 * Author:              wenhao
 * CreateTime:          2025-03-01  15:06
 * Description:         TODO
 * Version:             1.0
 */
@Mapper
public interface AiMapper {
    void insert(Dialog dialog);

    void insertMessage(Message message);

    List<Message> selectByDialogId(Long dialogId);

    List<Dialog> selectByUserId(Long userId);

    @Delete("delete from dialog where id=#{dialogId}")
    void deleteDialogById(Long dialogId);

    @Delete("delete from message where dialog_id =#{dialod}")
    void deleteMessageByDialogId(Long dialogId);

    void updateDialog(Dialog dialog);

    @Delete("delete from dialog where user_id=#{userId}")
    void deleteByUserId(Long userId);

    void deleteMessageByDialogIds(List<Long> dialogIds);
}
