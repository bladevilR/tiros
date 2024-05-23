package org.jeecg.modules.third.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.jdx.bean.BuWorkOrder;
import org.jeecg.modules.third.jdx.service.BuWorkOrderThirdService;
import org.jeecg.modules.third.maximo.service.JdxWoInService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * <p>
 * 从maximo扫描并同步工单的maximo工单id 定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/12/20
 */
@Slf4j
@Component
@EnableScheduling
public class TransMaximoWorkOrderIdFromMaximoTask {

    @Resource
    private BuWorkOrderThirdService buWorkOrderThirdService;
    @Resource
    private JdxWoInService jdxWoInService;

    @Value("${serviceTask.transFromMaximo.maximoWorkOrderId.enable}")
    private String enableTask;


    @Scheduled(fixedDelayString = "${serviceTask.transFromMaximo.maximoWorkOrderId.interval}")
    private void checkAndDealTransData() {
        if ("false".equals(enableTask)) {
            return;
        }

        try {
            // 获取没有maximo工单id的工单
            List<BuWorkOrder> orderList = buWorkOrderThirdService.listOrderNeedMaximoWorkOrderId();
            if (CollectionUtils.isEmpty(orderList)) {
                return;
            }

            // 读取对应的maximo工单id
            boolean flag = jdxWoInService.getOrderMaximoWorkOrderId(orderList);

            // 更新
            if (flag) {
                buWorkOrderThirdService.updateOrderMaximoWorkOrderId(orderList);
            }
        } catch (Exception ex) {
            log.error("定时从maximo同步数据--maximo工单id：定时任务执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
