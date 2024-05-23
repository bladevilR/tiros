package org.jeecg.modules.dispatch.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.workflow.bean.WfBizStatus;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFar;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanFarDetail;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearAmountVO;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanFarDetailMapper;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanFarMapper;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanFarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 远期规划 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Slf4j
@Service
public class BuRepairPlanFarServiceImpl extends ServiceImpl<BuRepairPlanFarMapper, BuRepairPlanFar> implements BuRepairPlanFarService {

    @Resource
    private BuRepairPlanFarMapper buRepairPlanFarMapper;

    @Resource
    private BuRepairPlanFarDetailMapper buRepairPlanFarDetailMapper;

    /**
     * @see BuRepairPlanFarService#page(BuRepairPlanFarYearQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRepairPlanFar> page(BuRepairPlanFarYearQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buRepairPlanFarMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuRepairPlanFarService#selectById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanFar selectById(String id) throws Exception {
        return buRepairPlanFarMapper.selectById(id);
    }

    /**
     * @see BuRepairPlanFarService#savePlanFar(BuRepairPlanFar)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean savePlanFar(BuRepairPlanFar buRepairPlanFar) throws Exception {
        // 获取总架修大修数量
        Integer middleRepair = 0;
        Integer hightRepair = 0;
        List<BuRepairPlanFarDetail> detailList = buRepairPlanFar.getDetailList();
        for (BuRepairPlanFarDetail buRepairPlanFarDetail : detailList) {
            int detailMiddleRepair = getRepairAmount(buRepairPlanFarDetail.getMiddleRepair());
            int detailHightRepair = getRepairAmount(buRepairPlanFarDetail.getHightRepair());
            middleRepair = middleRepair + detailMiddleRepair;
            hightRepair = hightRepair + detailHightRepair;
        }

        // 保存远期规划
        buRepairPlanFar
                .setMiddleRepair(middleRepair)
                .setHightRepair(hightRepair);
        buRepairPlanFarMapper.insert(buRepairPlanFar);

        // 保存远期规划明细
        String planFarId = buRepairPlanFar.getId();
        for (BuRepairPlanFarDetail buRepairPlanFarDetail : detailList) {
            buRepairPlanFarDetail.setLongPlanId(planFarId);
            buRepairPlanFarDetailMapper.insert(buRepairPlanFarDetail);
        }

        return true;
    }

    /**
     * @see BuRepairPlanFarService#updatePlanFar(BuRepairPlanFar)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updatePlanFar(BuRepairPlanFar buRepairPlanFar) throws Exception {
        String planFarId = buRepairPlanFar.getId();

        // 删除以前的远期规划明细
        LambdaQueryWrapper<BuRepairPlanFarDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(BuRepairPlanFarDetail::getLongPlanId, planFarId);
        buRepairPlanFarDetailMapper.delete(detailWrapper);

        // 获取总架修大修数量
        Integer middleRepair = 0;
        Integer hightRepair = 0;
        List<BuRepairPlanFarDetail> detailList = buRepairPlanFar.getDetailList();
        for (BuRepairPlanFarDetail buRepairPlanFarDetail : detailList) {
            int detailMiddleRepair = getRepairAmount(buRepairPlanFarDetail.getMiddleRepair());
            int detailHightRepair = getRepairAmount(buRepairPlanFarDetail.getHightRepair());
            middleRepair = middleRepair + detailMiddleRepair;
            hightRepair = hightRepair + detailHightRepair;

            // 保存远期规划明细
            buRepairPlanFarDetail.setLongPlanId(planFarId);
            buRepairPlanFarDetailMapper.insert(buRepairPlanFarDetail);
        }

        // 更新远期规划
        buRepairPlanFar
                .setMiddleRepair(middleRepair)
                .setHightRepair(hightRepair);
        buRepairPlanFarMapper.updateById(buRepairPlanFar);

        return true;
    }

    /**
     * @see BuRepairPlanFarService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> planFarIdList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(planFarIdList)) {
            LambdaQueryWrapper<BuRepairPlanFarDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(BuRepairPlanFarDetail::getLongPlanId, planFarIdList);
            buRepairPlanFarDetailMapper.delete(detailWrapper);

            LambdaQueryWrapper<WfBizStatus> wfWrapper = new LambdaQueryWrapper<>();
            wfWrapper.in(WfBizStatus::getBusinessKey, planFarIdList);
            new WfBizStatus().delete(wfWrapper);

            buRepairPlanFarMapper.deleteBatchIds(planFarIdList);
        }

        return true;
    }

    /**
     * @see BuRepairPlanFarService#getPlanAmountByYearAndDepotId(Integer, String)
     */
    @Transactional(readOnly = true)
    @Override
    public BuRepairPlanYearAmountVO getPlanAmountByYearAndDepotId(Integer year, String depotId) throws Exception {
        BuRepairPlanYearAmountVO planYearAmountVO = new BuRepairPlanYearAmountVO();
        planYearAmountVO.setYear(year);
        planYearAmountVO.setDepotId(depotId);

        int middleRepair = 0;
        int hightRepair = 0;
        List<BuRepairPlanFar> planFarList = buRepairPlanFarMapper.selectListByYearAndDepotId(year, depotId);
        if (CollectionUtils.isNotEmpty(planFarList)) {
            for (BuRepairPlanFar planFar : planFarList) {
                List<BuRepairPlanFarDetail> planFarDetailList = planFar.getDetailList();
                if (CollectionUtils.isNotEmpty(planFarDetailList)) {
                    for (BuRepairPlanFarDetail planFarDetail : planFarDetailList) {
                        int middleRepairAmount = getRepairAmount(planFarDetail.getMiddleRepair(), year);
                        middleRepair += middleRepairAmount;
                        int hightRepairAmount = getRepairAmount(planFarDetail.getHightRepair(), year);
                        hightRepair += hightRepairAmount;
                    }
                }
                planYearAmountVO.setDepotName(planFar.getDepotName());
            }
        }
        planYearAmountVO
                .setMiddleRepair(middleRepair)
                .setHightRepair(hightRepair);

        return planYearAmountVO;
    }

    private int getRepairAmount(String repairStr) throws Exception {
        // 格式：2020:0#2021:5#2022:5... 冒号分隔年份和该年架修数量，#分隔不同年份，必须每年都设置
        int totalAmount = 0;
        if (StringUtils.isBlank(repairStr)) {
            return totalAmount;
        }

        String[] years = repairStr.split("#");
        for (String yearStr : years) {
            if (yearStr.contains(":")) {
                int amount = Integer.parseInt(yearStr.split(":")[1]);
                totalAmount = totalAmount + amount;
            } else {
                throw new JeecgBootException("架大修数量参数错误");
            }
        }


        return totalAmount;
    }

    private int getRepairAmount(String repairStr, Integer year) throws Exception {
        // 格式：2020:0#2021:5#2022:5... 冒号分隔年份和该年架修数量，#分隔不同年份，必须每年都设置
        int totalAmount = 0;
        if (StringUtils.isBlank(repairStr)) {
            return totalAmount;
        }

        String[] years = repairStr.split("#");
        for (String yearStr : years) {
            if (yearStr.contains(":")) {
                if (year == Integer.parseInt(yearStr.split(":")[0])) {
                    int amount = Integer.parseInt(yearStr.split(":")[1]);
                    totalAmount = totalAmount + amount;
                }
            }
        }

        return totalAmount;
    }

}
