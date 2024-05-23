package org.jeecg.modules.quality.recordconfirm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormCheckRecord;
import org.jeecg.modules.quality.recordconfirm.bean.BuPlanFormCheckRecordJudge;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormCheckRecordVO;
import org.jeecg.modules.quality.recordconfirm.bean.vo.BuFormRecordQueryVO;
import org.jeecg.modules.quality.recordconfirm.mapper.BuPlanFormCheckRecordQualityMapper;
import org.jeecg.modules.quality.recordconfirm.service.BuPlanFormCheckRecordQualityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业检查记录表（实例） 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
@Service
public class BuPlanFormCheckRecordQualityServiceImpl extends ServiceImpl<BuPlanFormCheckRecordQualityMapper, BuPlanFormCheckRecord> implements BuPlanFormCheckRecordQualityService {

    @Resource
    private BuPlanFormCheckRecordQualityMapper buPlanFormCheckRecordQualityMapper;


    /**
     * @see BuPlanFormCheckRecordQualityService#pageFormCheckRecord(BuFormRecordQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuFormCheckRecordVO> pageFormCheckRecord(BuFormRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuFormCheckRecordVO> page = buPlanFormCheckRecordQualityMapper.selectFormCheckRecordVOPageByFormRecordQueryVO(new Page<>(pageNo, pageSize), queryVO);

        setCheckRecordJudgeList(page);
        setFormInstType(page);

        return page;
    }


    private void setCheckRecordJudgeList(IPage<BuFormCheckRecordVO> page) {
        List<BuFormCheckRecordVO> checkRecordVOList = page.getRecords();
        if (CollectionUtils.isEmpty(checkRecordVOList)) {
            return;
        }

        Set<String> checkRecordIdSet = checkRecordVOList.stream()
                .map(BuFormCheckRecordVO::getId)
                .collect(Collectors.toSet());
        List<BuPlanFormCheckRecordJudge> allJudgeList = buPlanFormCheckRecordQualityMapper.selectJudgeListByCheckRecordIdList(new ArrayList<>(checkRecordIdSet));

        for (BuFormCheckRecordVO checkRecordVO : checkRecordVOList) {
            String checkRecordVOId = checkRecordVO.getId();
            List<BuPlanFormCheckRecordJudge> judgeList = allJudgeList.stream()
                    .filter(judge -> checkRecordVOId.equals(judge.getCheckInstId()))
                    .collect(Collectors.toList());
            checkRecordVO.setJudgeList(judgeList);
        }
    }

    private void setFormInstType(IPage<BuFormCheckRecordVO> page) {
        List<BuFormCheckRecordVO> checkRecordVOList = page.getRecords();
        if (CollectionUtils.isEmpty(checkRecordVOList)) {
            return;
        }

        for (BuFormCheckRecordVO checkRecordVO : checkRecordVOList) {
            if (null == checkRecordVO.getFormType()) {
                checkRecordVO.setFormType(checkRecordVO.getInstType());
            }
        }
    }

}
