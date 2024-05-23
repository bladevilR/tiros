package org.jeecg.modules.group.partchange.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.group.partchange.bean.BuWorkPartChange;
import org.jeecg.modules.group.partchange.bean.vo.BuWorkPartChangeQueryVO;
import org.jeecg.modules.group.partchange.mapper.BuWorkPartChangeMapper;
import org.jeecg.modules.group.partchange.service.BuWorkPartChangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 部件更换 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Slf4j
@Service
public class BuWorkPartChangeServiceImpl extends ServiceImpl<BuWorkPartChangeMapper, BuWorkPartChange> implements BuWorkPartChangeService {

    @Resource
    private BuWorkPartChangeMapper buWorkPartChangeMapper;

    /**
     * @see BuWorkPartChangeService#page(BuWorkPartChangeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkPartChange> page(BuWorkPartChangeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buWorkPartChangeMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuWorkPartChangeService#selectById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkPartChange selectById(String id) throws Exception {
        return buWorkPartChangeMapper.selectById(id);
    }

    /**
     * @see BuWorkPartChangeService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        buWorkPartChangeMapper.deleteBatchIds(idList);

        return true;
    }

}
