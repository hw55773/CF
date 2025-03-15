package com.kxdkcf.service;

import com.kxdkcf.Result.Result;
import com.kxdkcf.enity.Dialog;
import com.kxdkcf.vo.MessageVO;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2025-03-01  15:02
 * Description:         TODO
 * Version:             1.0
 */
public interface IAiService {
    Result addDislog();

    void addMessage(String content, Long dialogId, byte type);

    List<MessageVO> showRecord(Long dialogId);

    Result showDialogs();

    Result deleteDialogById(Long dialogId);

    Result updateDialog(Dialog dialog);
}
