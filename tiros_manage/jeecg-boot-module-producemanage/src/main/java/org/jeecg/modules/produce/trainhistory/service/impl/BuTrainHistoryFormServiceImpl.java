package org.jeecg.modules.produce.trainhistory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainHistoryFormRecordVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuTrainHistoryFormQueryVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainHistoryFormMapper;
import org.jeecg.modules.produce.trainhistory.service.BuTrainHistoryFormService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 车辆履历-表单记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-04
 */
@Service
public class BuTrainHistoryFormServiceImpl implements BuTrainHistoryFormService {

    @Resource
    private BuTrainHistoryFormMapper buTrainHistoryFormMapper;


    /**
     * @see BuTrainHistoryFormService#pageFormRecordVO(BuTrainHistoryFormQueryVO, Integer, Integer)
     */
    @Override
    public IPage<BuTrainHistoryFormRecordVO> pageFormRecordVO(BuTrainHistoryFormQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuTrainHistoryFormRecordVO> page = buTrainHistoryFormMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<BuTrainHistoryFormRecordVO> formRecordVOList = page.getRecords();
            for (BuTrainHistoryFormRecordVO formRecordVO : formRecordVOList) {
                formRecordVO.setFormRecordTypeName(transToTypeName(formRecordVO.getFormRecordType()));
            }
        }

        return page;
    }

    private String transToTypeName(Integer formRecordType) {
        // 1作业记录表2数据记录表3检查记录表4工单附件

        if (null == formRecordType) {
            return "未知";
        }
        if (1 == formRecordType) {
            return "作业记录表";
        }
        if (2 == formRecordType) {
            return "数据记录表";
        }
        if (3 == formRecordType) {
            return "检查记录表";
        }
        if (4 == formRecordType) {
            return "工单附件";
        }

        return "未知";
    }

}
