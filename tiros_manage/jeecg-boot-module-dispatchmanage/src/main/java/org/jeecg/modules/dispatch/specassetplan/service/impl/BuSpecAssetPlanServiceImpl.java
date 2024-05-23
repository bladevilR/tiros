package org.jeecg.modules.dispatch.specassetplan.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.dispatch.specassetplan.bean.BuSpecAssetPlan;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetMonthUsageQueryVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanCheckVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanQueryVO;
import org.jeecg.modules.dispatch.specassetplan.bean.vo.BuSpecAssetPlanVO;
import org.jeecg.modules.dispatch.specassetplan.mapper.BuSpecAssetPlanMapper;
import org.jeecg.modules.dispatch.specassetplan.service.BuSpecAssetPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 特种设备运用/保养计划 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-03
 */
@Service
public class BuSpecAssetPlanServiceImpl extends ServiceImpl<BuSpecAssetPlanMapper, BuSpecAssetPlan> implements BuSpecAssetPlanService {

    @Resource
    private BuSpecAssetPlanMapper buSpecAssetPlanMapper;


    /**
     * @see BuSpecAssetPlanService#page(BuSpecAssetPlanQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuSpecAssetPlan> page(BuSpecAssetPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtils.transToDayEnd(queryVO.getEndDate()));
        }
        return buSpecAssetPlanMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuSpecAssetPlanService#getSpecAssetPlanById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuSpecAssetPlan getSpecAssetPlanById(String id) throws Exception {
        return buSpecAssetPlanMapper.selectSpecAssetPlanById(id);
    }

    /**
     * @see BuSpecAssetPlanService#checkConflict(BuSpecAssetPlanCheckVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public boolean checkConflict(BuSpecAssetPlanCheckVO checkVO) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (null == checkVO.getStartTime() && null == checkVO.getEndTime()) {
            checkVO.setNowTime(new Date());
        }

        List<BuSpecAssetPlan> conflictToolPlanList = buSpecAssetPlanMapper.selectConflictToolPlanList(checkVO);
        if (CollectionUtils.isEmpty(conflictToolPlanList)) {
            return true;
        }

        for (BuSpecAssetPlan buToolPlan : conflictToolPlanList) {
            if (StringUtils.isNotBlank(checkVO.getSpecAssetPlanId()) && checkVO.getSpecAssetPlanId().equals(buToolPlan.getId())) {
                continue;
            }
            if (buToolPlan.getPlanType() == 1) {
                throw new JeecgBootException(String.format("该工具（装）%s至%s已有运用计划", dateFormat.format(buToolPlan.getStartTime()), dateFormat.format(buToolPlan.getEndTime())));
            }
            if (buToolPlan.getPlanType() == 2) {
                throw new JeecgBootException(String.format("该工具（装）%s至%s已有保养计划", dateFormat.format(buToolPlan.getStartTime()), dateFormat.format(buToolPlan.getEndTime())));
            }
        }

        return true;
    }

    /**
     * @see BuSpecAssetPlanService#addSpecAssetPlan(BuSpecAssetPlan)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addSpecAssetPlan(BuSpecAssetPlan specAssetPlan) throws Exception {
        BuSpecAssetPlanCheckVO checkVO = new BuSpecAssetPlanCheckVO();
        checkVO.setSpecAssetPlanId(specAssetPlan.getId())
                .setSpecAssetId(specAssetPlan.getSpecAssetId())
                .setStartTime(specAssetPlan.getStartTime())
                .setEndTime(specAssetPlan.getEndTime());
        checkConflict(checkVO);

        buSpecAssetPlanMapper.insert(specAssetPlan);

        return true;
    }

    /**
     * @see BuSpecAssetPlanService#updateSpecAssetPlan(BuSpecAssetPlan)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateSpecAssetPlan(BuSpecAssetPlan specAssetPlan) throws Exception {
        BuSpecAssetPlanCheckVO checkVO = new BuSpecAssetPlanCheckVO();
        checkVO.setSpecAssetPlanId(specAssetPlan.getId())
                .setSpecAssetId(specAssetPlan.getSpecAssetId())
                .setStartTime(specAssetPlan.getStartTime())
                .setEndTime(specAssetPlan.getEndTime());
        checkConflict(checkVO);

        buSpecAssetPlanMapper.updateById(specAssetPlan);

        return true;
    }

    /**
     * @see BuSpecAssetPlanService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> specAssetPlanIdList = Arrays.asList(ids.split(","));
        buSpecAssetPlanMapper.deleteBatchIds(specAssetPlanIdList);

        return true;
    }

    /**
     * @see BuSpecAssetPlanService#getSpecAssetMonthUsage(BuSpecAssetMonthUsageQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public LinkedHashMap<String, BuSpecAssetPlanVO> getSpecAssetMonthUsage(BuSpecAssetMonthUsageQueryVO usageQueryVO) throws Exception {
        Integer year = usageQueryVO.getYear();
        Integer month = usageQueryVO.getMonth();
        String specAssetId = usageQueryVO.getSpecAssetId();

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.YEAR, year);
        startCalendar.set(Calendar.MONTH, month - 1);
        DateUtils.transToMonthStartDay(startCalendar);
        DateUtils.transToDayStart(startCalendar);
        Date startTime = startCalendar.getTime();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.YEAR, year);
        endCalendar.set(Calendar.MONTH, month - 1);
        DateUtils.transToMonthEndDay(endCalendar);
        DateUtils.transToDayEnd(endCalendar);
        Date endTime = endCalendar.getTime();

        List<BuSpecAssetPlan> specAssetPlanList = buSpecAssetPlanMapper.selectListBySpecAssetIdAndTime(specAssetId, startTime, endTime);

        LinkedHashMap<String, BuSpecAssetPlanVO> resultMap = new LinkedHashMap<>();
        while (!endCalendar.before(startCalendar)) {
            Date day = startCalendar.getTime();

            List<BuSpecAssetPlan> dayPlanList = specAssetPlanList.stream()
                    .filter(plan -> DateUtils.containsDay(plan.getStartTime(), plan.getEndTime(), day))
                    .collect(Collectors.toList());

            String value = "空闲";
            Integer type=1;
            if (CollectionUtils.isNotEmpty(dayPlanList)) {
                long useCount = dayPlanList.stream()
                        .filter(plan -> 1 == plan.getPlanType())
                        .count();
                long maintainCount = dayPlanList.stream()
                        .filter(plan -> 2 == plan.getPlanType())
                        .count();
                   String remark=dayPlanList.get(0).getRemark();
                if (useCount > 0) {
                    value =remark;
                    type=2;
                }
                if (maintainCount > 0) {
                    value =remark;
                    type=3;
                }
                if (useCount > 0 && maintainCount > 0) {
                    value =remark;;
                    type=4;
                }
            }

            String key = month + "-" + startCalendar.get(Calendar.DAY_OF_MONTH);
            resultMap.put(key, new BuSpecAssetPlanVO().setType(type).setValue(value));
            startCalendar.add(Calendar.DATE, 1);
        }

        return resultMap;
    }

}
