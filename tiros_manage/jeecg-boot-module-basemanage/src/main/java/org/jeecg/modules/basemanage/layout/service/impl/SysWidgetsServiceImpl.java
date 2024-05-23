package org.jeecg.modules.basemanage.layout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.layout.bean.SysLayoutWidgets;
import org.jeecg.modules.basemanage.layout.bean.SysWidgets;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysWidgetsQueryVO;
import org.jeecg.modules.basemanage.layout.mapper.SysLayoutWidgetsMapper;
import org.jeecg.modules.basemanage.layout.mapper.SysWidgetsMapper;
import org.jeecg.modules.basemanage.layout.service.SysWidgetsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 桌面部件 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Service
public class SysWidgetsServiceImpl extends ServiceImpl<SysWidgetsMapper, SysWidgets> implements SysWidgetsService {

    @Resource
    private SysWidgetsMapper sysWidgetsMapper;
    @Resource
    private SysLayoutWidgetsMapper sysLayoutWidgetsMapper;


    /**
     * @see SysWidgetsService#pageWidgets(BuSysWidgetsQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<SysWidgets> pageWidgets(BuSysWidgetsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return sysWidgetsMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see SysWidgetsService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(idList)) {
            LambdaQueryWrapper<SysLayoutWidgets> layoutWidgetsWrapper = new LambdaQueryWrapper<SysLayoutWidgets>()
                    .in(SysLayoutWidgets::getWidgetId, idList);
            Integer count = sysLayoutWidgetsMapper.selectCount(layoutWidgetsWrapper);
            if (null != count && count > 0) {
                throw new JeecgBootException("桌面部件已关联布局，不能删除");
            }

            sysWidgetsMapper.deleteBatchIds(idList);
        }

        return true;
    }

}
