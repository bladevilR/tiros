package org.jeecg.modules.third.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.jdx.service.BuOrderMaterialRewriteService;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;
import org.jeecg.modules.third.maximo.service.JdxMatusetransInService;
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
 * 从maximo扫描并同步数据 定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-01
 */
@Slf4j
@Component
@EnableScheduling
public class TransMaterialReWriteFromMaximoTask {

    @Resource
    private JdxMatusetransInService jdxMatusetransInService;
    @Resource
    private BuOrderMaterialRewriteService buOrderMaterialRewriteService;

    @Value("${serviceTask.transFromMaximo.materialRewrite.enable}")
    private String enableTask;


    @Scheduled(fixedDelayString = "${serviceTask.transFromMaximo.materialRewrite.interval}")
    private void checkAndDealTransData() {
        if ("false".equals(enableTask)) {
            return;
        }

        try {
            // maximo工单物料中ready=1的数据（maximo消耗后更新ready=1）
            List<JdxMatusetransIn> maximoOrderMaterialList = jdxMatusetransInService.listMaximoMaterialByReady("1");
            if (CollectionUtils.isEmpty(maximoOrderMaterialList)) {
                return;
            }

            // 处理数据
            boolean consumeFlag = buOrderMaterialRewriteService.consumeMaximoTransData(maximoOrderMaterialList);

            // 标记maximo工单物料ready=2
            if (consumeFlag) {
                jdxMatusetransInService.updateMaximoMaterialReady(maximoOrderMaterialList, "2");
            }
        } catch (Exception ex) {
            log.error("从maximo同步数据--工单物料消耗或退料回写：定时任务执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
