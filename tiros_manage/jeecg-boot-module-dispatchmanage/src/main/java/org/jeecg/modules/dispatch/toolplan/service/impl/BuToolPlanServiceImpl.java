package org.jeecg.modules.dispatch.toolplan.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.dispatch.toolplan.bean.BuToolPlan;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanCheckVO;
import org.jeecg.modules.dispatch.toolplan.bean.vo.BuToolPlanQueryVO;
import org.jeecg.modules.dispatch.toolplan.mapper.BuToolPlanMapper;
import org.jeecg.modules.dispatch.toolplan.service.BuToolPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工具(装)运用/保养计划 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-09
 */
@Slf4j
@Service
public class BuToolPlanServiceImpl extends ServiceImpl<BuToolPlanMapper, BuToolPlan> implements BuToolPlanService {

    @Resource
    private BuToolPlanMapper buToolPlanMapper;

    /**
     * @see BuToolPlanService#page(BuToolPlanQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuToolPlan> page(BuToolPlanQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {

        return buToolPlanMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuToolPlanService#saveToolPlan(BuToolPlan)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveToolPlan(BuToolPlan buToolPlan) throws Exception {
        BuToolPlanCheckVO checkVO = new BuToolPlanCheckVO();
        checkVO.setToolPlanId(buToolPlan.getId())
                .setToolId(buToolPlan.getToolId())
                .setStartTime(buToolPlan.getStartTime())
                .setEndTime(buToolPlan.getEndTime());
        checkConflict(checkVO);

        buToolPlanMapper.insert(buToolPlan);

        return true;
    }

    /**
     * @see BuToolPlanService#updateToolPlan(BuToolPlan)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateToolPlan(BuToolPlan buToolPlan) throws Exception {
        BuToolPlanCheckVO checkVO = new BuToolPlanCheckVO();
        checkVO.setToolPlanId(buToolPlan.getId())
                .setToolId(buToolPlan.getToolId())
                .setStartTime(buToolPlan.getStartTime())
                .setEndTime(buToolPlan.getEndTime());
        checkConflict(checkVO);

        buToolPlanMapper.updateById(buToolPlan);

        return true;
    }

    /**
     * @see BuToolPlanService#checkConflict(BuToolPlanCheckVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public boolean checkConflict(BuToolPlanCheckVO checkVO) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (null == checkVO.getStartTime() && null == checkVO.getEndTime()) {
            checkVO.setNowTime(new Date());
        }

        List<BuToolPlan> conflictToolPlanList = buToolPlanMapper.selectConflictToolPlanList(checkVO);
        if (CollectionUtils.isEmpty(conflictToolPlanList)) {
            return true;
        }

        for (BuToolPlan buToolPlan : conflictToolPlanList) {
            if (StringUtils.isNotBlank(checkVO.getToolPlanId()) && checkVO.getToolPlanId().equals(buToolPlan.getId())) {
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
     * @see BuToolPlanService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> toolPlanIdList = Arrays.asList(ids.split(","));

        buToolPlanMapper.deleteBatchIds(toolPlanIdList);

        return true;
    }

}
