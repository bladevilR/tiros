package org.jeecg;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.service.*;
import org.jeecg.modules.third.maximo.bean.*;
import org.jeecg.modules.third.maximo.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 第三方接口测试
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FromMaximoTaskTest {

    @Autowired
    private JdxFailurelistOutService jdxFailurelistOutService;
    @Autowired
    private BuFaultCodeNewThirdService buFaultCodeNewThirdService;
    @Autowired
    private JdxRealassetOutService jdxRealassetOutService;
    @Autowired
    private BuMaterialToolsThirdService buMaterialToolsThirdService;
    @Autowired
    private JdxInvbalancesOutService jdxInvbalancesOutService;
    @Resource
    private JdxMatusetransInService jdxMatusetransInService;
    @Autowired
    private BuOrderMaterialRewriteService buOrderMaterialRewriteService;
    @Autowired
    private BuMaterialTypeThirdService buMaterialTypeThirdService;
    @Autowired
    private JdxWoOutService jdxWoOutService;
    @Autowired
    private BuTrainHistoryWorkThirdService buTrainHistoryWorkThirdService;
    @Autowired
    private JdxSrOutService jdxSrOutService;
    @Autowired
    private BuTrainHistoryFaultThirdService buTrainHistoryFaultThirdService;
    @Autowired
    private JdxRealassettransOutService jdxRealassettransOutService;
    @Autowired
    private BuTrainHistoryChangeThirdService buTrainHistoryChangeThirdService;
    @Autowired
    private MxoutInterTransService mxoutInterTransService;
    @Resource
    private JdxAssetOutService jdxAssetOutService;
    @Resource
    private JdxProjectOutService jdxProjectOutService;
    @Resource
    private BuMaximoFinanceItemThirdService buMaximoFinanceItemThirdService;


    /**
     * 定时从maximo同步数据--工单
     */
    @Test
    public void TransOrderFromMaximoTask() throws Exception {
        // 获取最新的消息
        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_ORDER);
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return;
        }

        // 获取变动数据
        List<JdxWoOut> maximoOrderList = jdxWoOutService.listByOutTransList(outTransBakList);
        // 获取maximo资产
        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        // 处理变动数据
        boolean consumeFlag = buTrainHistoryWorkThirdService.taskConsumeMaximoOrderData(maximoOrderList, maximoAssetList);

        // 删除消息
        if (consumeFlag) {
            mxoutInterTransService.deleteOutTransQueue(outTransBakList);
        }
    }

    /**
     * 定时从maximo同步数据--故障
     */
    @Test
    public void TransFaultFromMaximoTask() throws Exception {
        // 获取最新的消息
        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_FAULT);
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return;
        }

        // 获取变动数据
        List<JdxSrOut> maximoFaultList = jdxSrOutService.listByOutTransList(outTransBakList);
        // 获取maximo资产
        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        // 处理变动数据
        boolean consumeFlag = buTrainHistoryFaultThirdService.taskConsumeMaximoFaultData(maximoFaultList, maximoAssetList);

        // 删除消息
        if (consumeFlag) {
            mxoutInterTransService.deleteOutTransQueue(outTransBakList);
        }
    }

    /**
     * 定时从maximo同步数据--更换
     */
    @Test
    public void TransChangeFromMaximoTask() throws Exception {
        // 获取最新的消息
        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_CHANGE);
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return;
        }

        // 获取变动数据
        List<JdxRealassettransOut> maximoChangeList = jdxRealassettransOutService.listByOutTransList(outTransBakList);
        // 获取maximo资产
        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        // 处理变动数据
        boolean consumeFlag = buTrainHistoryChangeThirdService.taskConsumeMaximoChangeData(maximoChangeList, maximoAssetList);

        // 删除消息
        if (consumeFlag) {
            mxoutInterTransService.deleteOutTransQueue(outTransBakList);
        }
    }

    /**
     * 定时从maximo同步数据--物资库存
     */
    @Test
    public void TransMaterialStockFromMaximoTask() throws Exception {
//        // 获取最新的消息
//        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_STOCK);
//        if (CollectionUtils.isEmpty(outTransBakList)) {
//            return;
//        }

        List<Long> transIdList = Arrays.asList(1064943L, 1064944L, 1064945L, 1064946L, 1064947L, 1064948L, 1064949L, 1064950L, 1064951L, 1064952L, 1064953L, 1064954L, 1064955L, 1064956L, 1064957L, 1064958L, 1064959L, 1064960L, 1065035L, 1065036L, 1065037L, 1065038L, 1065039L, 1065040L, 1065041L, 1065042L, 1065043L, 1065044L, 1065045L, 1065046L, 1065047L, 1065048L, 1065049L, 1065050L, 1065051L, 1065052L, 1065053L, 1065054L, 1065055L, 1065056L, 1065057L, 1065058L, 1065059L, 1065060L, 1065061L, 1065062L, 1065063L, 1065064L, 1065065L, 1065066L, 1065067L, 1065068L, 1065069L, 1065070L, 1065071L, 1065081L, 1065082L, 1065083L, 1065113L, 1065114L, 1065115L, 1065117L, 1065130L, 1065131L, 1065132L, 1065133L, 1065134L, 1065135L, 1065188L, 1065189L, 1065190L, 1065191L, 1065192L, 1065193L, 1065194L, 1065195L, 1065196L, 1065197L, 1065198L, 1065199L, 1065200L, 1065202L, 1065203L, 1065204L, 1065205L, 1065206L, 1065207L, 1065208L, 1065209L, 1065210L, 1065211L, 1065212L, 1065213L, 1065214L, 1065215L, 1065216L, 1065217L, 1065218L, 1065219L, 1065220L, 1065221L, 1065222L, 1065223L, 1065224L, 1065225L, 1065226L, 1065227L, 1065228L, 1065241L, 1065242L, 1065243L, 1065244L, 1065956L, 1065965L, 1065966L, 1065967L, 1065993L, 1065994L, 1065995L, 1065996L, 1065999L, 1066000L, 1066003L, 1066004L, 1066005L, 1066006L, 1066007L, 1066008L, 1066012L, 1066013L, 1066015L, 1066021L);
        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByTransIdList(transIdList);

        // 获取变动数据
        List<JdxInvbalancesOut> maximoStockList = jdxInvbalancesOutService.listByOutTransList(outTransBakList);
        System.out.println(maximoStockList.size());
//        // 处理变动数据
//        boolean consumeFlag = buMaterialTypeThirdService.taskConsumeMaximoStockData(maximoStockList);
//
//        // 删除消息
//        if (consumeFlag) {
//            mxoutInterTransService.deleteOutTransQueue(outTransBakList);
//        }
    }

    /**
     * 定时从maximo同步数据--工单物料之架大修消耗状态回写
     */
    @Test
    public void TransMaterialConsumeRewriteFromMaximoTask() throws Exception {
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
    }

    /**
     * 定时从maximo同步数据--财务项目
     */
    @Test
    public void TransProjectFromMaximoTask() throws Exception {
        // 获取最新的消息
        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_FINANCE);
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return;
        }

        // 获取变动数据
        List<JdxProjectOut> maximoProjectList = jdxProjectOutService.listByOutTransList(outTransBakList);
        // 处理变动数据
        boolean consumeFlag = buMaximoFinanceItemThirdService.consumeMaximoTransChangeData(maximoProjectList);

        // 删除消息
        if (consumeFlag) {
            mxoutInterTransService.deleteOutTransQueue(outTransBakList);
        }
    }

    /**
     * 定时从maximo同步数据--工器具
     */
    @Test
    public void TransToolFromMaximoTask() throws Exception {
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
    }

    /**
     * 定时从maximo同步数据--故障代码
     */
    @Test
    public void TransFaultCodeFromMaximoTask() throws Exception {
// 获取最新的消息
        List<MxoutInterTransBak> outTransBakList = mxoutInterTransService.listOutTransQueueByIFaceName(MaximoThirdConstant.IFACENAME_FAULT_CODE);
        if (CollectionUtils.isEmpty(outTransBakList)) {
            return;
        }

        // 获取变动数据
        List<JdxFailurelistOut> maximoFaultCodeList = jdxFailurelistOutService.listByOutTransList(outTransBakList);
        // 处理变动数据
        boolean consumeFlag = buFaultCodeNewThirdService.consumeMaximoTransChangeData(maximoFaultCodeList);

        // 删除消息
        if (consumeFlag) {
            mxoutInterTransService.deleteOutTransQueue(outTransBakList);
        }
    }

}
