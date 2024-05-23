package org.jeecg.modules.basemanage.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.org.bean.BuTraiiningAttend;
import org.jeecg.modules.basemanage.org.bean.BuTraining;
import org.jeecg.modules.basemanage.org.bean.vo.BuTrainingQueryVO;
import org.jeecg.modules.basemanage.org.mapper.BuTraiiningAttendMapper;
import org.jeecg.modules.basemanage.org.mapper.BuTrainingMapper;
import org.jeecg.modules.basemanage.org.service.BuTrainingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 培训记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
@Service
public class BuTrainingServiceImpl extends ServiceImpl<BuTrainingMapper, BuTraining> implements BuTrainingService {

    @Resource
    private BuTrainingMapper buTrainingMapper;
    @Resource
    private BuTraiiningAttendMapper buTraiiningAttendMapper;


    /**
     * @see BuTrainingService#pageTraining(BuTrainingQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTraining> pageTraining(BuTrainingQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buTrainingMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuTrainingService#saveTraining(BuTraining)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public boolean saveTraining(BuTraining training) throws Exception {
        String trainingId = training.getId();
        if (StringUtils.isBlank(trainingId)) {
            buTrainingMapper.insert(training);
        } else {
            buTrainingMapper.updateById(training);
        }

        return true;
    }

    /**
     * @see BuTrainingService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        for (String id : idList) {
            LambdaQueryWrapper<BuTraiiningAttend> wrapper = new LambdaQueryWrapper<BuTraiiningAttend>()
                    .eq(BuTraiiningAttend::getTrainingId, id);
            Integer count = buTraiiningAttendMapper.selectCount(wrapper);
            if (count > 0) {
                BuTraining training = buTrainingMapper.selectById(id);
                throw new JeecgBootException("培训记录[" + training.getTitle() + "]已关联用户，删除失败");
            }
        }

        buTrainingMapper.deleteBatchIds(idList);

        return true;
    }

}
