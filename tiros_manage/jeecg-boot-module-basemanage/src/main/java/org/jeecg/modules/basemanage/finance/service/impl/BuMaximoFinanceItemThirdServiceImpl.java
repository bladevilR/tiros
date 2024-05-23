package org.jeecg.modules.basemanage.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.finance.bean.BuMaximoFinanceItem;
import org.jeecg.modules.basemanage.finance.mapper.BuMaximoFinanceItemMapper;
import org.jeecg.modules.basemanage.finance.service.BuMaximoFinanceItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 财务项目 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
@Slf4j
@Service
public class BuMaximoFinanceItemThirdServiceImpl extends ServiceImpl<BuMaximoFinanceItemMapper, BuMaximoFinanceItem> implements BuMaximoFinanceItemService {

    @Resource
    private BuMaximoFinanceItemMapper buMaximoFinanceItemMapper;


    /**
     * @see BuMaximoFinanceItemService#listProjectItem()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaximoFinanceItem> listProjectItem() throws Exception {
        LambdaQueryWrapper<BuMaximoFinanceItem> wrapper = new LambdaQueryWrapper<BuMaximoFinanceItem>()
                .eq(BuMaximoFinanceItem::getType, "PROJECT")
                .orderByAsc(BuMaximoFinanceItem::getCode)
                .orderByAsc(BuMaximoFinanceItem::getName);
        return buMaximoFinanceItemMapper.selectList(wrapper);
    }

    /**
     * @see BuMaximoFinanceItemService#listTaskItemByProjectId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaximoFinanceItem> listTaskItemByProjectId(String projectId) throws Exception {
        BuMaximoFinanceItem project = buMaximoFinanceItemMapper.selectById(projectId);
        if (null == project) {
            throw new JeecgBootException("财务项目不存在");
        }

        LambdaQueryWrapper<BuMaximoFinanceItem> wrapper = new LambdaQueryWrapper<BuMaximoFinanceItem>()
                .eq(BuMaximoFinanceItem::getType, "TASK")
                .eq(BuMaximoFinanceItem::getProjectCode, project.getCode())
                .orderByAsc(BuMaximoFinanceItem::getCode)
                .orderByAsc(BuMaximoFinanceItem::getName);
        return buMaximoFinanceItemMapper.selectList(wrapper);
    }

}
