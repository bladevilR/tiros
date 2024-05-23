package org.jeecg.modules.third.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.service.BuMaterialTypeThirdService;
import org.jeecg.modules.third.maximo.bean.JdxInvbalancesOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.service.JdxInvbalancesOutService;
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
public class TransMaterialStockFromMaximoTask {

    @Resource
    private JdxInvbalancesOutService jdxInvbalancesOutService;
    @Resource
    private BuMaterialTypeThirdService buMaterialTypeThirdService;
    @Resource
    private MxoutInterTransService mxoutInterTransService;

    @Value("${serviceTask.transFromMaximo.materialStock.enable}")
    private String enableTask;


    @Scheduled(fixedDelayString = "${serviceTask.transFromMaximo.materialStock.interval}")
    private void checkAndDealTransData() {
        if ("false".equals(enableTask)) {
            return;
        }

        try {
            // 获取最新的消息
            List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_STOCK);
            if (CollectionUtils.isEmpty(outTransBakList)) {
                return;
            }

            // 获取变动数据
            List<JdxInvbalancesOut> maximoStockList = jdxInvbalancesOutService.listByOutTransList(outTransBakList);
            // 处理变动数据
            boolean consumeFlag = buMaterialTypeThirdService.taskConsumeMaximoStockData(maximoStockList);

            // 删除消息
            if (consumeFlag) {
                mxoutInterTransService.deleteOutTransQueue(outTransBakList);
            }
        } catch (Exception ex) {
            log.error("定时从maximo同步数据--物资库存：定时任务执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
