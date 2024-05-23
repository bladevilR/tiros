package org.jeecg.modules.basemanage.workrecord.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.workrecord.bean.BuWorkRecordCategory;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordCategoryMapper;
import org.jeecg.modules.basemanage.workrecord.mapper.BuWorkRecordDetailMapper;
import org.jeecg.modules.basemanage.workrecord.service.BuWorkRecordCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 作业记录明细分项 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-08
 */
@Slf4j
@Service
public class BuWorkRecordCategoryServiceImpl extends ServiceImpl<BuWorkRecordCategoryMapper, BuWorkRecordCategory> implements BuWorkRecordCategoryService {

    @Resource
    private BuWorkRecordDetailMapper buWorkRecordDetailMapper;
    @Resource
    private BuWorkRecordCategoryMapper buWorkRecordCategoryMapper;


    /**
     * @see BuWorkRecordCategoryService#listByWorkRecordId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuWorkRecordCategory> listByWorkRecordId(String workRecordId) {
        return buWorkRecordCategoryMapper.selectListByWorkRecordId(workRecordId);
    }

    /**
     * @see BuWorkRecordCategoryService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        if (StringUtils.isBlank(ids)) {
            throw new JeecgBootException("请选择要删除的作业记录明细分项");
        }
        List<String> categoryIdList = Arrays.asList(ids.split(","));

        // 删除作业记录表明细
        List<String> detailIdList = buWorkRecordDetailMapper.selectIdListByCategoryIdList(categoryIdList);
        if (CollectionUtils.isNotEmpty(detailIdList)) {
            buWorkRecordDetailMapper.deleteBatchIds(detailIdList);
        }
        // 删除作业记录表明细分项
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            buWorkRecordCategoryMapper.deleteBatchIds(categoryIdList);
        }

        return true;
    }
}
