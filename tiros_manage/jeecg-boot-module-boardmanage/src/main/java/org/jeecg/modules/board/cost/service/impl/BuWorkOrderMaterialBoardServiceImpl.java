package org.jeecg.modules.board.cost.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.bean.vo.chart.PieChartVO;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.modules.board.cost.bean.BuSpecAssetPlan;
import org.jeecg.modules.board.cost.bean.BuWorkOrderMaterial;
import org.jeecg.modules.board.cost.bean.OutsourceAssetPrice;
import org.jeecg.modules.board.cost.bean.vo.BuBoardCostQueryVO;
import org.jeecg.modules.board.cost.bean.vo.BuMaterialAlertVO;
import org.jeecg.modules.board.cost.bean.vo.BuWorkshopCostItemVO;
import org.jeecg.modules.board.cost.bean.vo.WorkshopMonthCostData;
import org.jeecg.modules.board.cost.mapper.BuCostCustomMapper;
import org.jeecg.modules.board.cost.mapper.BuWorkOrderMaterialBoardMapper;
import org.jeecg.modules.board.cost.service.BuWorkOrderMaterialBoardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单物料 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-02
 */
@Service
public class BuWorkOrderMaterialBoardServiceImpl extends ServiceImpl<BuWorkOrderMaterialBoardMapper, BuWorkOrderMaterial> implements BuWorkOrderMaterialBoardService {

    @Resource
    private BuWorkOrderMaterialBoardMapper buWorkOrderMaterialBoardMapper;
    @Resource
    private BuCostCustomMapper buCostCustomMapper;


    /**
     * @see BuWorkOrderMaterialBoardService#getMaterialCostTopTen(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PieChartVO> getMaterialCostTopTen(BuBoardCostQueryVO queryVO) throws Exception {
        int resultSize = 10;
        List<PieChartVO> resultList = new ArrayList<>();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialBoardMapper.selectCostListByYearAndCondition(year, queryVO);

        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            // 设置消耗数量价格
            setConsumeAmountAndPrice(orderMaterialList);

            List<PieChartVO> pieChartVOList = new ArrayList<>();
            // 按物资类型id分组
            Map<String, List<BuWorkOrderMaterial>> materialTypeIdOrderMaterialListMap = orderMaterialList.stream()
                    .filter(orderMaterial -> StringUtils.isNotBlank(orderMaterial.getMaterialTypeId()))
                    .collect(Collectors.groupingBy(BuWorkOrderMaterial::getMaterialTypeId));
            for (Map.Entry<String, List<BuWorkOrderMaterial>> materialTypeIdOrderMaterialListEntry : materialTypeIdOrderMaterialListMap.entrySet()) {
//                String materialTypeId = materialTypeIdOrderMaterialListEntry.getKey();
                List<BuWorkOrderMaterial> materialList = materialTypeIdOrderMaterialListEntry.getValue();
                String materialTypeName = materialList.get(0).getMaterialTypeName();

                BigDecimal totalPrice = materialList.stream()
                        .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                PieChartVO pieChartVO = new PieChartVO()
                        .setItem(materialTypeName)
                        .setCount(totalPrice.doubleValue());
                pieChartVOList.add(pieChartVO);
            }
            pieChartVOList.sort(Comparator.comparing(PieChartVO::getCount).reversed());

            if (pieChartVOList.size() >= resultSize) {
                resultList = pieChartVOList.subList(0, resultSize);
            } else {
                resultList = pieChartVOList;
            }
        }

        return resultList;
    }

    /**
     * @see BuWorkOrderMaterialBoardService#getWorkshopMonthCostData(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public WorkshopMonthCostData getWorkshopMonthCostData(BuBoardCostQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }

        Date startDate = queryVO.getStartDate();
        Date endDate = queryVO.getEndDate();
        long millisDiff = endDate.getTime() - startDate.getTime();
        int dayDiff = (int) (millisDiff / (24 * 3600 * 1000));

        // 人工成本
        Integer userCount = buCostCustomMapper.countGroupUser(queryVO);
        Integer orderTaskHour = buCostCustomMapper.sumFinishOrderTaskUseTime(queryVO);
        if (null == orderTaskHour) {
            orderTaskHour = 0;
        }
        double userTimePercent = 0D;
        if (userCount > 0) {
            userTimePercent = PercentUtils.percent(orderTaskHour, userCount * dayDiff * 8);
        }

        // 工装成本
       /* List<String> toolIdList = buCostCustomMapper.selectWorkshopTool5IdList(queryVO);
        int toolCount = toolIdList.size();
        double toolUsePercent = 0D;
        if (toolCount > 0) {
            List<BuToolPlan> toolPlanList = buCostCustomMapper.selectToolUserPlanList(toolIdList, startDate, endDate);
            long toolUseMillis = 0;
            if (CollectionUtils.isNotEmpty(toolPlanList)) {
                for (BuToolPlan toolPlan : toolPlanList) {
                    Date userStartTime = toolPlan.getStartTime().before(startDate) ? startDate : toolPlan.getStartTime();
                    Date userEndTime = toolPlan.getEndTime().after(endDate) ? endDate : toolPlan.getEndTime();
                    toolUseMillis = toolUseMillis + (userEndTime.getTime() - userStartTime.getTime());
                }
            }
            toolUsePercent = PercentUtils.percent(toolUseMillis, toolCount * millisDiff);
        }*/
        //特种设备成本
        List<String> speAssetIdList = buCostCustomMapper.selectWorkshopSpeAssetIdList(queryVO);
        int speAssetCount = speAssetIdList.size();
        double speAssetUsePercent = 0D;

        if (speAssetCount > 0) {
            List<BuSpecAssetPlan> specAssetPlanList = buCostCustomMapper.selectSpeAssetUserPlanList(speAssetIdList, startDate, endDate);
            long speAssetUseMillis = 0;
            if (CollectionUtils.isNotEmpty(specAssetPlanList)) {
                for (BuSpecAssetPlan specAssetPlan : specAssetPlanList) {
                    Date userStartTime = specAssetPlan.getStartTime().before(startDate) ? startDate : specAssetPlan.getStartTime();
                    Date userEndTime = specAssetPlan.getEndTime().after(endDate) ? endDate : specAssetPlan.getEndTime();
                    speAssetUseMillis = speAssetUseMillis + (userEndTime.getTime() - userStartTime.getTime());
                }
            }
            speAssetUsePercent = PercentUtils.percent(speAssetUseMillis, speAssetCount * millisDiff);
        }


        // 委外成本
        // 委外支付关联到委外合同，按合同中线路、车辆段、车号、修程、支付记录中支付时间过滤
      /*  BigDecimal outsourcePayCount = buCostCustomMapper.sumOutsourcePayAmount(queryVO);
        if (null == outsourcePayCount) {
            outsourcePayCount = BigDecimal.ZERO;
        }*/
        Integer outsourceAssetCount = 0;
        BigDecimal outsourcePayCount = BigDecimal.ZERO;
        List<OutsourceAssetPrice> outsourceAssetPriceList = buCostCustomMapper.sumOutsourceAsset(queryVO);
        if (CollectionUtils.isNotEmpty(outsourceAssetPriceList)) {
            outsourceAssetCount = outsourceAssetPriceList.stream().map(OutsourceAssetPrice::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).intValue();
            outsourcePayCount = outsourceAssetPriceList.stream().map(OutsourceAssetPrice::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
      /*  if (null == outsourceAssetCount) {
            outsourceAssetCount = 0;
        }*/

        // 物资消耗
        BigDecimal materialNum = BigDecimal.ZERO;
        BigDecimal materialCost = BigDecimal.ZERO;
        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialBoardMapper.selectCostListByCondition(queryVO);
        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            // 设置消耗数量价格
            setConsumeAmountAndPrice(orderMaterialList);

            for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
                materialNum = materialNum.add(orderMaterial.getConsumeAmount());
                materialCost = materialCost.add(orderMaterial.getConsumeTotalPrice());
            }
        }

        return new WorkshopMonthCostData()
                .setUserNum(userCount)
                .setUserTimePercent(userTimePercent)
                .setToolNum(speAssetCount)
                .setToolUsePercent(speAssetUsePercent)
                .setOutsourceCost(outsourcePayCount)
                .setOutsourceAssetNum(outsourceAssetCount)
                .setMaterialCost(materialCost)
                .setMaterialNum(materialNum);
    }

    /**
     * @see BuWorkOrderMaterialBoardService#listMaterialCostItem(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkshopCostItemVO> listMaterialCostItem(BuBoardCostQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }

        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialBoardMapper.selectCostListByCondition(queryVO);

        BuWorkshopCostItemVO total = new BuWorkshopCostItemVO().setName("总物料消耗").setQuantity(0D).setCost(0D);
        BuWorkshopCostItemVO must = new BuWorkshopCostItemVO().setName("必换件消耗").setQuantity(0D).setCost(0D);
        BuWorkshopCostItemVO random = new BuWorkshopCostItemVO().setName("偶换件消耗").setQuantity(0D).setCost(0D);
        BuWorkshopCostItemVO consume = new BuWorkshopCostItemVO().setName("耗材消耗").setQuantity(0D).setCost(0D);

        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            // 设置消耗数量价格
            setConsumeAmountAndPrice(orderMaterialList);

            Map<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListMap = orderMaterialList.stream()
                    .collect(Collectors.groupingBy(BuWorkOrderMaterial::getUseCategory));
            for (Map.Entry<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListEntry : useCategoryOrderMaterialListMap.entrySet()) {
                Integer useCategory = useCategoryOrderMaterialListEntry.getKey();
                List<BuWorkOrderMaterial> materialList = useCategoryOrderMaterialListEntry.getValue();

                BigDecimal quantity = materialList.stream()
                        .map(BuWorkOrderMaterial::getConsumeAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalPrice = materialList.stream()
                        .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (useCategory == 1) {
                    must.setQuantity(quantity.doubleValue()).setCost(totalPrice.doubleValue());
                }
                if (useCategory == 2) {
                    random.setQuantity(quantity.doubleValue()).setCost(totalPrice.doubleValue());
                }
                if (useCategory == 3) {
                    consume.setQuantity(quantity.doubleValue()).setCost(totalPrice.doubleValue());
                }
            }
            total.setQuantity(must.getQuantity() + random.getQuantity() + consume.getQuantity());
            total.setCost(must.getCost() + random.getCost() + consume.getCost());
        }

        return Arrays.asList(total, must, random, consume);
    }

    /**
     * @see BuWorkOrderMaterialBoardService#listMaterialCostProportion(BuBoardCostQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<PieChartVO> listMaterialCostProportion(BuBoardCostQueryVO queryVO) throws Exception {
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            // 设置本月时间
            queryVO.setCurrentMonthQueryDate();
        }

        List<BuWorkOrderMaterial> orderMaterialList = buWorkOrderMaterialBoardMapper.selectCostListByCondition(queryVO);

        PieChartVO must = new PieChartVO().setItem("必换件").setCount(0D);
        PieChartVO random = new PieChartVO().setItem("偶换件").setCount(0D);
        PieChartVO consume = new PieChartVO().setItem("耗材").setCount(0D);

        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            // 设置消耗数量价格
            setConsumeAmountAndPrice(orderMaterialList);

            Map<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListMap = orderMaterialList.stream()
                    .collect(Collectors.groupingBy(BuWorkOrderMaterial::getUseCategory));
            for (Map.Entry<Integer, List<BuWorkOrderMaterial>> useCategoryOrderMaterialListEntry : useCategoryOrderMaterialListMap.entrySet()) {
                Integer useCategory = useCategoryOrderMaterialListEntry.getKey();
                List<BuWorkOrderMaterial> materialList = useCategoryOrderMaterialListEntry.getValue();

                BigDecimal totalPrice = materialList.stream()
                        .map(BuWorkOrderMaterial::getConsumeTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (useCategory == 1) {
                    must.setCount(totalPrice.doubleValue());
                }
                if (useCategory == 2) {
                    random.setCount(totalPrice.doubleValue());
                }
                if (useCategory == 3) {
                    consume.setCount(totalPrice.doubleValue());
                }
            }
        }

        return Arrays.asList(must, random, consume);
    }

    /**
     * @see BuWorkOrderMaterialBoardService#listMaterialThresholdAlertTopTen()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialAlertVO> listMaterialThresholdAlertTopTen() throws Exception {
        List<BuMaterialAlertVO> materialAlertVOList = buWorkOrderMaterialBoardMapper.selectMaterialAlertVOList();
        if (CollectionUtils.isEmpty(materialAlertVOList)) {
            return new ArrayList<>();
        }

        return getDiffStockTopTen(materialAlertVOList);
    }


    private void setConsumeAmountAndPrice(List<BuWorkOrderMaterial> orderMaterialList) {
        if (CollectionUtils.isEmpty(orderMaterialList)) {
            return;
        }

        for (BuWorkOrderMaterial orderMaterial : orderMaterialList) {
            if (null == orderMaterial.getConsumeAmount()) {
                orderMaterial.setConsumeAmount(BigDecimal.ZERO);
            }
            if (null == orderMaterial.getConsumeTotalPrice()) {
                orderMaterial.setConsumeTotalPrice(BigDecimal.ZERO);
            }
            if (BigDecimal.ZERO.compareTo(orderMaterial.getConsumeAmount()) < 0) {
                orderMaterial.setUnitPrice(orderMaterial.getConsumeTotalPrice().divide(orderMaterial.getConsumeAmount(), 8, BigDecimal.ROUND_HALF_UP));
            } else {
                orderMaterial.setUnitPrice(BigDecimal.ZERO);
            }
        }

        orderMaterialList.removeIf(orderMaterial ->
                BigDecimal.ZERO.compareTo(orderMaterial.getConsumeAmount()) >= 0
                        && BigDecimal.ZERO.compareTo(orderMaterial.getConsumeTotalPrice()) >= 0
                        && BigDecimal.ZERO.compareTo(orderMaterial.getUnitPrice()) >= 0);
    }

    private List<BuMaterialAlertVO> getDiffStockTopTen(List<BuMaterialAlertVO> materialAlertVOList) {
        for (BuMaterialAlertVO alertVO : materialAlertVOList) {
            alertVO.setDiffStock(alertVO.getAlertStock() - alertVO.getCurrentStock());
        }

        // 过滤出偏差值大于0的，并排序
        List<BuMaterialAlertVO> needAlterVOList = materialAlertVOList.stream()
                .filter(alertVO -> null != alertVO && alertVO.getDiffStock() > 0)
                .sorted(Comparator.comparing(BuMaterialAlertVO::getDiffStock).reversed())
                .collect(Collectors.toList());

        // 设置序号
        AtomicInteger index = new AtomicInteger(1);
        for (BuMaterialAlertVO alertVO : needAlterVOList) {
            alertVO.setOrder(index.getAndIncrement());
        }

        int resultSize = 10;
        if (needAlterVOList.size() > resultSize) {
            needAlterVOList = needAlterVOList.subList(0, resultSize);
        }

        return needAlterVOList;
    }

}
