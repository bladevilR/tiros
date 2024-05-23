package org.jeecg.modules.basemanage.workrecord.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordDetail;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordDetailQueryVO;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordDetailMapper;
import org.jeecg.modules.basemanage.workrecord.service.BuWorkRecordDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 作业记录表明细, 一条规程项关联一条或多条作业记录明细，如果是多条表示对这条规程项的拆分 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
@Slf4j
@Service
public class BuWorkRecordDetailServiceImpl extends ServiceImpl<BuWorkRecordDetailMapper, BuWorkRecordDetail> implements BuWorkRecordDetailService {

    @Resource
    private BuWorkRecordDetailMapper workRecordDetailMapper;


    /**
     * @see BuWorkRecordDetailService#listWorkRecordDetail(BuWorkRecordDetailQueryVO)
     */
    @Override
    public List<BuWorkRecordDetail> listWorkRecordDetail(BuWorkRecordDetailQueryVO queryVO) throws Exception {
        return workRecordDetailMapper.listByCondition(queryVO);
    }

}
