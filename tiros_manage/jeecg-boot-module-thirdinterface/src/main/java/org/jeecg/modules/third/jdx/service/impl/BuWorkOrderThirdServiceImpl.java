package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuWorkOrder;
import org.jeecg.modules.third.jdx.bean.bo.PriceZero;
import org.jeecg.modules.third.jdx.bean.vo.OrderDiff;
import org.jeecg.modules.third.jdx.mapper.BuWorkOrderThirdMapper;
import org.jeecg.modules.third.jdx.mapper.ThirdCommonMapper;
import org.jeecg.modules.third.jdx.service.BuWorkOrderThirdService;
import org.jeecg.modules.third.maximo.bean.JdxWoIn;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工单 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-13
 */
@Slf4j
@Service
public class BuWorkOrderThirdServiceImpl extends ServiceImpl<BuWorkOrderThirdMapper, BuWorkOrder> implements BuWorkOrderThirdService {

    @Resource
    private BuWorkOrderThirdMapper buWorkOrderThirdMapper;
    @Resource
    private ThirdCommonMapper thirdCommonMapper;


    /**
     * @see BuWorkOrderThirdService#getMaximoOrderNeedWriteByOrder(BuWorkOrder)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public JdxWoIn getMaximoOrderNeedWriteByOrder(BuWorkOrder order) throws Exception {
        return transformToMaximoOrder(order);
    }

    /**
     * @see BuWorkOrderThirdService#listOrderNeedMaximoWorkOrderId()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkOrder> listOrderNeedMaximoWorkOrderId() throws Exception {
        List<Integer> notNeedMaximoWorkOrderIdOrderStatus = Arrays.asList(0, 1, 5, 9);
        LambdaQueryWrapper<BuWorkOrder> wrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .isNull(BuWorkOrder::getMaximoWorkOrderId)
                .notIn(BuWorkOrder::getOrderStatus, notNeedMaximoWorkOrderIdOrderStatus);
        return buWorkOrderThirdMapper.selectList(wrapper);
    }

    /**
     * @see BuWorkOrderThirdService#updateOrderMaximoWorkOrderId(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrderMaximoWorkOrderId(List<BuWorkOrder> orderList) throws Exception {
        if (CollectionUtils.isEmpty(orderList)) {
            return true;
        }

        int all = orderList.size();
        orderList.removeIf(order -> null == order.getMaximoWorkOrderId());
        int update = orderList.size();

        List<List<BuWorkOrder>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderList);
        for (List<BuWorkOrder> batchSub : batchSubList) {
            buWorkOrderThirdMapper.updateListMaximoWorkOrderId(batchSub);
        }

        if (update > 0) {
            log.info(String.format("定时从maximo同步数据--maximo工单id：本次获取到需设置maximoWorkOrderId的工单%s条，更新了%s条",
                    all,
                    update));
        }

        return true;
    }

    /**
     * @see BuWorkOrderThirdService#getOrderDiffOfJdxAndMaximo(List)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public OrderDiff getOrderDiffOfJdxAndMaximo(List<String> maximoOrderCodeList) throws Exception {
        // 查询已核实的工单
        List<Integer> notVerifyOrderStatusList = Arrays.asList(0, 1);
        LambdaQueryWrapper<BuWorkOrder> wrapper = new LambdaQueryWrapper<BuWorkOrder>()
                .notIn(BuWorkOrder::getOrderStatus, notVerifyOrderStatusList);
        List<BuWorkOrder> orderList = buWorkOrderThirdMapper.selectList(wrapper);
        // 暂停前未核实的过滤掉
        orderList.removeIf(order -> 5 == order.getOrderStatus() && notVerifyOrderStatusList.contains(order.getSuspendStatus()));

        // 计算
        List<String> bothExistOrderCodeList = new ArrayList<>();
        List<String> onlyJdxExistOrderCodeList = new ArrayList<>();
        List<String> onlyMaximoExistOrderCodeList = new ArrayList<>(maximoOrderCodeList);
        for (BuWorkOrder order : orderList) {
            String orderCode = order.getOrderCode();
            if (maximoOrderCodeList.contains(orderCode)) {
                bothExistOrderCodeList.add(orderCode);
                onlyMaximoExistOrderCodeList.remove(orderCode);
            } else {
                onlyJdxExistOrderCodeList.add(orderCode);
            }
        }

        return new OrderDiff()
                .setJdxOrderCount(orderList.size())
                .setMaximoOrderCount(maximoOrderCodeList.size())
                .setBothExistOrderCount(bothExistOrderCodeList.size())
                .setBothExistOrderCodeList(bothExistOrderCodeList)
                .setOnlyJdxExistOrderCount(onlyJdxExistOrderCodeList.size())
                .setOnlyJdxExistOrderCodeList(onlyJdxExistOrderCodeList)
                .setOnlyMaximoExistOrderCount(onlyMaximoExistOrderCodeList.size())
                .setOnlyMaximoExistOrderCodeList(onlyMaximoExistOrderCodeList);
    }

    /**
     * @see BuWorkOrderThirdService#getNeedCloseOrderMaterialPriceZero()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PriceZero> getNeedCloseOrderMaterialPriceZero() throws Exception {
        return buWorkOrderThirdMapper.selectNeedCloseOrderMaterialPriceZero();
    }

    /**
     * @see BuWorkOrderThirdService#rewriteNeedCloseOrderMaterialPriceZero(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<PriceZero> rewriteNeedCloseOrderMaterialPriceZero(List<PriceZero> priceZeroList) throws Exception {
        for (PriceZero priceZero : priceZeroList) {
            BigDecimal currentMaximoPrice = priceZero.getCurrentMaximoPrice();
            boolean currentMaximoPriceNotZero = null != priceZero.getCurrentMaximoPrice() && !priceZero.getCurrentMaximoPrice().equals(BigDecimal.ZERO);
            if (currentMaximoPriceNotZero) {
                buWorkOrderThirdMapper.updateOrderMaterialActPrice(priceZero.getOrderMaterialActId(), currentMaximoPrice);
                buWorkOrderThirdMapper.updateGroupStockPrice(priceZero.getGroupStockId(), currentMaximoPrice);
                buWorkOrderThirdMapper.updateAssignDetailPrice(priceZero.getAssignDetailId(), currentMaximoPrice);
            }

            BigDecimal orderMaterialActPrice = buWorkOrderThirdMapper.selectOrderMaterialActPrice(priceZero.getOrderMaterialActId());
            BigDecimal groupStockPrice = buWorkOrderThirdMapper.selectGroupStockPrice(priceZero.getGroupStockId());
            BigDecimal assignDetailPrice = buWorkOrderThirdMapper.selectAssignDetailPrice(priceZero.getAssignDetailId());
            priceZero.setNewOrderMaterialActPrice(orderMaterialActPrice)
                    .setNewGroupStockPrice(groupStockPrice)
                    .setNewAssignDetailPrice(assignDetailPrice);
        }
        return priceZeroList;
    }


    private JdxWoIn transformToMaximoOrder(BuWorkOrder order) {
        if (null == order) {
            return null;
        }

        // 时间处理
        Date startTime = DateUtils.transToDayStart(order.getStartTime());
        Date finishTime = DateUtils.transToDayEnd(order.getFinishTime());
        long diffTimeMillis = finishTime.getTime() - startTime.getTime();
        BigDecimal hours = BigDecimal.valueOf((double) diffTimeMillis / (double) 3600000)
                .setScale(2, BigDecimal.ROUND_DOWN);
        // 最小0.01
        BigDecimal minHours = BigDecimal.valueOf(0.01D);
        if (hours.compareTo(minHours) < 0) {
            hours = minHours;
        }

        JdxWoIn jdxWoIn = new JdxWoIn()
                .setActfinish(order.getActFinish())
                .setActstart(order.getActStart())
                .setChangedate(order.getUpdateTime())
                .setCBigclass(transToCBigclass(order.getRepairProgramName()))
                // C_LINECODE是施工调度系统的，无需传参
//                .setCLinecode(order.getLineNum())
                .setCNDjclass(transToCNDjclass(order.getRepairProgramName()))
                .setCNZqclass(transToCNZqclass(order.getRepairProgramName()))
                .setCSpecialty("电客车")
                .setCStation(null)
                .setDescription(order.getOrderName())
                .setEstdur(hours.floatValue())
                .setFcprojectid(order.getFdProjectCode())
                .setFctaskid(order.getFdTaskCode())
                .setLocation(null)
                .setParent(null)
                .setReportdate(order.getCreateTime())
                // 如果传创建人，需要传入UUV的账号信息，并注意部门人员验证
                //TODO-zhaiyantao 2021/7/4 18:56 现在系统中好些用户工号不规范，传到maximo中肯定报部门和人员不匹配的错，先不传
                .setReportedby(null)
                .setStatus(transToStatus(order.getOrderStatus()))
                .setStatusdate(null)
                .setSupervisor(null)
                .setTargcompdate(finishTime)
                .setTargstartdate(startTime)
                .setWonum(order.getOrderCode())
                .setWorktype(transToWorktype(order.getRepairProgramName()))
                .setTransid(null)
                .setTransseq(1L);

        String trainId = thirdCommonMapper.selectTrainIdByTrainNo(order.getTrainNo());
        String workshopCode = thirdCommonMapper.selectDepartMaximoCodeById(order.getWorkshopId());

        // 设置作业资产设备编码
        if (StringUtils.isNotBlank(order.getAssetCode())) {
            // 有资产设备编码，用资产设备编码
            jdxWoIn.setAssetnum(order.getAssetCode());
        } else {
            // 没有资产设备编码，用车辆资产编码
            // 没有车辆资产编码，用车辆资产的上级
            if (StringUtils.isBlank(trainId)) {
                String topAssetNum = MaximoThirdConstant.TRAIN_PARENT_LINE123;
                String lineId = order.getLineId();
                if (lineId.equals("1") || lineId.equals("2") || lineId.equals("3")) {
                    topAssetNum = MaximoThirdConstant.TRAIN_PARENT_LINE123;
                } else if (lineId.equals("4")) {
                    topAssetNum = MaximoThirdConstant.TRAIN_PARENT_LINE4;
                }

                jdxWoIn.setAssetnum(topAssetNum);
            } else {
                jdxWoIn.setAssetnum(trainId);
            }
        }

        // 设置 部门人员等（UUV）
        // 正式使用车辆id，车辆id为从maximo同步过来时的assetnum
        jdxWoIn.setCrewid(StringUtils.isBlank(workshopCode) ? MaximoThirdConstant.JDX_WORKSHOP_1 : workshopCode)
                .setSiteid(StringUtils.isBlank(order.getLineId()) ? null : MaximoThirdConstant.SITE_PREFIX + order.getLineId());

        if (StringUtils.isNotBlank(order.getGroupId())) {
            String groupId = order.getGroupId();
            String monitorWorkNo = thirdCommonMapper.selectMonitorWorkNoByGroupId(groupId);
            String groupCode = thirdCommonMapper.selectDepartMaximoCodeById(groupId);

            if (StringUtils.isNotBlank(groupCode) && StringUtils.isNotBlank(monitorWorkNo)) {
                jdxWoIn.setLead(monitorWorkNo)
                        .setPersongroup(groupCode);
            }
        }

        return jdxWoIn;
    }

    private String transToWorktype(String repairProgramName) {
        /**
         * FHYD	防寒越冬
         * PM	预防维修
         * TM	临时维修
         * TR	车辆大修
         * EM	紧急维修
         * OM	车辆架修
         * AM	设备保养
         * CM	设备维修
         * EI	设备检查
         * FM	故障维修
         * AW	辅助施工
         * AT	设备试验
         * PM	预防性维护
         * AM	设备保养
         * TM	临时维修
         */

        if (StringUtils.isBlank(repairProgramName)) {
            return "OM";
        }
        if ("架修".equals(repairProgramName)) {
            return "OM";
        }
        if (repairProgramName.contains("大修")) {
            return "TR";
        }

        return "OM";
    }

    private String transToCBigclass(String repairProgramName) {
        /**
         * 日常保养
         * 大修
         * 中修
         * 专项修
         */

        if (StringUtils.isBlank(repairProgramName)) {
            return "中修";
        }
        if ("架修".equals(repairProgramName)) {
            return "中修";
        }
        if (repairProgramName.contains("大修")) {
            return "大修";
        }

        return "中修";
    }

    private String transToCNDjclass(String repairProgramName) {
        /**
         * 电客车	10	大修
         * 电客车	24	故障维修
         * 电客车	29	车辆架修
         * 电客车	34	日检
         * 电客车	35	均衡修
         * 电客车	36	特别修
         * 电客车	37	专项修
         */

        if (StringUtils.isBlank(repairProgramName)) {
            return "29";
        }
        if ("架修".equals(repairProgramName)) {
            return "29";
        }
        if (repairProgramName.contains("大修")) {
            return "10";
        }

        return "29";
    }

    private String transToCNZqclass(String repairProgramName) {
        /**
         * 01	日检
         * 02	周检
         * 03	双周检
         * 04	月检
         * 05	三月检
         * 06	半年检
         * 07	年检
         * 08	三年检
         * 09	临修
         * 10	两年检
         * 11	双日检
         * 12	二月检
         * 13	五年检
         * 14	六年检
         * 15	十年检
         * 16	非定期检
         */

        if (StringUtils.isBlank(repairProgramName)) {
            return "13";
        }
        if ("架修".equals(repairProgramName) || "5年大修".equals(repairProgramName)) {
            return "13";
        }
        if ("10年大修".equals(repairProgramName)) {
            return "15";
        }
        if ("15年大修".equals(repairProgramName)) {
            //TODO-zhaiyantao 2021/4/30 14:54 需要检修系统提供”十年检“对应的值
            return "16";
        }

        return "13";
    }

    private String transToStatus(Integer orderStatus) {
        /**
         * APPR	车间主任已审批
         * APPR1	已批准
         * APPR2	再编制
         * CAN	已取消
         * CANSG	施工调度系统取消
         * CLOSE	已关闭
         * COMP	已完成
         * HISTEDIT	编辑历史记录
         * INPRG	进行中
         * SCHAPPR	施工计划已批准
         * SCHCAN	施工计划取消
         * SCHGNRT	施工计划已生成
         * SCHREQ	施工计划申请
         * SDCANCEL	施工计划取消
         * WACNMG	中心主任审批
         * WACNPM	中心材料工程师审核
         * WAPPR	编制中
         * WAWSDP	车间生产调度审核
         * WAWSMG	车间主任审核
         * WAWSPM	车间专业工程师已审批
         * WMATL	等待物料
         * WPCOND	等待厂家处理
         * WSCH	等待计划
         */

        if (null == orderStatus) {
            return "APPR";
        }
        if (2 == orderStatus) {
            return "APPR";
        }
        if (4 == orderStatus) {
            return "CLOSE";
        }

        return "APPR";
    }

}
