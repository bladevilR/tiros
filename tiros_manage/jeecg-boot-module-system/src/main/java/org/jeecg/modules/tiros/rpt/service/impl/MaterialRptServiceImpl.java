package org.jeecg.modules.tiros.rpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.tiros.rpt.service.MaterialRptService;
import org.jeecg.common.tiros.util.DatabaseBatchSubUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.board.cost.bean.BuRptBoardTrainMaterial;
import org.jeecg.modules.board.cost.mapper.BuRptBoardTrainMaterialMapper;
import org.jeecg.modules.dispatch.exchange.bean.BuBaseTrainRepairPeriod;
import org.jeecg.modules.dispatch.exchange.mapper.BuBaseTrainRepairPeriodMapper;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrder;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderMapper;
import org.jeecg.modules.produce.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.produce.cost.mapper.BuWorkOrderMaterialProduceMapper;
import org.jeecg.modules.report.cost.bean.BuRptBoardSysMaterial;
import org.jeecg.modules.report.cost.mapper.BuRptBoardSysMaterialMapper;
import org.jeecg.modules.tiros.rpt.bean.BuMaterialRptBO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 物料消耗统计 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
@Service
public class MaterialRptServiceImpl implements MaterialRptService {

    @Resource
    private BuWorkOrderMaterialProduceMapper buWorkOrderMaterialProduceMapper;
    @Resource
    private BuBaseTrainRepairPeriodMapper buBaseTrainRepairPeriodMapper;
    @Resource
    private BuRptBoardSysMaterialMapper buRptBoardSysMaterialMapper;
    @Resource
    private BuRptBoardTrainMaterialMapper buRptBoardTrainMaterialMapper;
    @Resource
    private BuWorkOrderMapper buWorkOrderMapper;


    /**
     * @see MaterialRptService#reBuildRptMaterial()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean reBuildRptMaterial() {
        // 1. 删除中间表的数据：  bu_rpt_board_train_material  bu_rpt_board_sys_material
        LambdaQueryWrapper<BuRptBoardSysMaterial> wrapper1 = new LambdaQueryWrapper<BuRptBoardSysMaterial>()
                .isNotNull(BuRptBoardSysMaterial::getId);
        buRptBoardSysMaterialMapper.delete(wrapper1);
        LambdaQueryWrapper<BuRptBoardTrainMaterial> wrapper2 = new LambdaQueryWrapper<BuRptBoardTrainMaterial>()
                .isNotNull(BuRptBoardTrainMaterial::getId);
        buRptBoardTrainMaterialMapper.delete(wrapper2);

        // 查找所有非发料工单，且已关闭的工单
        LambdaQueryWrapper<BuWorkOrder> wrapper3 = new LambdaQueryWrapper<BuWorkOrder>()
                .ne(BuWorkOrder::getOrderType, 4)
                .in(BuWorkOrder::getOrderStatus, Arrays.asList(3, 4))
                .select(BuWorkOrder::getId);
        List<BuWorkOrder> orders = buWorkOrderMapper.selectList(wrapper3);
        List<String> orderIdList = orders.stream()
                .map(BuWorkOrder::getId)
                .collect(Collectors.toList());

        // 构造工单物料成本中间统计表数据
        buildOrderMaterial(orderIdList);

        return true;
    }

    /**
     * @see MaterialRptService#reBuildRptMaterialByTrainNo(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean reBuildRptMaterialByTrainNo(String trainNo) {
        // 删除中间表数据
        LambdaQueryWrapper<BuRptBoardSysMaterial> wrapper1 = new LambdaQueryWrapper<BuRptBoardSysMaterial>()
                .isNotNull(BuRptBoardSysMaterial::getId)
                .eq(BuRptBoardSysMaterial::getTrainNo, trainNo);
        buRptBoardSysMaterialMapper.delete(wrapper1);
        LambdaQueryWrapper<BuRptBoardTrainMaterial> wrapper2 = new LambdaQueryWrapper<BuRptBoardTrainMaterial>()
                .isNotNull(BuRptBoardTrainMaterial::getId)
                .eq(BuRptBoardTrainMaterial::getTrainNo, trainNo);
        buRptBoardTrainMaterialMapper.delete(wrapper2);

        // 查找所有非发料工单，且已关闭的工单
        LambdaQueryWrapper<BuWorkOrder> wrapper3 = new LambdaQueryWrapper<BuWorkOrder>()
                .ne(BuWorkOrder::getOrderType, 4)
                .in(BuWorkOrder::getOrderStatus, Arrays.asList(3, 4))
                .eq(BuWorkOrder::getTrainNo, trainNo)
                .select(BuWorkOrder::getId);
        List<BuWorkOrder> orders = buWorkOrderMapper.selectList(wrapper3);
        List<String> orderIdList = orders.stream()
                .map(BuWorkOrder::getId)
                .collect(Collectors.toList());

        // 构造工单物料成本中间统计表数据
        buildOrderMaterial(orderIdList);

        return true;
    }


    private void buildOrderMaterial(List<String> orderIdList) {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return;
        }

        // 查询工单物料
        List<BuWorkOrderMaterial> orderMaterialList = new ArrayList<>();
        List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderIdList);
        for (List<String> batchSub : batchSubList) {
            List<BuWorkOrderMaterial> subOrderMaterialList = buWorkOrderMaterialProduceMapper.selectListForRptByOrderIdList(batchSub);
            orderMaterialList.addAll(subOrderMaterialList);
        }
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        // 查询车辆架修周期
//        Map<String, BuBaseTrainRepairPeriod> trainNoLastPeriodMap = new HashMap<>();
//        Set<String> trainNoSet = orderMaterialList.stream()
//                .map(BuWorkOrderMaterial::getTrainNo)
//                .collect(Collectors.toSet());
//        for (String trainNo : trainNoSet) {
//            if (StringUtils.isNotBlank(trainNo)) {
//                BuBaseTrainRepairPeriod period = buBaseTrainRepairPeriodMapper.selectLastTrainPeriod(trainNo);
//                if (null != period) {
//                    trainNoLastPeriodMap.put(trainNo, period);
//                }
//            }
//        }
        List<BuBaseTrainRepairPeriod> periodList = buBaseTrainRepairPeriodMapper.selectList(Wrappers.emptyWrapper());

        List<BuMaterialRptBO> materialRptBOList = new ArrayList<>();
        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            // 设置架修周期
//            BuBaseTrainRepairPeriod period = trainNoLastPeriodMap.get(orderMaterial.getTrainNo());
            BuBaseTrainRepairPeriod period = getRepairPeriodByOrderMaterial(periodList, orderMaterial);
            if (null != period) {
                orderMaterial.setPeriodId(period.getId());
                orderMaterial.setRepairIndex(period.getRepairIndex());
            }

            // 转换成物料统计bo
            transToMaterialRptBO(orderMaterial, materialRptBOList);
        }
        if (CollectionUtils.isEmpty(materialRptBOList)) {
            return;
        }

        // 更新物料统计数据
        for (BuMaterialRptBO materialRptBO : materialRptBOList) {
            updateMaterialRptData(materialRptBO);
        }
    }

    private BuBaseTrainRepairPeriod getRepairPeriodByOrderMaterial(List<BuBaseTrainRepairPeriod> periodList, BuWorkOrderMaterial orderMaterial) {
        if (CollectionUtils.isEmpty(periodList) || null == orderMaterial) {
            return null;
        }

        String programId = orderMaterial.getProgramId();
        if (StringUtils.isBlank(programId)) {
            return null;
        }
        if (null == orderMaterial.getConsumeTime()) {
            return null;
        }
        int year = DateUtils.getYear(orderMaterial.getConsumeTime());
        String lineId = orderMaterial.getLineId();
        String workshopId = orderMaterial.getWorkshopId();
        String companyId = orderMaterial.getCompanyId();

        List<BuBaseTrainRepairPeriod> filterList = new ArrayList<>();
        for (BuBaseTrainRepairPeriod period : periodList) {
            if (programId.equals(period.getRepairProgram())
                    && year == period.getYear()
                    && lineId.equals(period.getLineId())
                    && workshopId.equals(period.getWorkshopId())
                    && companyId.equals(period.getCompanyId())) {
                filterList.add(period);
            }
        }
        if (CollectionUtils.isEmpty(filterList)) {
            return null;
        } else {
            filterList.sort(Comparator.comparing(BuBaseTrainRepairPeriod::getRepairIndex, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            return filterList.get(0);
        }
    }

    /**
     * 工单物料转换成统计数据bo，并放入列表中
     *
     * @param orderMaterial     工单物料
     * @param materialRptBOList 统计数据列表
     */
    private void transToMaterialRptBO(BuWorkOrderMaterial orderMaterial, List<BuMaterialRptBO> materialRptBOList) {
        BuMaterialRptBO result = null;

        for (BuMaterialRptBO materialRptBO : materialRptBOList) {
            if (isSame(orderMaterial, materialRptBO)) {
                result = materialRptBO;

                addOrChangeMaterialRptBOSum(orderMaterial, result);

                break;
            }
        }
        if (result == null) {
            result = new BuMaterialRptBO()
                    .setDepotId(orderMaterial.getDepotId())
                    .setLineId(orderMaterial.getLineId())
                    .setWorkshopId(orderMaterial.getWorkshopId())
                    .setCompanyId(orderMaterial.getCompanyId())
                    .setRepairPeriod(StringUtils.isBlank(orderMaterial.getPeriodId()) ? "-1" : orderMaterial.getPeriodId())
                    .setTrainId(orderMaterial.getTrainId())
                    .setTrainNo(orderMaterial.getTrainNo())
                    .setRepairIndex(null == orderMaterial.getRepairIndex() ? -1 : orderMaterial.getRepairIndex())
                    .setYear(DateUtils.getYear(orderMaterial.getConsumeTime()))
                    .setMonth(DateUtils.getMonth(orderMaterial.getConsumeTime()))
                    .setProgramId(StringUtils.isBlank(orderMaterial.getProgramId()) ? "-1" : orderMaterial.getProgramId())
                    .setSysId(StringUtils.isBlank(orderMaterial.getSystemId()) ? "-1" : orderMaterial.getSystemId())
                    .setMustCost(BigDecimal.ZERO)
                    .setRandomCost(BigDecimal.ZERO)
                    .setConsumeCost(BigDecimal.ZERO);

            addOrChangeMaterialRptBOSum(orderMaterial, result);

            materialRptBOList.add(result);
        }
    }

    private boolean isSame(BuWorkOrderMaterial orderMaterial, BuMaterialRptBO materialRptBO) {
        if (StringUtils.isNotBlank(orderMaterial.getDepotId()) && !orderMaterial.getDepotId().equals(materialRptBO.getDepotId())) {
            return false;
        }
        if (StringUtils.isNotBlank(orderMaterial.getLineId()) && !orderMaterial.getLineId().equals(materialRptBO.getLineId())) {
            return false;
        }
        if (StringUtils.isNotBlank(orderMaterial.getWorkshopId()) && !orderMaterial.getWorkshopId().equals(materialRptBO.getWorkshopId())) {
            return false;
        }
        if (StringUtils.isNotBlank(orderMaterial.getCompanyId()) && !orderMaterial.getCompanyId().equals(materialRptBO.getCompanyId())) {
            return false;
        }
        if (StringUtils.isNotBlank(orderMaterial.getPeriodId()) && !orderMaterial.getPeriodId().equals(materialRptBO.getRepairPeriod())) {
            return false;
        }
        if (StringUtils.isNotBlank(orderMaterial.getTrainNo()) && !orderMaterial.getTrainNo().equals(materialRptBO.getTrainNo())) {
            return false;
        }
        if (null != orderMaterial.getRepairIndex() && !orderMaterial.getRepairIndex().equals(materialRptBO.getRepairIndex())) {
            return false;
        }
        if (null != orderMaterial.getConsumeTime()) {
            if (DateUtils.getYear(orderMaterial.getConsumeTime()) != materialRptBO.getYear()
                    || DateUtils.getMonth(orderMaterial.getConsumeTime()) != materialRptBO.getMonth()) {
                return false;
            }
        }
        if (StringUtils.isNotBlank(orderMaterial.getProgramId()) && !orderMaterial.getProgramId().equals(materialRptBO.getProgramId())) {
            return false;
        }
        if (StringUtils.isNotBlank(orderMaterial.getSystemId()) && !orderMaterial.getSystemId().equals(materialRptBO.getSysId())) {
            return false;
        }

        return true;
    }

    /**
     * 根据工单物料计算并更新统计数据bo
     *
     * @param orderMaterial 工单物料
     * @param materialRptBO 统计数据
     */
    private void addOrChangeMaterialRptBOSum(BuWorkOrderMaterial orderMaterial, BuMaterialRptBO materialRptBO) {
        int useCategory = null == orderMaterial.getUseCategory() ? -1 : orderMaterial.getUseCategory();

        BigDecimal cost = orderMaterial.getConsumeTotalPrice();

        BigDecimal consumeAmount = null == orderMaterial.getConsumeAmount() ? BigDecimal.ZERO : orderMaterial.getConsumeAmount();
        if (BigDecimal.ZERO.compareTo(consumeAmount) == 0) {
            consumeAmount = null == orderMaterial.getActAmount() ? BigDecimal.ZERO : BigDecimal.valueOf(orderMaterial.getActAmount());
            BigDecimal unitPrice = orderMaterial.getMaterialTypePrice();

            cost = consumeAmount.multiply(unitPrice);
        }

        if (1 == useCategory) {
            materialRptBO.setMustCost(cost.add(materialRptBO.getMustCost()));
        } else if (2 == useCategory) {
            materialRptBO.setRandomCost(cost.add(materialRptBO.getRandomCost()));
        } else if (3 == useCategory) {
            materialRptBO.setConsumeCost(cost.add(materialRptBO.getConsumeCost()));
        }
    }

    private void updateMaterialRptData(BuMaterialRptBO materialRptBO) {
        if (null == materialRptBO) {
            return;
        }

        // 获取查询wrapper
        LambdaQueryWrapper<BuRptBoardTrainMaterial> trainWrapper = initTrainQueryWrapper(materialRptBO);
        LambdaQueryWrapper<BuRptBoardSysMaterial> sysWrapper = initSysQueryWrapper(materialRptBO);
        if (null == trainWrapper || null == sysWrapper) {
            return;
        }

        // 车辆物料统计，不存在新增，存在增加数量
        BuRptBoardTrainMaterial trainMaterial = buRptBoardTrainMaterialMapper.selectOne(trainWrapper);
        if (null == trainMaterial) {
            trainMaterial = new BuRptBoardTrainMaterial();
            BeanUtils.copyProperties(materialRptBO, trainMaterial);
            trainMaterial.setId(UUIDGenerator.generate());
            buRptBoardTrainMaterialMapper.insert(trainMaterial);
        } else {
            increaseCost(trainMaterial, materialRptBO);
            buRptBoardTrainMaterialMapper.updateById(trainMaterial);
        }
        // 系统物料统计，不存在新增，存在增加数量
        BuRptBoardSysMaterial sysMaterial = buRptBoardSysMaterialMapper.selectOne(sysWrapper);
        if (null == sysMaterial) {
            sysMaterial = new BuRptBoardSysMaterial();
            BeanUtils.copyProperties(materialRptBO, sysMaterial);
            sysMaterial.setId(UUIDGenerator.generate());
            buRptBoardSysMaterialMapper.insert(sysMaterial);
        } else {
            increaseCost(sysMaterial, materialRptBO);
            buRptBoardSysMaterialMapper.updateById(sysMaterial);
        }
    }

    private LambdaQueryWrapper<BuRptBoardTrainMaterial> initTrainQueryWrapper(BuMaterialRptBO materialRptBO) {
        boolean valid = checkFaultRptBOValid(materialRptBO);
        if (!valid) {
            return null;
        }

        return new LambdaQueryWrapper<BuRptBoardTrainMaterial>()
                .eq(BuRptBoardTrainMaterial::getDepotId, materialRptBO.getDepotId())
                .eq(BuRptBoardTrainMaterial::getLineId, materialRptBO.getLineId())
                .eq(BuRptBoardTrainMaterial::getWorkshopId, materialRptBO.getWorkshopId())
                .eq(BuRptBoardTrainMaterial::getCompanyId, materialRptBO.getCompanyId())
                .eq(BuRptBoardTrainMaterial::getRepairPeriod, materialRptBO.getRepairPeriod())
                .eq(BuRptBoardTrainMaterial::getTrainId, materialRptBO.getTrainId())
                .eq(BuRptBoardTrainMaterial::getTrainNo, materialRptBO.getTrainNo())
                .eq(BuRptBoardTrainMaterial::getRepairIndex, materialRptBO.getRepairIndex())
                .eq(BuRptBoardTrainMaterial::getYear, materialRptBO.getYear())
                .eq(BuRptBoardTrainMaterial::getMonth, materialRptBO.getMonth())
                .eq(BuRptBoardTrainMaterial::getProgramId, materialRptBO.getProgramId());
    }

    private LambdaQueryWrapper<BuRptBoardSysMaterial> initSysQueryWrapper(BuMaterialRptBO materialRptBO) {
        boolean valid = checkFaultRptBOValid(materialRptBO);
        if (!valid) {
            return null;
        }

        return new LambdaQueryWrapper<BuRptBoardSysMaterial>()
                .eq(BuRptBoardSysMaterial::getDepotId, materialRptBO.getDepotId())
                .eq(BuRptBoardSysMaterial::getLineId, materialRptBO.getLineId())
                .eq(BuRptBoardSysMaterial::getWorkshopId, materialRptBO.getWorkshopId())
                .eq(BuRptBoardSysMaterial::getCompanyId, materialRptBO.getCompanyId())
                .eq(BuRptBoardSysMaterial::getRepairPeriod, materialRptBO.getRepairPeriod())
                .eq(BuRptBoardSysMaterial::getTrainId, materialRptBO.getTrainId())
                .eq(BuRptBoardSysMaterial::getTrainNo, materialRptBO.getTrainNo())
                .eq(BuRptBoardSysMaterial::getRepairIndex, materialRptBO.getRepairIndex())
                .eq(BuRptBoardSysMaterial::getYear, materialRptBO.getYear())
                .eq(BuRptBoardSysMaterial::getMonth, materialRptBO.getMonth())
                .eq(BuRptBoardSysMaterial::getProgramId, materialRptBO.getProgramId())
                .eq(BuRptBoardSysMaterial::getSysId, materialRptBO.getSysId());
    }

    private boolean checkFaultRptBOValid(BuMaterialRptBO materialRptBO) {
        if (StringUtils.isBlank(materialRptBO.getDepotId())) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getLineId())) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getWorkshopId())) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getCompanyId())) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getRepairPeriod())) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getTrainId())) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getTrainNo())) {
            return false;
        }
        if (null == materialRptBO.getRepairIndex()) {
            return false;
        }
        if (null == materialRptBO.getYear()) {
            return false;
        }
        if (null == materialRptBO.getMonth()) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getProgramId())) {
            return false;
        }
        if (StringUtils.isBlank(materialRptBO.getSysId())) {
            return false;
        }

        return true;
    }

    private void increaseCost(BuRptBoardTrainMaterial trainMaterial, BuMaterialRptBO materialRptBO) {
        trainMaterial.setMustCost(trainMaterial.getMustCost().add(materialRptBO.getMustCost()));
        trainMaterial.setRandomCost(trainMaterial.getRandomCost().add(materialRptBO.getRandomCost()));
        trainMaterial.setConsumeCost(trainMaterial.getConsumeCost().add(materialRptBO.getConsumeCost()));
    }

    private void increaseCost(BuRptBoardSysMaterial sysMaterial, BuMaterialRptBO materialRptBO) {
        sysMaterial.setMustCost(sysMaterial.getMustCost().add(materialRptBO.getMustCost()));
        sysMaterial.setRandomCost(sysMaterial.getRandomCost().add(materialRptBO.getRandomCost()));
        sysMaterial.setConsumeCost(sysMaterial.getConsumeCost().add(materialRptBO.getConsumeCost()));
    }

}
