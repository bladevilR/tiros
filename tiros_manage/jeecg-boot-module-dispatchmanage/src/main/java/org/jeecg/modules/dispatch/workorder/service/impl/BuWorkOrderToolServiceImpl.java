package org.jeecg.modules.dispatch.workorder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderTool;
import org.jeecg.modules.dispatch.workorder.mapper.BuWorkOrderToolMapper;
import org.jeecg.modules.dispatch.workorder.service.BuWorkOrderToolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-09-29
 */
@Service
public class BuWorkOrderToolServiceImpl extends ServiceImpl<BuWorkOrderToolMapper, BuWorkOrderTool> implements BuWorkOrderToolService {

    @Resource
    private BuWorkOrderToolMapper buWorkOrderToolMapper;


    /**
     * @see BuWorkOrderToolService#saveOrderTool(BuWorkOrderTool)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrderTool(BuWorkOrderTool tool) throws Exception {
        if (StringUtils.isBlank(tool.getToolTypeId())) {
            throw new JeecgBootException("请选择工器具类型");
        }
        if (StringUtils.isBlank(tool.getToolId())) {
            throw new JeecgBootException("请选择具体工器具");
        }

        if (StringUtils.isBlank(tool.getId())) {
            tool.setId(UUIDGenerator.generate());
        }
        buWorkOrderToolMapper.deleteById(tool.getId());
        buWorkOrderToolMapper.insert(tool);

        return true;
    }

    /**
     * @see BuWorkOrderToolService#deleteOrderTool(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOrderTool(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buWorkOrderToolMapper.deleteBatchIds(idList);

        return true;
    }

}
