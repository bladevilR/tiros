package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.constant.MaximoThirdConstant;
import org.jeecg.modules.third.jdx.bean.BuMaximoFinanceItem;
import org.jeecg.modules.third.jdx.bean.BuMtrLine;
import org.jeecg.modules.third.jdx.mapper.BuMaximoFinanceItemThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuTrainInfoThirdMapper;
import org.jeecg.modules.third.jdx.service.BuMaximoFinanceItemThirdService;
import org.jeecg.modules.third.maximo.bean.JdxProjectOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 财务项目 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
@Slf4j
@Service
public class BuMaximoFinanceItemThirdServiceImpl extends ServiceImpl<BuMaximoFinanceItemThirdMapper, BuMaximoFinanceItem> implements BuMaximoFinanceItemThirdService {

    @Resource
    private BuMaximoFinanceItemThirdMapper buMaximoFinanceItemThirdMapper;
    @Resource
    private BuTrainInfoThirdMapper buTrainInfoThirdMapper;


    /**
     * @see BuMaximoFinanceItemThirdService#insertAllFinanceFromMaximoData(List, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertAllFinanceFromMaximoData(List<JdxProjectOut> maximoProjectList, Boolean needUpdateOldBusinessTableData) throws Exception {
        if (CollectionUtils.isEmpty(maximoProjectList)) {
            return true;
        }

        String sourceScene = "初始化";
        Date now = new Date();

        // 更新旧的业务表关联财务项目id
        if (null == needUpdateOldBusinessTableData || needUpdateOldBusinessTableData) {
            updateOldBusinessTableData();
        }

        // 删除旧数据
        int deleteCount = buMaximoFinanceItemThirdMapper.delete(Wrappers.emptyWrapper());
        log.info(sourceScene + "同步maximo财务项目：删除了" + deleteCount + "条旧数据；");

        // 查询线路信息
        List<BuMtrLine> lineList = buTrainInfoThirdMapper.selectAllLine();
        // 查询旧的财务项目数据
        Map<String, BuMaximoFinanceItem> idFinanceMap = new HashMap<>();
        List<String> fincntrlidList = maximoProjectList.stream()
                .map(JdxProjectOut::getFincntrlid)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fincntrlidList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(fincntrlidList);
            for (List<String> batchSub : batchSubList) {
                List<BuMaximoFinanceItem> subFinanceList = buMaximoFinanceItemThirdMapper.selectBatchIds(batchSub);
                subFinanceList.forEach(finance -> idFinanceMap.put(finance.getId(), finance));
            }
        }

        Set<String> validFinanceIdSet = new HashSet<>();
        Map<String, BuMaximoFinanceItem> needAddIdFinanceMap = new HashMap<>();
        Map<String, BuMaximoFinanceItem> needUpdateIdFinanceMap = new HashMap<>();
        for (JdxProjectOut maximoProject : maximoProjectList) {
            String transAction = maximoProject.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                String fincntrlid = maximoProject.getFincntrlid();
                BuMaximoFinanceItem finance = idFinanceMap.get(fincntrlid);
                if (null != finance) {
                    needAddIdFinanceMap.remove(finance.getId());
                    needAddIdFinanceMap.remove(finance.getId());
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                String fincntrlid = maximoProject.getFincntrlid();
                BuMaximoFinanceItem finance = idFinanceMap.get(fincntrlid);
                if (null == finance) {
                    // 财务项目不存在新增财务项目
                    finance = buildNewFinance(maximoProject, lineList, sourceScene, now);
                    // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                    needAddIdFinanceMap.put(finance.getId(), finance);
                } else {
                    // 财务项目存在更新数据
                    updateOldFinance(maximoProject, lineList, sourceScene, now);
                    // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                    needUpdateIdFinanceMap.put(finance.getId(), finance);
                }
            }
        }

        // 保存数据
        saveFinanceData(maximoProjectList, needAddIdFinanceMap, needUpdateIdFinanceMap, sourceScene);

        // 新增或修改的财务项目是有效的
        validFinanceIdSet.addAll(needAddIdFinanceMap.keySet());
        validFinanceIdSet.addAll(needUpdateIdFinanceMap.keySet());
        // 删除财务项目旧数据
        deleteInvalidFinance(validFinanceIdSet, sourceScene);

        // 日志中记录本次处理信息
        log.info(String.format(sourceScene + "从maximo同步数据--财务项目：处理财务项目信息%s条（新增%s，更新%s，删除%s）",
                maximoProjectList.size(),
                needAddIdFinanceMap.size(),
                needUpdateIdFinanceMap.size(),
                validFinanceIdSet.size()));

        return true;
    }

    /**
     * @see BuMaximoFinanceItemThirdService#consumeMaximoTransChangeData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean consumeMaximoTransChangeData(List<JdxProjectOut> changeList) throws Exception {
        if (CollectionUtils.isEmpty(changeList)) {
            return true;
        }

        String sourceScene = "定时";
        Date now = new Date();

        // 查询线路信息
        List<BuMtrLine> lineList = buTrainInfoThirdMapper.selectAllLine();

        // 查询旧的财务项目数据
        Map<String, BuMaximoFinanceItem> idFinanceMap = new HashMap<>();
        List<String> fincntrlidList = changeList.stream()
                .map(JdxProjectOut::getFincntrlid)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fincntrlidList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(fincntrlidList);
            for (List<String> batchSub : batchSubList) {
                List<BuMaximoFinanceItem> subFinanceList = buMaximoFinanceItemThirdMapper.selectBatchIds(batchSub);
                subFinanceList.forEach(finance -> idFinanceMap.put(finance.getId(), finance));
            }
        }

        Map<String, BuMaximoFinanceItem> needAddIdFinanceMap = new HashMap<>();
        Map<String, BuMaximoFinanceItem> needUpdateIdFinanceMap = new HashMap<>();
        for (JdxProjectOut maximoProject : changeList) {
            String transAction = maximoProject.getTransAction();
            if ("Delete".equals(transAction)) {
                // 删除
                String fincntrlid = maximoProject.getFincntrlid();
                BuMaximoFinanceItem finance = idFinanceMap.get(fincntrlid);
                if (null != finance) {
                    needAddIdFinanceMap.remove(finance.getId());
                    needAddIdFinanceMap.remove(finance.getId());
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                String fincntrlid = maximoProject.getFincntrlid();
                BuMaximoFinanceItem finance = idFinanceMap.get(fincntrlid);
                if (null == finance) {
                    // 财务项目不存在新增财务项目
                    finance = buildNewFinance(maximoProject, lineList, sourceScene, now);
                    // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                    needAddIdFinanceMap.put(finance.getId(), finance);
                } else {
                    // 财务项目存在更新数据
                    updateOldFinance(maximoProject, lineList, sourceScene, now);
                    // 因为列表是按照transid正序排序的，后修改的会后处理，通过id替换掉保证只留一个最新的
                    needUpdateIdFinanceMap.put(finance.getId(), finance);
                }
            }
        }

        // 保存数据
        saveFinanceData(changeList, needAddIdFinanceMap, needUpdateIdFinanceMap, sourceScene);

        // 日志中记录本次处理信息
        List<String> transIdList = changeList.stream()
                .map(JdxProjectOut::getTransid)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
        log.info(String.format(sourceScene + "从maximo同步数据--财务项目：处理财务项目信息%s条（新增%s，更新%s），transIds=%s",
                changeList.size(),
                needAddIdFinanceMap.size(),
                needUpdateIdFinanceMap.size(),
                String.join(",", transIdList)));

        return true;
    }


    private void updateOldBusinessTableData() {
        // 查询旧数据，用于映射id为新的
        List<BuMaximoFinanceItem> oldProjectList = buMaximoFinanceItemThirdMapper.selectListUsed();
        Map<String, BuMaximoFinanceItem> idOldProjectMap = new HashMap<>();
        oldProjectList.forEach(oldProject -> idOldProjectMap.put(oldProject.getId(), oldProject));
        // 故障、工单任务、表单实例会有关联资产设备
        List<Map<String, Object>> tpPlanIdProjectIdList = buMaximoFinanceItemThirdMapper.selectTpPlanIdProjectIdList();
        List<Map<String, Object>> planIdProjectIdList = buMaximoFinanceItemThirdMapper.selectPlanIdProjectIdList();
        List<Map<String, Object>> orderIdProjectIdList = buMaximoFinanceItemThirdMapper.selectOrderIdProjectIdList();
        List<Map<String, Object>> historyOrderIdProjectIdList = buMaximoFinanceItemThirdMapper.selectHistoryOrderIdProjectIdList();
        // 计划模板
        if (CollectionUtils.isNotEmpty(tpPlanIdProjectIdList)) {
            List<Map<String, Object>> tpPlanIdNewProjectIdMapList = getIdNewAssetIdMapList(idOldProjectMap, tpPlanIdProjectIdList);

            if (CollectionUtils.isNotEmpty(tpPlanIdNewProjectIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(tpPlanIdNewProjectIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoFinanceItemThirdMapper.updateTpPlanProjectIdBatch(batchSub);
                }
            }
        }
        // 列计划
        if (CollectionUtils.isNotEmpty(planIdProjectIdList)) {
            List<Map<String, Object>> planIdNewProjectIdMapList = getIdNewAssetIdMapList(idOldProjectMap, planIdProjectIdList);

            if (CollectionUtils.isNotEmpty(planIdNewProjectIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(planIdNewProjectIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoFinanceItemThirdMapper.updatePlanProjectIdBatch(batchSub);
                }
            }
        }
        // 工单
        if (CollectionUtils.isNotEmpty(orderIdProjectIdList)) {
            List<Map<String, Object>> orderIdNewProjectIdMapList = getIdNewAssetIdMapList(idOldProjectMap, orderIdProjectIdList);

            if (CollectionUtils.isNotEmpty(orderIdNewProjectIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(orderIdNewProjectIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoFinanceItemThirdMapper.updateOrderProjectIdBatch(batchSub);
                }
            }
        }
        // 车辆履历作业记录
        if (CollectionUtils.isNotEmpty(historyOrderIdProjectIdList)) {
            List<Map<String, Object>> historyOrderIdNewProjectIdMapList = getIdNewAssetIdMapList(idOldProjectMap, historyOrderIdProjectIdList);

            if (CollectionUtils.isNotEmpty(historyOrderIdNewProjectIdMapList)) {
                List<List<Map<String, Object>>> batchSubList = DatabaseBatchSubUtil.batchSubList(historyOrderIdNewProjectIdMapList);
                for (List<Map<String, Object>> batchSub : batchSubList) {
                    buMaximoFinanceItemThirdMapper.updateHistoryOrderProjectIdBatch(batchSub);
                }
            }
        }
    }

    private List<Map<String, Object>> getIdNewAssetIdMapList(Map<String, BuMaximoFinanceItem> idOldFinanceMap,
                                                             List<Map<String, Object>> idOldProjectIdsMapList) {
        List<Map<String, Object>> idNewAssetIdMapList = new ArrayList<>();

        for (Map<String, Object> idOldProjectIdsMap : idOldProjectIdsMapList) {
            String id = (String) idOldProjectIdsMap.get("id");
            String fdProject = (String) idOldProjectIdsMap.get("fdProject");
            String fdTask = (String) idOldProjectIdsMap.get("fdTask");

            Map<String, Object> workInstIdNewAssetIdMap = new HashMap<>();
            workInstIdNewAssetIdMap.put("id", id);

            BuMaximoFinanceItem oldProject = idOldFinanceMap.get(fdProject);
            BuMaximoFinanceItem oldTask = idOldFinanceMap.get(fdTask);
            if (null != oldProject) {
                workInstIdNewAssetIdMap.put("fdProject", oldProject.getFcCode());
            }
            if (null != oldTask) {
                workInstIdNewAssetIdMap.put("fdTask", oldTask.getFcCode());
            }
            idNewAssetIdMapList.add(workInstIdNewAssetIdMap);
        }

        return idNewAssetIdMapList;
    }

    private BuMaximoFinanceItem buildNewFinance(JdxProjectOut maximoFinance, List<BuMtrLine> lineList, String sourceScene, Date now) {
        String fctype = maximoFinance.getFctype();

        BuMaximoFinanceItem financeItem = new BuMaximoFinanceItem()
                .setId(maximoFinance.getFincntrlid())
                .setType(fctype)
                .setName(maximoFinance.getDescription())
                .setFcCode(maximoFinance.getFincntrlid())
                .setParentFcCode(maximoFinance.getParentfincntrlid())
                .setProjectCode(maximoFinance.getProjectid())
                .setFcStatus(maximoFinance.getFcstatus())
                .setStartDate(maximoFinance.getStartdate())
                .setEndDate(maximoFinance.getEnddate())
                .setStatus(getStatus(maximoFinance.getDisabled()))
                .setRemark("maximo" + sourceScene + "同步；")
                .setMaximoOrgId(maximoFinance.getOrgid())
                .setMaximoSiteId(maximoFinance.getSiteid())
                .setCreateTime(now)
                .setCreateBy("admin");
        if ("PROJECT".equals(fctype)) {
            financeItem
                    .setCode(maximoFinance.getProjectid())
                    .setTaskLevel(null);
        } else if ("TASK".equals(fctype)) {
            int taskLevel = null == maximoFinance.getTasklevel() ? 1 : maximoFinance.getTasklevel().intValue();
            financeItem
                    .setCode(maximoFinance.getTaskid())
                    .setTaskLevel(taskLevel);
        }

        // 查找并设置线路信息
        transLine(maximoFinance, financeItem, lineList);

        return financeItem;
    }

    private void updateOldFinance(JdxProjectOut maximoFinance, List<BuMtrLine> lineList, String sourceScene, Date now) {
        String fctype = maximoFinance.getFctype();

        BuMaximoFinanceItem financeItem = new BuMaximoFinanceItem()
                .setType(fctype)
                .setName(maximoFinance.getDescription())
                .setParentFcCode(maximoFinance.getParentfincntrlid())
                .setProjectCode(maximoFinance.getProjectid())
                .setFcStatus(maximoFinance.getFcstatus())
                .setStartDate(maximoFinance.getStartdate())
                .setEndDate(maximoFinance.getEnddate())
                .setStatus(getStatus(maximoFinance.getDisabled()))
                .setRemark("maximo" + sourceScene + "同步修改；")
                .setMaximoOrgId(maximoFinance.getOrgid())
                .setMaximoSiteId(maximoFinance.getSiteid())
                .setUpdateTime(now)
                .setUpdateBy("admin");
        if ("PROJECT".equals(fctype)) {
            financeItem
                    .setCode(maximoFinance.getProjectid())
                    .setTaskLevel(null);
        } else if ("TASK".equals(fctype)) {
            int taskLevel = null == maximoFinance.getTasklevel() ? 1 : maximoFinance.getTasklevel().intValue();
            financeItem
                    .setCode(maximoFinance.getTaskid())
                    .setTaskLevel(taskLevel);
        }

        // 查找并设置线路信息
        transLine(maximoFinance, financeItem, lineList);
    }

    private Integer getStatus(Double disabled) {
        if (null == disabled) {
            return 1;
        }
        if (0D == disabled) {
            return 1;
        }
        return 0;
    }

    private void transLine(JdxProjectOut maximoFinance,
                           BuMaximoFinanceItem financeItem,
                           List<BuMtrLine> lineList) {
        String siteid = maximoFinance.getSiteid();
        String lineValue = siteid.replaceAll(MaximoThirdConstant.SITE_PREFIX, "");

        List<BuMtrLine> matchLineList = lineList.stream()
                .filter(line -> line.getLineId().equals(lineValue))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(matchLineList)) {
            financeItem.setLineId(matchLineList.get(0).getLineId());
        } else {
            financeItem.setLineId(lineValue);
        }
    }

    private void saveFinanceData(List<JdxProjectOut> maximoProjectList,
                                 Map<String, BuMaximoFinanceItem> needAddIdFinanceMap,
                                 Map<String, BuMaximoFinanceItem> needUpdateIdFinanceMap,
                                 String sourceScene) {
        log.info(sourceScene + "同步maximo财务项目信息：传入了" + maximoProjectList.size() + "条财务项目记录；");
        if (!needAddIdFinanceMap.isEmpty()) {
            List<BuMaximoFinanceItem> needAddFinanceList = new ArrayList<>(needAddIdFinanceMap.values());
            List<List<BuMaximoFinanceItem>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddFinanceList);
            for (List<BuMaximoFinanceItem> batchSub : batchSubList) {
                buMaximoFinanceItemThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo财务项目信息：新增了" + needAddFinanceList.size() + "条财务项目记录；");
        }
        if (!needUpdateIdFinanceMap.isEmpty()) {
            List<BuMaximoFinanceItem> needUpdateFinanceList = new ArrayList<>(needUpdateIdFinanceMap.values());
            List<List<BuMaximoFinanceItem>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateFinanceList);
            for (List<BuMaximoFinanceItem> batchSub : batchSubList) {
                buMaximoFinanceItemThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "同步maximo财务项目信息：更新了" + needUpdateFinanceList.size() + "条财务项目记录；");
        }
    }

    private void deleteInvalidFinance(Set<String> validFinanceIdSet, String sourceScene) {
        // 查询所有旧的id
        List<String> oldIdList = buMaximoFinanceItemThirdMapper.selectIdList();
        // 过滤掉有效的
        if (CollectionUtils.isNotEmpty(validFinanceIdSet)) {
            oldIdList.removeAll(validFinanceIdSet);
        }
        // 删除无效的
        if (CollectionUtils.isNotEmpty(oldIdList)) {
            List<List<String>> batchSubList = DatabaseBatchSubUtil.batchSubList(oldIdList);
            for (List<String> batchSub : batchSubList) {
                buMaximoFinanceItemThirdMapper.deleteBatchIds(batchSub);
            }
            log.info(sourceScene + "同步maximo财务项目信息：删除了" + oldIdList.size() + "条旧财务项目数据；");
        }
    }

}
