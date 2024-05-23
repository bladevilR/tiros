package org.jeecg.modules.third.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.service.BuMaterialToolsThirdService;
import org.jeecg.modules.third.maximo.bean.JdxRealassetOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.service.JdxRealassetOutService;
import org.jeecg.modules.third.maximo.service.MxoutInterTransService;
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
 * @since 2021-05-06
 */
@Slf4j
@Component
@EnableScheduling
public class TransToolFromMaximoTask {

    @Resource
    private JdxRealassetOutService jdxRealassetOutService;
    @Resource
    private BuMaterialToolsThirdService buMaterialToolsThirdService;
    @Resource
    private MxoutInterTransService mxoutInterTransService;

    @Value("${serviceTask.transFromMaximo.tool.enable}")
    private String enableTask;


    @Scheduled(fixedDelayString = "${serviceTask.transFromMaximo.tool.interval}")
    private void checkAndDealTransData() {
        if ("false".equals(enableTask)) {
            return;
        }

        try {
            // 获取最新的消息
            List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_TOOL);
            if (CollectionUtils.isEmpty(outTransBakList)) {
                return;
            }

            // 获取变动数据
            List<JdxRealassetOut> maximoToolList = jdxRealassetOutService.listByOutTransList(outTransBakList);
            // 处理变动数据
            boolean consumeFlag = buMaterialToolsThirdService.consumeMaximoTransChangeData(maximoToolList);

            // 删除消息
            if (consumeFlag) {
                mxoutInterTransService.deleteOutTransQueue(outTransBakList);
            }
        } catch (Exception ex) {
            log.error("从maximo同步数据--工器具：定时任务执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
