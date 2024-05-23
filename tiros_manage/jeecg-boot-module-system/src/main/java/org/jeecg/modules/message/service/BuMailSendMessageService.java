package org.jeecg.modules.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.message.entity.BuMailSendMessage;

/**
 * @author yyg
 */
public interface BuMailSendMessageService extends IService<BuMailSendMessage> {
   /**
    * 保存邮件消息
    */
   void saveMailMsg();
}
