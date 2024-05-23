package org.jeecg.modules.third.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.service.BuMaterialTypeThirdService;
import org.jeecg.modules.third.maximo.bean.JdxInvbalancesOut;
import org.jeecg.modules.third.maximo.bean.JdxInvcostOut;
import org.jeecg.modules.third.maximo.bean.MxoutInterTransBak;
import org.jeecg.modules.third.maximo.service.JdxInvbalancesOutService;
import org.jeecg.modules.third.maximo.service.JdxInvcostOutService;
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
 * @since 2022-10-31
 */
@Slf4j
@Component
@EnableScheduling
public class TransMaterialStockPriceFromMaximoTask {

    @Resource
    private JdxInvcostOutService jdxInvcostOutService;
    @Resource
    private BuMaterialTypeThirdService buMaterialTypeThirdService;
    @Resource
    private MxoutInterTransService mxoutInterTransService;

    @Value("${serviceTask.transFromMaximo.materialStockPrice.enable}")
    private String enableTask;


    @Scheduled(fixedDelayString = "${serviceTask.transFromMaximo.materialStockPrice.interval}")
    private void checkAndDealTransData() {
        if ("false".equals(enableTask)) {
            return;
        }

        try {
            // 获取最新的消息
            List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_STOCK_PRICE);
            if (CollectionUtils.isEmpty(outTransBakList)) {
                return;
            }

            // 获取变动数据
            List<JdxInvcostOut> maximoStockPriceList = jdxInvcostOutService.listByOutTransList(outTransBakList);
            // 处理变动数据
            boolean consumeFlag = buMaterialTypeThirdService.taskConsumeMaximoStockPriceData(maximoStockPriceList);

            // 删除消息
            if (consumeFlag) {
                mxoutInterTransService.deleteOutTransQueue(outTransBakList);
            }
        } catch (Exception ex) {
            log.error("定时从maximo同步数据--物资库存成本：定时任务执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
