package org.jeecg.modules.basemanage.traininfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainTypeQueryVO;
import org.jeecg.modules.basemanage.traininfo.mapper.BuTrainTypeMapper;
import org.jeecg.modules.basemanage.traininfo.service.IBuTrainTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 车型信息 如：A型车，B型车 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-14
 */
@Service
public class BuTrainTypeServiceImpl extends ServiceImpl<BuTrainTypeMapper, BuTrainType> implements IBuTrainTypeService {

    @Resource
    private BuTrainTypeMapper trainTypeMapper;


    @Override
    public Page<BuTrainType> selectTrainTypePage(Page<BuTrainType> page, BuTrainTypeQueryVO trainType) {
        return page.setRecords(trainTypeMapper.selectTrainTypePage(page, trainType));
    }

}
