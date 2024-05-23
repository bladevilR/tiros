package org.jeecg.modules.group.sparepart.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.group.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.group.sparepart.bean.vo.BuMaterialSparePartQueryVO;
import org.jeecg.modules.group.sparepart.mapper.BuMaterialSparePartGroupMapper;
import org.jeecg.modules.group.sparepart.service.BuMaterialSparePartGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 列管备件 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Slf4j
@Service
public class BuMaterialSparePartGroupServiceImpl extends ServiceImpl<BuMaterialSparePartGroupMapper, BuMaterialSparePart> implements BuMaterialSparePartGroupService {

    @Resource
    private BuMaterialSparePartGroupMapper buMaterialSparePartGroupMapper;


    /**
     * @see BuMaterialSparePartGroupService#page(BuMaterialSparePartQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public IPage<BuMaterialSparePart> page(BuMaterialSparePartQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {

        return buMaterialSparePartGroupMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialSparePartGroupService#getById(String)
     */
    @Transactional(readOnly = true)
    @Override
    public BuMaterialSparePart getById(String id) throws Exception {
        return buMaterialSparePartGroupMapper.getById(id);
    }

}
