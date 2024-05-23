package org.jeecg.modules.group.information.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.group.information.mapper.BuMaterialToolsGroupMapper;
import org.jeecg.modules.group.information.service.BuMaterialToolsGroupService;
import org.jeecg.modules.group.tool.bean.BuMaterialTools;
import org.jeecg.modules.group.tool.mapper.BuMaterialToolMapper;
import org.jeecg.modules.group.tool.service.BuMaterialToolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 车间班组信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
@Slf4j
@Service
public class BuMaterialToolsGroupServiceImpl extends ServiceImpl<BuMaterialToolsGroupMapper, BuMaterialTools> implements BuMaterialToolsGroupService {

    @Resource
    private BuMaterialToolService buMaterialToolService;
    @Resource
    private BuMaterialToolMapper  buMaterialToolMapper;

    /**
     * @see BuMaterialToolsGroupService
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addToolsToGroup(List<String> toolsId, String groupId) {
        List<BuMaterialTools> toolsList=new ArrayList<>();
        toolsId.forEach(id->{
            BuMaterialTools tools = buMaterialToolMapper.selectById(id);
            if (null == tools) {
                throw new JeecgBootException("工装/工具不存在");
            }
            if (StringUtils.isNotBlank(tools.getGroupId())) {
                throw new JeecgBootException("工装/工具已关联工班");
            }
            tools.setGroupId(groupId);
            toolsList.add(tools);
        });
          return buMaterialToolService.updateBatchById(toolsList);
    }

    /**
     * @see BuMaterialToolsGroupService#deleteToolsFromGroup(String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteToolsFromGroup(String toolsId, String groupId) {
        BuMaterialTools tools = buMaterialToolMapper.selectById(toolsId);
        if (null == tools) {
            throw new JeecgBootException("工装/工具不存在");
        }
        if (StringUtils.isNotBlank(tools.getGroupId()) && !groupId.equals(tools.getGroupId())) {
            throw new JeecgBootException("工装/工具未关联到此工班");
        }

        tools.setGroupId(null);
        buMaterialToolMapper.updateById(tools);

        return true;
    }

}
