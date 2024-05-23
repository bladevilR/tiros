package org.jeecg.modules.dispatch.planform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.dispatch.planform.bean.BuPlanFormCheckRecordJudge;
import org.jeecg.modules.dispatch.planform.bean.vo.CheckRecordJudgeQueryVO;
import org.jeecg.modules.dispatch.planform.mapper.BuPlanFormCheckRecordJudgeMapper;
import org.jeecg.modules.dispatch.planform.service.BuPlanFormCheckRecordJudgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 质量评定 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Service
public class BuPlanFormCheckRecordJudgeServiceImpl extends ServiceImpl<BuPlanFormCheckRecordJudgeMapper, BuPlanFormCheckRecordJudge> implements BuPlanFormCheckRecordJudgeService {

    @Resource
    private BuPlanFormCheckRecordJudgeMapper buPlanFormCheckRecordJudgeMapper;


    /**
     * @see BuPlanFormCheckRecordJudgeService#listJudge(CheckRecordJudgeQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuPlanFormCheckRecordJudge> listJudge(CheckRecordJudgeQueryVO queryVO) throws Exception {
        return buPlanFormCheckRecordJudgeMapper.selectListByQueryVO(queryVO);
    }

    /**
     * @see BuPlanFormCheckRecordJudgeService#getJudgeById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuPlanFormCheckRecordJudge getJudgeById(String id) throws Exception {
        return buPlanFormCheckRecordJudgeMapper.selectJudgeById(id);
    }

    /**
     * @see BuPlanFormCheckRecordJudgeService#saveOrUpdateJudge(BuPlanFormCheckRecordJudge)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateJudge(BuPlanFormCheckRecordJudge checkRecordJudge) throws Exception {
        checkRecordJudge.setJdTime(new Date());
        if (StringUtils.isBlank(checkRecordJudge.getId())) {
            buPlanFormCheckRecordJudgeMapper.insert(checkRecordJudge);
        } else {
            buPlanFormCheckRecordJudgeMapper.updateById(checkRecordJudge);
        }

        return true;
    }

    /**
     * @see BuPlanFormCheckRecordJudgeService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        if (StringUtils.isBlank(ids)) {
            return true;
        }

        List<String> idList = Arrays.asList(ids.split(","));
        buPlanFormCheckRecordJudgeMapper.deleteBatchIds(idList);

        return true;
    }

}
