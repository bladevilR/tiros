package org.jeecg.modules.basemanage.layout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.basemanage.layout.bean.SysLayoutWidgets;
import org.jeecg.modules.basemanage.layout.bean.SysLayouts;
import org.jeecg.modules.basemanage.layout.bean.SysWidgets;
import org.jeecg.modules.basemanage.layout.bean.vo.BuSysLayoutsQueryVO;
import org.jeecg.modules.basemanage.layout.mapper.SysLayoutWidgetsMapper;
import org.jeecg.modules.basemanage.layout.mapper.SysLayoutsMapper;
import org.jeecg.modules.basemanage.layout.mapper.SysWidgetsMapper;
import org.jeecg.modules.basemanage.layout.service.SysLayoutsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 界面布局 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-27
 */
@Service
public class SysLayoutsServiceImpl extends ServiceImpl<SysLayoutsMapper, SysLayouts> implements SysLayoutsService {

    @Resource
    private SysLayoutsMapper sysLayoutsMapper;
    @Resource
    private SysLayoutWidgetsMapper sysLayoutWidgetsMapper;
    @Resource
    private SysWidgetsMapper sysWidgetsMapper;


    /**
     * @see SysLayoutsService#pageLayouts(BuSysLayoutsQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<SysLayouts> pageLayouts(BuSysLayoutsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return sysLayoutsMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see SysLayoutsService#getLayouts(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public SysLayouts getLayouts(String id) throws Exception {
        return sysLayoutsMapper.selectLayoutsById(id);
    }

    /**
     * @see SysLayoutsService#getUserDefaultLayouts()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public SysLayouts getUserDefaultLayouts() throws Exception {
        // 获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser == null ? "-1" : sysUser.getId();

        String layoutsId = sysLayoutsMapper.selectDefaultLayoutsIdByUserId(userId);
        return sysLayoutsMapper.selectLayoutsById(layoutsId);
    }

    /**
     * @see SysLayoutsService#saveLayouts(SysLayouts)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveLayouts(SysLayouts layouts) throws Exception {
        setLayoutsByUserId(layouts);

        String layoutsId = layouts.getId();
        if (StringUtils.isBlank(layoutsId)) {
            layouts.setId(UUIDGenerator.generate());
            sysLayoutsMapper.insert(layouts);
        } else {
            // 删除布局组件
            LambdaQueryWrapper<SysLayoutWidgets> layoutWidgetsWrapper = new LambdaQueryWrapper<SysLayoutWidgets>()
                    .eq(SysLayoutWidgets::getLayoutId, layoutsId);
            sysLayoutWidgetsMapper.delete(layoutWidgetsWrapper);

            sysLayoutsMapper.updateById(layouts);
        }
        saveLayoutWidgets(layouts);

        return true;
    }

    /**
     * @see SysLayoutsService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(idList)) {
            // 删除布局组件
            LambdaQueryWrapper<SysLayoutWidgets> layoutWidgetsWrapper = new LambdaQueryWrapper<SysLayoutWidgets>()
                    .in(SysLayoutWidgets::getLayoutId, idList);
            sysLayoutWidgetsMapper.delete(layoutWidgetsWrapper);

            sysLayoutsMapper.deleteBatchIds(idList);
        }

        return true;
    }


    private void setLayoutsByUserId(SysLayouts layouts) {
        if (null == layouts.getLayoutScope()) {
            layouts.setLayoutScope(0);
        }
        if (StringUtils.isBlank(layouts.getByUserId()) && layouts.getLayoutScope() == 1) {
            // 获取登录用户信息
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String userId = sysUser == null ? "-1" : sysUser.getId();
            layouts.setByUserId(userId);
        }
    }

    private void saveLayoutWidgets(SysLayouts layouts) {
        List<SysLayoutWidgets> layoutWidgetsList = layouts.getLayoutWidgetsList();
        if (CollectionUtils.isNotEmpty(layoutWidgetsList)) {
            List<String> widgetIdList = layoutWidgetsList.stream()
                    .map(SysLayoutWidgets::getWidgetId)
                    .collect(Collectors.toList());
            List<SysWidgets> widgetsList = sysWidgetsMapper.selectBatchIds(widgetIdList);
            Map<String, SysWidgets> idWidgetsMap = new HashMap<>();
            widgetsList.forEach(widgets -> idWidgetsMap.put(widgets.getId(), widgets));

            for (SysLayoutWidgets layoutWidgets : layoutWidgetsList) {
                SysWidgets widgets = idWidgetsMap.get(layoutWidgets.getWidgetId());
                if (null == layoutWidgets.getDefWidth()) {
                    layoutWidgets.setDefWidth(widgets.getDefWidth());
                }
                if (null == layoutWidgets.getDefHeight()) {
                    layoutWidgets.setDefHeight(widgets.getDefHeight());
                }

                layoutWidgets.setLayoutId(layouts.getId());
                sysLayoutWidgetsMapper.insert(layoutWidgets);
            }
        }
    }

}
