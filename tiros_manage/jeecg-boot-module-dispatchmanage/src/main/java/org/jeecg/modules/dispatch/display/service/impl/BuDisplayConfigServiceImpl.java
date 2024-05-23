package org.jeecg.modules.dispatch.display.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.dispatch.display.bean.BuDisplayConfig;
import org.jeecg.modules.dispatch.display.bean.BuDisplayResource;
import org.jeecg.modules.dispatch.display.mapper.BuDisplayConfigMapper;
import org.jeecg.modules.dispatch.display.mapper.BuDisplayResourceMapper;
import org.jeecg.modules.dispatch.display.service.BuDisplayConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 播放配置 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-10
 */
@Slf4j
@Service
public class BuDisplayConfigServiceImpl extends ServiceImpl<BuDisplayConfigMapper, BuDisplayConfig> implements BuDisplayConfigService {

    @Resource
    private BuDisplayConfigMapper buDisplayConfigMapper;
    @Resource
    private BuDisplayResourceMapper buDisplayResourceMapper;


    /**
     * @see BuDisplayConfigService#setResourceToScreen(String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setResourceToScreen(String screenId, String resourceId) throws Exception {
        BuDisplayResource displayResource = buDisplayResourceMapper.selectById(resourceId);
        if (null == displayResource) {
            throw new JeecgBootException("看板资源不存在");
        }

        BuDisplayConfig displayConfig = new BuDisplayConfig()
                .setScreenId(screenId)
                .setResourceType(1)
                .setResourceId(resourceId)
                .setTitle(displayResource.getTitle())
                .setAddress(displayResource.getUrl());
        buDisplayConfigMapper.insert(displayConfig);

        return true;
    }

    /**
     * @see BuDisplayConfigService#setCustomToScreen(String, String, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setCustomToScreen(String filePath, String title, String screenId) throws Exception {
        BuDisplayConfig displayConfig = new BuDisplayConfig()
                .setScreenId(screenId)
                .setResourceType(4)
                .setTitle(title)
                .setAddress(filePath);
        buDisplayConfigMapper.insert(displayConfig);

        return false;
    }

    /**
     * @see BuDisplayConfigService#listPlayContent(String, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuDisplayConfig> listPlayContent(String screenId, Integer resourceType) throws Exception {
        return buDisplayConfigMapper.selectListByScreenIdAndResourceType(screenId, resourceType);
    }

    /**
     * @see BuDisplayConfigService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        if (CollectionUtils.isNotEmpty(idList)) {
            buDisplayConfigMapper.deleteBatchIds(idList);
        }

        return true;
    }

}
