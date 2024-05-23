package org.jeecg.modules.basemanage.workrecord.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecord;
import org.jeecg.modules.basemanage.workrecord.bean.vo.BuWorkRecordQueryVO;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordCategoryMapper;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordDetailMapper;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordMapper;
import org.jeecg.modules.basemanage.workrecord.service.BuWorkRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 作业记录表 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-08-10
 */
@Slf4j
@Service
public class BuWorkRecordServiceImpl extends ServiceImpl<BuWorkRecordMapper, BuWorkRecord> implements BuWorkRecordService {

    @Resource
    private BuWorkRecordMapper buWorkRecordMapper;
    @Resource
    private BuWorkRecordCategoryMapper buWorkRecordCategoryMapper;
    @Resource
    private BuWorkRecordDetailMapper buWorkRecordDetailMapper;

    /**
     * @see BuWorkRecordService#pageWorkRecord(Page, BuWorkRecordQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Page<BuWorkRecord> pageWorkRecord(Page<BuWorkRecord> page, BuWorkRecordQueryVO queryVO) {
        return buWorkRecordMapper.selectWorkRecordPage(page, queryVO);
    }

    /**
     * @see BuWorkRecordService#getWorkRecordById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkRecord getWorkRecordById(String id) throws Exception {
        return buWorkRecordMapper.selectWorkRecordById(id);
    }

    /**
     * @see BuWorkRecordService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        if (StringUtils.isBlank(ids)) {
            throw new JeecgBootException("请选择要删除的作业记录表");
        }
        List<String> workRecordIdList = Arrays.asList(ids.split(","));

        // 删除作业记录表明细
        List<String> detailIdList = buWorkRecordDetailMapper.selectIdListByWorkRecordIdList(workRecordIdList);
        if (CollectionUtils.isNotEmpty(detailIdList)) {
            buWorkRecordDetailMapper.deleteBatchIds(detailIdList);
        }
        // 删除作业记录表明细分项
        List<String> categoryIdList = buWorkRecordCategoryMapper.selectIdListByWorkRecordIdList(workRecordIdList);
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            buWorkRecordCategoryMapper.deleteBatchIds(categoryIdList);
        }
        // 删除作业记录表
        if (CollectionUtils.isNotEmpty(workRecordIdList)) {
            buWorkRecordMapper.deleteBatchIds(workRecordIdList);
        }

        return true;
    }

}
