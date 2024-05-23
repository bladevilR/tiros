package org.jeecg.modules.message.job;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.message.entity.BuMailSendMessage;
import org.jeecg.modules.message.handle.impl.EmailSendMsgHandle;
import org.jeecg.modules.message.service.BuMailSendMessageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yyg
 */
@Slf4j
public class SendEmailJob implements Job {

    @Resource
    private BuMailSendMessageService mailSendMessageService;
    @Resource
    private EmailSendMsgHandle emailSendMsgHandle;

    @Override

    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {

        log.info(String.format(" Jeecg-Boot 发送邮件任务 SendEmailJob !  时间:" + DateUtils.getTimestamp()));

        mailSendMessageService.saveMailMsg();
        List<BuMailSendMessage> messageList = mailSendMessageService.list(Wrappers.<BuMailSendMessage>lambdaQuery().eq(BuMailSendMessage::getSendStatus, 0));

        if (CollectionUtil.isNotEmpty(messageList)) {
            messageList.forEach(msg -> {
                emailSendMsgHandle.SendMsg(msg.getReceiveMail(), msg.getTitle(), msg.getContent());
                mailSendMessageService.updateById(new BuMailSendMessage().setId(msg.getId()).setSendStatus(1));
            });
        }

    }
}
