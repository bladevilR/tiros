package org.jeecg;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuWorkOrder;
import org.jeecg.modules.third.jdx.service.BuFaultInfoThirdService;
import org.jeecg.modules.third.jdx.service.BuMaterialAssignDetailThirdService;
import org.jeecg.modules.third.jdx.service.BuMaterialReturnedDetailThirdService;
import org.jeecg.modules.third.jdx.service.BuWorkOrderThirdService;
import org.jeecg.modules.third.maximo.bean.JdxMatusetransIn;
import org.jeecg.modules.third.maximo.bean.JdxSrIn;
import org.jeecg.modules.third.maximo.bean.JdxWoIn;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;
import org.jeecg.modules.third.maximo.service.JdxMatusetransInService;
import org.jeecg.modules.third.maximo.service.JdxSrInService;
import org.jeecg.modules.third.maximo.service.JdxWoInService;
import org.jeecg.modules.third.maximo.service.MxinInterTransService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
public class ToMaximoOnceTest {

    @Autowired
    private MxinInterTransService mxinInterTransService;
    @Autowired
    private BuWorkOrderThirdService buWorkOrderThirdService;
    @Autowired
    private JdxWoInService jdxWoInService;
    @Autowired
    private BuMaterialAssignDetailThirdService buMaterialAssignDetailThirdService;
    @Autowired
    private BuMaterialReturnedDetailThirdService buMaterialReturnedDetailThirdService;
    @Autowired
    private JdxMatusetransInService jdxMatusetransInService;
    @Autowired
    private BuFaultInfoThirdService buFaultInfoThirdService;
    @Autowired
    private JdxSrInService jdxSrInService;


    /**
     * 写入工单
     */
    @Test
    public void writeNewOrder() throws Exception {
        String orderStr = "{\"createBy\":\"nijiahua\",\"createTime\":1626072533896,\"depotId\":\"CLD1\",\"duration\":1,\"finishTime\":1626019200000,\"fromType\":3,\"generate\":0,\"groupId\":\"cggb\",\"id\":\"8a8a8b497a98370e017a9979cb840282\",\"issuingTime\":1626072534070,\"lineId\":\"2\",\"lineNum\":\"L002\",\"monitor\":\"0acf487530214829a1b253c46ff144e0\",\"monitorWorkNo\":\"202913\",\"orderCode\":\"J000000053\",\"orderName\":\"补单\",\"orderStatus\":2,\"orderType\":4,\"planId\":\"8a8a8b497a6dca62017a755a042c5a97\",\"repairProgramName\":\"架修\",\"startTime\":1626019200000,\"trainNo\":\"0231\",\"updateBy\":\"zhuxiachen\",\"updateTime\":1626072607253,\"workStatus\":0,\"workshopId\":\"CJ1\"}";
        BuWorkOrder order = JSON.parseObject(orderStr, BuWorkOrder.class);
        JdxWoIn maximoOrder = buWorkOrderThirdService.getMaximoOrderNeedWriteByOrder(order);

        if (null != maximoOrder) {
            long transId = mxinInterTransService.getInTransId();
            System.out.println(transId);

            maximoOrder.setTransid(transId);

            MxinInterTrans inTrans = new MxinInterTrans()
                    .setTransid(transId)
                    .setAction("Add")
                    .setExtsysname("JDX")
                    .setIfacename(MaximoThirdConstant.IFACENAME_ORDER)
                    .setTranslanguage("ZH");

            // 保存数据
            boolean saveFlag = jdxWoInService.saveOne(maximoOrder);
            if (saveFlag) {
                mxinInterTransService.saveOne(inTrans);
            }
        }
    }

    /**
     * 关闭工单，写入工单更新
     */
    @Test
    public void writeCloseOrder() throws Exception {
        String orderStr = "{\"createBy\":\"nijiahua\",\"createTime\":1626072533896,\"depotId\":\"CLD1\",\"duration\":1,\"finishTime\":1626019200000,\"fromType\":3,\"generate\":0,\"groupId\":\"cggb\",\"id\":\"8a8a8b497a98370e017a9979cb840282\",\"issuingTime\":1626072534070,\"lineId\":\"2\",\"lineNum\":\"L002\",\"monitor\":\"0acf487530214829a1b253c46ff144e0\",\"monitorWorkNo\":\"202913\",\"orderCode\":\"J000000053\",\"orderName\":\"补单\",\"orderStatus\":2,\"orderType\":4,\"planId\":\"8a8a8b497a6dca62017a755a042c5a97\",\"repairProgramName\":\"架修\",\"startTime\":1626019200000,\"trainNo\":\"0231\",\"updateBy\":\"zhuxiachen\",\"updateTime\":1626072607253,\"workStatus\":0,\"workshopId\":\"CJ1\"}";
        BuWorkOrder order = JSON.parseObject(orderStr, BuWorkOrder.class);
        JdxWoIn maximoOrder = buWorkOrderThirdService.getMaximoOrderNeedWriteByOrder(order);

        if (null != maximoOrder) {
            Long maxInTransId = mxinInterTransService.getInTransId();
            AtomicLong transIdIndex = new AtomicLong(maxInTransId + 1);

            long transId = transIdIndex.getAndIncrement();
            maximoOrder.setTransid(transId);

            MxinInterTrans inTrans = new MxinInterTrans()
                    .setTransid(transId)
                    .setAction("Replace")
                    .setExtsysname("JDX")
                    .setIfacename(MaximoThirdConstant.IFACENAME_ORDER)
                    .setTranslanguage("ZH");

            // 保存数据
            jdxWoInService.saveOne(maximoOrder);
            mxinInterTransService.saveOne(inTrans);
        }
    }

    /**
     * 写入物料消耗
     */
    @Test
    public void writeMaterialConsume() throws Exception {
        String applyDetailId = "";
        // 查询需写入maximo物料消耗，并转换
        List<JdxMatusetransIn> maximoOrderMaterialList = buMaterialAssignDetailThirdService.getMaximoMaterialConsumeNeedWriteByApplyDetailId(applyDetailId);

        if (CollectionUtils.isNotEmpty(maximoOrderMaterialList)) {
            Long maxInTransId = mxinInterTransService.getInTransId();
            AtomicLong transIdIndex = new AtomicLong(maxInTransId + 1);

            List<MxinInterTrans> inTransList = new ArrayList<>();
            for (JdxMatusetransIn maximoOrderMaterial : maximoOrderMaterialList) {
                long transId = transIdIndex.getAndIncrement();
                maximoOrderMaterial.setTransid(transId);

                MxinInterTrans inTrans = new MxinInterTrans()
                        .setTransid(transId)
                        .setAction("Add")
                        .setExtsysname("JDX")
                        .setIfacename("JDX_MATUSETRANS")
                        .setTranslanguage("ZH");
                inTransList.add(inTrans);
            }

            // 保存数据
            boolean saveFlag = jdxMatusetransInService.saveList(maximoOrderMaterialList);
            if (saveFlag) {
                mxinInterTransService.saveList(inTransList);
            }
        }
    }

    /**
     * 写入物料退库
     */
    @Test
    public void writeMaterialReturn() throws Exception {
        String returnedId = "";
        // 查询需写入maximo物料退库，并转换
        List<JdxMatusetransIn> maximoOrderMaterialList = buMaterialReturnedDetailThirdService.getMaximoMaterialReturnNeedWriteByReturnedId(returnedId);

        if (CollectionUtils.isNotEmpty(maximoOrderMaterialList)) {
            Long maxInTransId = mxinInterTransService.getInTransId();
            AtomicLong transIdIndex = new AtomicLong(maxInTransId + 1);

            List<MxinInterTrans> inTransList = new ArrayList<>();
            for (JdxMatusetransIn maximoOrderMaterial : maximoOrderMaterialList) {
                long transId = transIdIndex.getAndIncrement();
                maximoOrderMaterial.setTransid(transId);

                MxinInterTrans inTrans = new MxinInterTrans()
                        .setTransid(transId)
                        .setAction("Add")
                        .setExtsysname("JDX")
                        .setIfacename("JDX_MATUSETRANS")
                        .setTranslanguage("ZH");
                inTransList.add(inTrans);
            }

            // 保存数据
            boolean saveFlag = jdxMatusetransInService.saveList(maximoOrderMaterialList);
            if (saveFlag) {
                mxinInterTransService.saveList(inTransList);
            }
        }
    }

    /**
     * 写入故障
     */
    @Test
    public void writeFault() throws Exception {
        String faultId = "";
        // 查询需写入maximo故障，并转换
        JdxSrIn maximoFault = buFaultInfoThirdService.getMaximoFaultNeedWriteByFaultId(faultId);

        if (null != maximoFault) {
            Long maxInTransId = mxinInterTransService.getInTransId();
            AtomicLong transIdIndex = new AtomicLong(maxInTransId + 1);

            long transId = transIdIndex.getAndIncrement();
            maximoFault.setTransid(transId);

            MxinInterTrans inTrans = new MxinInterTrans()
                    .setTransid(transId)
                    .setAction("Add")
                    .setExtsysname("JDX")
                    .setIfacename(MaximoThirdConstant.IFACENAME_FAULT)
                    .setTranslanguage("ZH");

            // 保存数据
            boolean saveFlag = jdxSrInService.saveOne(maximoFault);
            if (saveFlag) {
                mxinInterTransService.saveOne(inTrans);
            }
        }
    }

}
