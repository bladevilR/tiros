package org.jeecg.modules.dispatch.display.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.dispatch.display.bean.BuDisplayScreen;
import org.jeecg.modules.dispatch.display.mapper.BuDisplayScreenMapper;
import org.jeecg.modules.dispatch.display.service.BuDisplayScreenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 大屏信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
@Slf4j
@Service
public class BuDisplayScreenServiceImpl extends ServiceImpl<BuDisplayScreenMapper, BuDisplayScreen> implements BuDisplayScreenService {

    @Resource
    private BuDisplayScreenMapper buDisplayScreenMapper;


    /**
     * @see BuDisplayScreenService#listByTypeAndGroupId(Integer, String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuDisplayScreen> listByTypeAndGroupId(Integer screenType, String groupId) throws Exception {
        return buDisplayScreenMapper.selectListByTypeAndGroupId(screenType,groupId);
    }

}
