package org.jeecg.modules.third.task;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;
import org.jeecg.modules.third.jdx.service.BuWorkOrderThirdService;
import org.jeecg.modules.third.maximo.service.JdxInvbalancesOutService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 处理工单关闭时物料价格为0 定时任务
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-03-19
 */
@Slf4j
@Component
@EnableScheduling
public class HandleOrderMaterialPriceZeroTask {

    @Resource
    private JdxInvbalancesOutService jdxInvbalancesOutService;
    @Resource
    private BuWorkOrderThirdService buWorkOrderThirdService;


    @Scheduled(cron = "0 * * * * ?") // 每小时的每0分执行一次
    private void checkAndDealTransData() {
        try {
            long time1 = System.currentTimeMillis();

            // 查询架大修中价格为0的
            List<PriceZero> priceZeroList = buWorkOrderThirdService.getNeedCloseOrderMaterialPriceZero();
            // 查询maximo中对应价格
            priceZeroList = jdxInvbalancesOutService.selectPriceOfPriceZeroList(priceZeroList);
            priceZeroList = buWorkOrderThirdService.rewriteNeedCloseOrderMaterialPriceZero(priceZeroList);

            long currentMaximoPriceNotZeroCount = priceZeroList.stream()
                    .filter(item -> null != item.getCurrentMaximoPrice() && !item.getCurrentMaximoPrice().equals(BigDecimal.ZERO))
                    .count();
            long rewritePriceCount = priceZeroList.stream()
                    .filter(item -> (null != item.getNewOrderMaterialActPrice() && !item.getNewOrderMaterialActPrice().equals(BigDecimal.ZERO))
                            && (null != item.getNewGroupStockPrice() && !item.getNewGroupStockPrice().equals(BigDecimal.ZERO))
                            && (null != item.getNewAssignDetailPrice() && !item.getNewAssignDetailPrice().equals(BigDecimal.ZERO))
                    )
                    .count();

            String format = String.format("查询到价格为0的数据%s条，其中%s条可修正，%s条已修正。数据详情：%s",
                    priceZeroList.size(),
                    currentMaximoPriceNotZeroCount,
                    rewritePriceCount,
                    JSON.toJSONString(priceZeroList));

            long time2 = System.currentTimeMillis();
            log.info("定时任务【处理工单关闭时物料价格为0】执行完毕，耗时" + (time2 - time1) + "毫秒");
            log.info(format);
        } catch (Exception ex) {
            log.error("定时任务【处理工单关闭时物料价格为0】执行失败，原因如下：" + ex.getMessage());
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                ex.printStackTrace(printWriter);
            }
            log.error(stringWriter.toString());
        }
    }

}
