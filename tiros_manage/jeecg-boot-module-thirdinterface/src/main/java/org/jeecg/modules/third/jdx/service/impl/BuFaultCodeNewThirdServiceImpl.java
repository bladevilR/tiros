package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.third.jdx.bean.BuFaultCategory;
import org.jeecg.modules.third.jdx.bean.BuFaultCodeNew;
import org.jeecg.modules.third.jdx.bean.BuFaultReason;
import org.jeecg.modules.third.jdx.bean.BuFaultSolution;
import org.jeecg.modules.third.jdx.mapper.BuFaultCategoryThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuFaultCodeNewThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuFaultReasonThirdMapper;
import org.jeecg.modules.third.jdx.mapper.BuFaultSolutionThirdMapper;
import org.jeecg.modules.third.jdx.service.BuFaultCodeNewThirdService;
import org.jeecg.modules.third.maximo.bean.JdxFailurelistOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 故障编码(新) 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-30
 */
@Slf4j
@Service
public class BuFaultCodeNewThirdServiceImpl extends ServiceImpl<BuFaultCodeNewThirdMapper, BuFaultCodeNew> implements BuFaultCodeNewThirdService {

    @Resource
    private BuFaultCodeNewThirdMapper buFaultCodeNewThirdMapper;
    @Resource
    private BuFaultCategoryThirdMapper buFaultCategoryThirdMapper;
    @Resource
    private BuFaultReasonThirdMapper buFaultReasonThirdMapper;
    @Resource
    private BuFaultSolutionThirdMapper buFaultSolutionThirdMapper;

//    private final String PREFIX_CODE = "CODE";
//    private final String PREFIX_REASON = "REASON";
//    private final String PREFIX_SOLUTION = "SOLUTION";
//    private final String PREFIX_CATEGORY = "CATEGORY";


    /**
     * @see BuFaultCodeNewThirdService#insertAllFaultCodeFromMaximoData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertAllFaultCodeFromMaximoData(List<JdxFailurelistOut> maximoFaultCodeList) throws Exception {
        Map<String, BuFaultCodeNew> needAddFaultCodeMap = new HashMap<>();
        Map<String, BuFaultReason> needAddFaultReasonMap = new HashMap<>();
        Map<String, BuFaultSolution> needAddFaultSolutionMap = new HashMap<>();
        Map<String, BuFaultCategory> needAddFaultCategoryMap = new HashMap<>();

        String sourceScene = "初始化";
        Date now = new Date();

        // 获取现有数据
        Integer dbFaultCodeCount = buFaultCodeNewThirdMapper.selectCount(Wrappers.emptyWrapper());
        Integer dbFaultReasonCount = buFaultReasonThirdMapper.selectCount(Wrappers.emptyWrapper());
        Integer dbFaultSolutionCount = buFaultSolutionThirdMapper.selectCount(Wrappers.emptyWrapper());
        Integer dbFaultCategoryCount = buFaultCategoryThirdMapper.selectCount(Wrappers.emptyWrapper());
        log.info(String.format(sourceScene + "同步maximo故障代码：架大修现有数据，【%s条代码】，【%s条原因】，【%s条措施】，【%s条分类】",
                dbFaultCodeCount, dbFaultReasonCount, dbFaultSolutionCount, dbFaultCategoryCount));
        // 删除现有数据
        buFaultCodeNewThirdMapper.delete(Wrappers.emptyWrapper());
        buFaultReasonThirdMapper.delete(Wrappers.emptyWrapper());
        buFaultSolutionThirdMapper.delete(Wrappers.emptyWrapper());
        buFaultCategoryThirdMapper.delete(Wrappers.emptyWrapper());
        log.info(sourceScene + "同步maximo故障代码：架大修现有数据，已被清空");

        // maximo数据分类
        List<JdxFailurelistOut> jdxCodeList = maximoFaultCodeList.stream()
                .filter(faultCode -> StringUtils.isBlank(faultCode.getFailtype()))
                .collect(Collectors.toList());
        List<JdxFailurelistOut> jdxReasonList = maximoFaultCodeList.stream()
                .filter(faultCode -> "PROBLEM".equals(faultCode.getFailtype()))
                .collect(Collectors.toList());
        List<JdxFailurelistOut> jdxSolutionList = maximoFaultCodeList.stream()
                .filter(faultCode -> "CAUSE".equals(faultCode.getFailtype()))
                .collect(Collectors.toList());
        List<JdxFailurelistOut> jdxCategoryList = maximoFaultCodeList.stream()
                .filter(faultCode -> "REMEDY".equals(faultCode.getFailtype()))
                .collect(Collectors.toList());

        log.info(String.format(sourceScene + "同步maximo故障代码：maximo全部数据，【%s条代码】，【%s条原因】，【%s条措施】，【%s条原因分类】",
                jdxCodeList.size(), jdxReasonList.size(), jdxSolutionList.size(), jdxCategoryList.size()));

        int codeCount = 0;
        int reasonCount = 0;
        int solutionCount = 0;
        int categoryCount = 0;
        // 代码
        if (CollectionUtils.isNotEmpty(jdxCodeList)) {
            jdxCodeList.sort(Comparator.comparing(JdxFailurelistOut::getFailurecode, Comparator.nullsLast(Comparator.naturalOrder())));

            // 获取顶级（即“专业”一级）
            Map<String, String> fltMajorCodeNameMap = new HashMap<>();
            List<JdxFailurelistOut> topJdxCodeList = jdxCodeList.stream()
                    .filter(jdxCode -> StringUtils.isBlank(jdxCode.getParent()))
                    .collect(Collectors.toList());
            for (JdxFailurelistOut topJdxCode : topJdxCodeList) {
                fltMajorCodeNameMap.put(topJdxCode.getFailurecode(), topJdxCode.getDescription());
            }

            // 这里的故障代码就等于部件编码
            // 例如 011401007 按2/2/2/3拆分为01 14 01 007：第一个01表示专业（01=电客车），第二个14表示系统，第三个01表示子系统，第四个007表示部件
            for (JdxFailurelistOut jdxCode : jdxCodeList) {
                if (StringUtils.isBlank(jdxCode.getFailurecode()) || StringUtils.isBlank(jdxCode.getDescription())) {
                    continue;
                }
                String failurecode = jdxCode.getFailurecode();
                int codeLength = failurecode.length();
                if (codeLength < 2) {
                    continue;
                }

                BuFaultCodeNew code = buildNewCode(jdxCode, fltMajorCodeNameMap, now);
                needAddFaultCodeMap.put(code.getFltCode(), code);
                codeCount++;
            }

        }

        // 原因
        if (CollectionUtils.isNotEmpty(jdxReasonList)) {
            jdxReasonList.sort(Comparator.comparing(JdxFailurelistOut::getFailurecode, Comparator.nullsLast(Comparator.naturalOrder())));

            for (JdxFailurelistOut jdxReason : jdxReasonList) {
                if (StringUtils.isBlank(jdxReason.getFailurecode()) || StringUtils.isBlank(jdxReason.getDescription())) {
                    continue;
                }

                BuFaultReason reason = buildNewReason(jdxReason);
                needAddFaultReasonMap.put(reason.getReasonCode(), reason);
                reasonCount++;
            }
        }

        // 措施
        if (CollectionUtils.isNotEmpty(jdxSolutionList)) {
            jdxSolutionList.sort(Comparator.comparing(JdxFailurelistOut::getFailurecode, Comparator.nullsLast(Comparator.naturalOrder())));

            for (JdxFailurelistOut jdxSolution : jdxSolutionList) {
                if (StringUtils.isBlank(jdxSolution.getFailurecode()) || StringUtils.isBlank(jdxSolution.getDescription())) {
                    continue;
                }

                BuFaultSolution solution = new BuFaultSolution()
                        .setId(jdxSolution.getFailurecode())
                        .setSolutionCode(jdxSolution.getFailurecode())
                        .setSolutionDesc(jdxSolution.getDescription());
                needAddFaultSolutionMap.put(solution.getSolutionCode(), solution);
                solutionCount++;
            }
        }

        // 原因分类
        if (CollectionUtils.isNotEmpty(jdxCategoryList)) {
            jdxCategoryList.sort(Comparator.comparing(JdxFailurelistOut::getFailurecode, Comparator.nullsLast(Comparator.naturalOrder())));

            for (JdxFailurelistOut jdxCategory : jdxCategoryList) {
                if (StringUtils.isBlank(jdxCategory.getFailurecode()) || StringUtils.isBlank(jdxCategory.getDescription())) {
                    continue;
                }

                BuFaultCategory category = new BuFaultCategory()
                        .setId(jdxCategory.getFailurecode())
                        .setCategoryCode(jdxCategory.getFailurecode())
                        .setCategoryDesc(jdxCategory.getDescription());
                needAddFaultCategoryMap.put(category.getCategoryCode(), category);
                categoryCount++;
            }
        }

        log.info(String.format(sourceScene + "同步maximo故障代码：maximo有效数据，【%s条代码】，【%s条原因】，【%s条措施】，【%s条原因分类】",
                codeCount, reasonCount, solutionCount, categoryCount));

        // 保存数据
        int codeInsertCount = needAddFaultCodeMap.size();
        if (!needAddFaultCodeMap.isEmpty()) {
            List<BuFaultCodeNew> needAddFaultCodeList = new ArrayList<>(needAddFaultCodeMap.values());
            needAddFaultCodeList.sort(Comparator.comparing(BuFaultCodeNew::getFltCode, Comparator.nullsLast(Comparator.naturalOrder())));

            List<List<BuFaultCodeNew>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultCodeList);
            for (List<BuFaultCodeNew> batchSub : batchSubList) {
                buFaultCodeNewThirdMapper.insertList(batchSub);
            }
        }
        int reasonInsertCount = needAddFaultReasonMap.size();
        if (!needAddFaultReasonMap.isEmpty()) {
            List<BuFaultReason> needAddFaultReasonList = new ArrayList<>(needAddFaultReasonMap.values());
            needAddFaultReasonList.sort(Comparator.comparing(BuFaultReason::getReasonCode, Comparator.nullsLast(Comparator.naturalOrder())));

            List<List<BuFaultReason>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultReasonList);
            for (List<BuFaultReason> batchSub : batchSubList) {
                buFaultReasonThirdMapper.insertList(batchSub);
            }
        }
        int solutionInsertCount = needAddFaultSolutionMap.size();
        if (!needAddFaultSolutionMap.isEmpty()) {
            List<BuFaultSolution> needAddFaultSolutionList = new ArrayList<>(needAddFaultSolutionMap.values());
            needAddFaultSolutionList.sort(Comparator.comparing(BuFaultSolution::getSolutionCode, Comparator.nullsLast(Comparator.naturalOrder())));

            List<List<BuFaultSolution>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultSolutionList);
            for (List<BuFaultSolution> batchSub : batchSubList) {
                buFaultSolutionThirdMapper.insertList(batchSub);
            }
        }
        int categoryInsertCount = needAddFaultCategoryMap.size();
        if (!needAddFaultCategoryMap.isEmpty()) {
            List<BuFaultCategory> needAddFaultCategoryList = new ArrayList<>(needAddFaultCategoryMap.values());
            needAddFaultCategoryList.sort(Comparator.comparing(BuFaultCategory::getCategoryCode, Comparator.nullsLast(Comparator.naturalOrder())));

            List<List<BuFaultCategory>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddFaultCategoryList);
            for (List<BuFaultCategory> batchSub : batchSubList) {
                buFaultCategoryThirdMapper.insertList(batchSub);
            }
        }
        log.info(String.format(sourceScene + "同步maximo故障代码：执行完毕，新增了【%s条分类】，【%s条代码】，【%s条原因】，【%s条措施】",
                categoryInsertCount, codeInsertCount, reasonInsertCount, solutionInsertCount));

        return true;
    }

    /**
     * @see BuFaultCodeNewThirdService#consumeMaximoTransChangeData(List)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean consumeMaximoTransChangeData(List<JdxFailurelistOut> changeList) throws Exception {
        if (CollectionUtils.isEmpty(changeList)) {
            return true;
        }

        String sourceScene = "定时";
        Date now = new Date();

        // 查询旧的数据
        List<BuFaultCodeNew> codeList = buFaultCodeNewThirdMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuFaultCodeNew> oldCodeMap = new HashMap<>();
        codeList.forEach(code -> oldCodeMap.put(code.getFltCode(), code));
        // 顶级（即“专业”一级）
        Map<String, String> fltMajorCodeNameMap = new HashMap<>();
        List<BuFaultCodeNew> topCodeList = codeList.stream()
                .filter(code -> StringUtils.isBlank(code.getParentCode()))
                .collect(Collectors.toList());
        for (BuFaultCodeNew topCode : topCodeList) {
            fltMajorCodeNameMap.put(topCode.getFltCode(), topCode.getFltName());
        }
        List<BuFaultReason> reasonList = buFaultReasonThirdMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuFaultReason> oldReasonMap = new HashMap<>();
        reasonList.forEach(reason -> oldReasonMap.put(reason.getReasonCode(), reason));
        List<BuFaultSolution> solutionList = buFaultSolutionThirdMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuFaultSolution> oldSolutionMap = new HashMap<>();
        solutionList.forEach(solution -> oldSolutionMap.put(solution.getSolutionCode(), solution));
        List<BuFaultCategory> categoryList = buFaultCategoryThirdMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuFaultCategory> oldCategoryMap = new HashMap<>();
        categoryList.forEach(category -> oldCategoryMap.put(category.getCategoryCode(), category));

        Map<String, BuFaultCodeNew> needAddCodeMap = new HashMap<>();
        Map<String, BuFaultCodeNew> needUpdateCodeMap = new HashMap<>();
        Map<String, BuFaultReason> needAddReasonMap = new HashMap<>();
        Map<String, BuFaultReason> needUpdateReasonMap = new HashMap<>();
        Map<String, BuFaultSolution> needAddSolutionMap = new HashMap<>();
        Map<String, BuFaultSolution> needUpdateSolutionMap = new HashMap<>();
        Map<String, BuFaultCategory> needAddCategoryMap = new HashMap<>();
        Map<String, BuFaultCategory> needUpdateCategoryMap = new HashMap<>();
        for (JdxFailurelistOut failurelist : changeList) {
            String failtype = failurelist.getFailtype();
            String transAction = failurelist.getTransAction();
            String failurecode = failurelist.getFailurecode();

            if ("Delete".equals(transAction)) {
                // 删除
                if (StringUtils.isBlank(failtype)) {
                    needAddCodeMap.remove(failurecode);
                    needUpdateCodeMap.remove(failurecode);
                    oldCodeMap.remove(failurecode);
                } else if ("PROBLEM".equals(failtype)) {
                    needAddReasonMap.remove(failurecode);
                    needUpdateReasonMap.remove(failurecode);
                    oldReasonMap.remove(failurecode);
                } else if ("CAUSE".equals(failtype)) {
                    needAddSolutionMap.remove(failurecode);
                    needUpdateSolutionMap.remove(failurecode);
                    oldSolutionMap.remove(failurecode);
                } else if ("REMEDY".equals(failtype)) {
                    needAddCategoryMap.remove(failurecode);
                    needUpdateCategoryMap.remove(failurecode);
                    oldCategoryMap.remove(failurecode);
                }
            } else if ("Add".equals(transAction) || "Replace".equals(transAction)) {
                // 新增或更新
                if (StringUtils.isBlank(failtype)) {
                    BuFaultCodeNew code = oldCodeMap.get(failurecode);
                    if (null == code) {
                        // 不存在新增
                        code = buildNewCode(failurelist, fltMajorCodeNameMap, now);

                        needAddCodeMap.put(code.getFltCode(), code);
                    } else {
                        // 存在更新数据
                        updateOldCode(code, failurelist, fltMajorCodeNameMap, now);

                        needUpdateCodeMap.put(code.getFltCode(), code);
                    }
                    oldCodeMap.put(code.getFltCode(), code);
                } else if ("PROBLEM".equals(failtype)) {
                    BuFaultReason reason = oldReasonMap.get(failurecode);
                    if (null == reason) {
                        // 不存在新增
                        reason = buildNewReason(failurelist);

                        needAddReasonMap.put(reason.getReasonCode(), reason);
                    } else {
                        // 存在更新数据
                        updateOldReason(reason, failurelist);

                        needUpdateReasonMap.put(reason.getReasonCode(), reason);
                    }
                    oldReasonMap.put(reason.getReasonCode(), reason);
                } else if ("CAUSE".equals(failtype)) {
                    BuFaultSolution solution = oldSolutionMap.get(failurecode);
                    if (null == solution) {
                        // 不存在新增
                        solution = buildNewSolution(failurelist);

                        needAddSolutionMap.put(solution.getSolutionCode(), solution);
                    } else {
                        // 存在更新数据
                        updateOldSolution(solution, failurelist);

                        needUpdateSolutionMap.put(solution.getSolutionCode(), solution);
                    }
                    oldSolutionMap.put(solution.getSolutionCode(), solution);
                } else if ("REMEDY".equals(failtype)) {
                    BuFaultCategory category = oldCategoryMap.get(failurecode);
                    if (null == category) {
                        // 不存在新增
                        category = buildNewCategory(failurelist);

                        needAddCategoryMap.put(category.getCategoryCode(), category);
                    } else {
                        // 存在更新数据
                        updateOldCategory(category, failurelist);

                        needUpdateCategoryMap.put(category.getCategoryCode(), category);
                    }
                    oldCategoryMap.put(category.getCategoryCode(), category);
                }
            }
        }

        // 保存数据
        saveCode(needAddCodeMap, needUpdateCodeMap, sourceScene);
        saveReason(needAddReasonMap, needUpdateReasonMap, sourceScene);
        saveSolution(needAddSolutionMap, needUpdateSolutionMap, sourceScene);
        saveCategory(needAddCategoryMap, needUpdateCategoryMap, sourceScene);

        // 日志中记录本次处理信息
        log.info(String.format(sourceScene + "从maximo同步数据--故障代码：处理故障代码信息%s条（代码：新增%s，更新%s；原因：新增%s，更新%s；措施：新增%s，更新%s；分类：新增%s，更新%s；）",
                changeList.size(),
                needAddCodeMap.size(), needUpdateCodeMap.size(),
                needAddReasonMap.size(), needUpdateReasonMap.size(),
                needAddSolutionMap.size(), needUpdateSolutionMap.size(),
                needAddCategoryMap.size(), needUpdateCategoryMap.size()));

        return true;
    }

    private BuFaultCodeNew buildNewCode(JdxFailurelistOut jdxCode,
                                        Map<String, String> fltMajorCodeNameMap,
                                        Date now) {
        String failurecode = jdxCode.getFailurecode();
        int codeLength = failurecode.length();

        BuFaultCodeNew code = new BuFaultCodeNew()
                .setId(failurecode)
                .setFltCode(failurecode)
                .setFltName(jdxCode.getDescription())
                .setSelfCreate(0)
                .setCreateTime(now)
                .setCreateBy("admin");
        if (2 == codeLength) {
            // 专业
            code.setFltLevel(0)
                    .setParentCode(null)
                    .setFltMajorCode(failurecode)
                    .setFltMajorName(jdxCode.getDescription());
            fltMajorCodeNameMap.put(failurecode, jdxCode.getDescription());
        } else if (4 == codeLength) {
            // 系统
            String fltMajorCode = failurecode.substring(0, 2);
            code.setFltLevel(1)
                    .setParentCode(fltMajorCode)
                    .setFltMajorCode(fltMajorCode)
                    .setFltMajorName(fltMajorCodeNameMap.get(fltMajorCode));
        } else if (6 == codeLength) {
            // 子系统
            String fltMajorCode = failurecode.substring(0, 2);
            code.setFltLevel(2)
                    .setParentCode(failurecode.substring(0, 4))
                    .setFltMajorCode(fltMajorCode)
                    .setFltMajorName(fltMajorCodeNameMap.get(fltMajorCode));
        } else {
            // 部件
            String fltMajorCode = failurecode.substring(0, 2);
            code.setFltLevel(3)
                    .setParentCode(failurecode.substring(0, 6))
                    .setFltMajorCode(fltMajorCode)
                    .setFltMajorName(fltMajorCodeNameMap.get(fltMajorCode));
        }
        return code;
    }

    private void updateOldCode(BuFaultCodeNew code,
                               JdxFailurelistOut jdxCode,
                               Map<String, String> fltMajorCodeNameMap,
                               Date now) {
        String failurecode = jdxCode.getFailurecode();
        int codeLength = failurecode.length();

        code.setFltName(jdxCode.getDescription())
                .setSelfCreate(0)
                .setUpdateTime(now)
                .setUpdateBy("admin");
        if (2 == codeLength) {
            // 专业
            code.setFltLevel(0)
                    .setParentCode(null)
                    .setFltMajorCode(failurecode)
                    .setFltMajorName(jdxCode.getDescription());
            fltMajorCodeNameMap.put(failurecode, jdxCode.getDescription());
        } else if (4 == codeLength) {
            // 系统
            String fltMajorCode = failurecode.substring(0, 2);
            code.setFltLevel(1)
                    .setParentCode(fltMajorCode)
                    .setFltMajorCode(fltMajorCode)
                    .setFltMajorName(fltMajorCodeNameMap.get(fltMajorCode));
        } else if (6 == codeLength) {
            // 子系统
            String fltMajorCode = failurecode.substring(0, 2);
            code.setFltLevel(2)
                    .setParentCode(failurecode.substring(0, 4))
                    .setFltMajorCode(fltMajorCode)
                    .setFltMajorName(fltMajorCodeNameMap.get(fltMajorCode));
        } else {
            // 部件
            String fltMajorCode = failurecode.substring(0, 2);
            code.setFltLevel(3)
                    .setParentCode(failurecode.substring(0, 6))
                    .setFltMajorCode(fltMajorCode)
                    .setFltMajorName(fltMajorCodeNameMap.get(fltMajorCode));
        }
    }

    private BuFaultReason buildNewReason(JdxFailurelistOut jdxReason) {
        return new BuFaultReason()
                .setId(jdxReason.getFailurecode())
                .setReasonCode(jdxReason.getFailurecode())
                .setReasonDesc(jdxReason.getDescription());
    }

    private void updateOldReason(BuFaultReason reason, JdxFailurelistOut jdxReason) {
        reason.setReasonCode(jdxReason.getFailurecode())
                .setReasonDesc(jdxReason.getDescription());
    }

    private BuFaultSolution buildNewSolution(JdxFailurelistOut jdxSolution) {
        return new BuFaultSolution()
                .setId(jdxSolution.getFailurecode())
                .setSolutionCode(jdxSolution.getFailurecode())
                .setSolutionDesc(jdxSolution.getDescription());
    }

    private void updateOldSolution(BuFaultSolution solution, JdxFailurelistOut jdxSolution) {
        solution.setSolutionCode(jdxSolution.getFailurecode())
                .setSolutionDesc(jdxSolution.getDescription());
    }

    private BuFaultCategory buildNewCategory(JdxFailurelistOut jdxCategory) {
        return new BuFaultCategory()
                .setId(jdxCategory.getFailurecode())
                .setCategoryCode(jdxCategory.getFailurecode())
                .setCategoryDesc(jdxCategory.getDescription());
    }

    private void updateOldCategory(BuFaultCategory category, JdxFailurelistOut jdxCategory) {
        category.setCategoryCode(jdxCategory.getFailurecode())
                .setCategoryDesc(jdxCategory.getDescription());
    }

    private void saveCode(Map<String, BuFaultCodeNew> needAddCodeMap,
                          Map<String, BuFaultCodeNew> needUpdateCodeMap,
                          String sourceScene) {
        if (!needAddCodeMap.isEmpty()) {
            List<BuFaultCodeNew> needAddList = new ArrayList<>(needAddCodeMap.values());
            List<List<BuFaultCodeNew>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddList);
            for (List<BuFaultCodeNew> batchSub : batchSubList) {
                buFaultCodeNewThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：新增了" + needAddList.size() + "条故障代码；");
        }
        if (!needUpdateCodeMap.isEmpty()) {
            List<BuFaultCodeNew> needUpdateList = new ArrayList<>(needUpdateCodeMap.values());
            List<List<BuFaultCodeNew>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateList);
            for (List<BuFaultCodeNew> batchSub : batchSubList) {
                buFaultCodeNewThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：更新了" + needUpdateList.size() + "条故障代码；");
        }
    }

    private void saveReason(Map<String, BuFaultReason> needAddReasonMap,
                            Map<String, BuFaultReason> needUpdateReasonMap,
                            String sourceScene) {
        if (!needAddReasonMap.isEmpty()) {
            List<BuFaultReason> needAddList = new ArrayList<>(needAddReasonMap.values());
            List<List<BuFaultReason>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddList);
            for (List<BuFaultReason> batchSub : batchSubList) {
                buFaultReasonThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：新增了" + needAddList.size() + "条故障原因；");
        }
        if (!needUpdateReasonMap.isEmpty()) {
            List<BuFaultReason> needUpdateList = new ArrayList<>(needUpdateReasonMap.values());
            List<List<BuFaultReason>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateList);
            for (List<BuFaultReason> batchSub : batchSubList) {
                buFaultReasonThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：更新了" + needUpdateList.size() + "条故障原因；");
        }
    }

    private void saveSolution(Map<String, BuFaultSolution> needAddSolutionMap,
                              Map<String, BuFaultSolution> needUpdateSolutionMap,
                              String sourceScene) {
        if (!needAddSolutionMap.isEmpty()) {
            List<BuFaultSolution> needAddList = new ArrayList<>(needAddSolutionMap.values());
            List<List<BuFaultSolution>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddList);
            for (List<BuFaultSolution> batchSub : batchSubList) {
                buFaultSolutionThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：新增了" + needAddList.size() + "条故障措施；");
        }
        if (!needUpdateSolutionMap.isEmpty()) {
            List<BuFaultSolution> needUpdateList = new ArrayList<>(needUpdateSolutionMap.values());
            List<List<BuFaultSolution>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateList);
            for (List<BuFaultSolution> batchSub : batchSubList) {
                buFaultSolutionThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：更新了" + needUpdateList.size() + "条故障措施；");
        }
    }

    private void saveCategory(Map<String, BuFaultCategory> needAddCategoryMap,
                              Map<String, BuFaultCategory> needUpdateCategoryMap,
                              String sourceScene) {
        if (!needAddCategoryMap.isEmpty()) {
            List<BuFaultCategory> needAddList = new ArrayList<>(needAddCategoryMap.values());
            List<List<BuFaultCategory>> batchSubList = DatabaseBatchSubUtil.batchSubList(needAddList);
            for (List<BuFaultCategory> batchSub : batchSubList) {
                buFaultCategoryThirdMapper.insertList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：新增了" + needAddList.size() + "条故障分类；");
        }
        if (!needUpdateCategoryMap.isEmpty()) {
            List<BuFaultCategory> needUpdateList = new ArrayList<>(needUpdateCategoryMap.values());
            List<List<BuFaultCategory>> batchSubList = DatabaseBatchSubUtil.batchSubList(needUpdateList);
            for (List<BuFaultCategory> batchSub : batchSubList) {
                buFaultCategoryThirdMapper.updateList(batchSub);
            }
            log.info(sourceScene + "同步maximo故障代码信息：更新了" + needUpdateList.size() + "条故障分类；");
        }
    }

}
