package org.jeecg.modules.material.sparepart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.material.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.material.sparepart.bean.vo.BuMaterialSparePartQueryVO;
import org.jeecg.modules.material.sparepart.mapper.BuMaterialSparePartMapper;
import org.jeecg.modules.material.sparepart.service.BuMaterialSparePartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

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
public class BuMaterialSparePartServiceImpl extends ServiceImpl<BuMaterialSparePartMapper, BuMaterialSparePart> implements BuMaterialSparePartService {

    @Resource
    private BuMaterialSparePartMapper buMaterialSparePartMapper;

    /**
     * @see BuMaterialSparePartService#page(BuMaterialSparePartQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public IPage<BuMaterialSparePart> page(BuMaterialSparePartQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {

        return buMaterialSparePartMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialSparePartService#getById(String)
     */
    @Transactional(readOnly = true)
    @Override
    public BuMaterialSparePart getById(String id) throws Exception {
        return buMaterialSparePartMapper.getById(id);
    }

    /**
     * @see BuMaterialSparePartService#deleteBatch(String)
     */
    @Transactional
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buMaterialSparePartMapper.deleteBatchIds(idList);

        return true;
    }

    /**
     * @see BuMaterialSparePartService#save(Object)
     */
    @Transactional
    @Override
    public boolean save(BuMaterialSparePart buMaterialSparePart) {
        if (isAssetCodeRepeated(buMaterialSparePart)) {
            throw new JeecgBootException("列管备件资产编码重复");
        }
        if (isManufNoRepeated(buMaterialSparePart)) {
            throw new JeecgBootException("列管备件序列号重复");
        }

        return super.save(buMaterialSparePart);
    }

    /**
     * @see BuMaterialSparePartService#updateById(Object)
     */
    @Transactional
    @Override
    public boolean updateById(BuMaterialSparePart buMaterialSparePart) {
        if (isAssetCodeRepeated(buMaterialSparePart)) {
            throw new JeecgBootException("列管备件资产编码重复");
        }
        if (isManufNoRepeated(buMaterialSparePart)) {
            throw new JeecgBootException("列管备件序列号重复");
        }

        return super.updateById(buMaterialSparePart);
    }

    private boolean isAssetCodeRepeated(BuMaterialSparePart buMaterialSparePart) {
        LambdaQueryWrapper<BuMaterialSparePart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuMaterialSparePart::getAssetCode, buMaterialSparePart.getAssetCode());
        List<BuMaterialSparePart> buMaterialSparePartList = buMaterialSparePartMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(buMaterialSparePartList)) {
            return false;
        }
        if (StringUtils.isBlank(buMaterialSparePart.getId())) {
            return true;
        }
        return !buMaterialSparePart.getId().equals(buMaterialSparePartList.get(0).getId());
    }

    private boolean isManufNoRepeated(BuMaterialSparePart buMaterialSparePart) {
        LambdaQueryWrapper<BuMaterialSparePart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuMaterialSparePart::getManufNo, buMaterialSparePart.getManufNo());
        List<BuMaterialSparePart> buMaterialSparePartList = buMaterialSparePartMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(buMaterialSparePartList)) {
            return false;
        }
        if (StringUtils.isBlank(buMaterialSparePart.getId())) {
            return true;
        }
        return !buMaterialSparePart.getId().equals(buMaterialSparePartList.get(0).getId());
    }

}
