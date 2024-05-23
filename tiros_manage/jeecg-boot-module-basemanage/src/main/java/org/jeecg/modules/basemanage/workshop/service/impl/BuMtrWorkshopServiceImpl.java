package org.jeecg.modules.basemanage.workshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrWorkshop;
import org.jeecg.modules.basemanage.workshop.mapper.BuMtrWorkshopMapper;
import org.jeecg.modules.basemanage.workshop.service.BuMtrWorkshopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 架修车间 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-13
 */
@Slf4j
@Service
public class BuMtrWorkshopServiceImpl extends ServiceImpl<BuMtrWorkshopMapper, BuMtrWorkshop> implements BuMtrWorkshopService {

    @Resource
    private BuMtrWorkshopMapper buMtrWorkshopMapper;


    /**
     * @see BuMtrWorkshopService#listAll()
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMtrWorkshop> listAll() throws Exception {
        return buMtrWorkshopMapper.selectAllList();
    }

    /**
     * @see BuMtrWorkshopService#getWorkshopById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMtrWorkshop getWorkshopById(String id) {
        return buMtrWorkshopMapper.selectWorkshopById(id);
    }

    /**
     * @see BuMtrWorkshopService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buMtrWorkshopMapper.deleteBatchIds(idList);

        return true;
    }

}
