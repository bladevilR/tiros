package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.outsource.bean.BuBaseSupplier;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.outsource.bean.VendorTree;
import org.jeecg.modules.outsource.bean.vo.BuBaseSupplierQueryVO;

import java.util.List;

/**
 * <p>
 * 厂商信息表 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuBaseSupplierMapper extends BaseMapper<BuBaseSupplier> {

    String selectAssetTypeName(String objTypeId);

    /**
     * 厂商树
     * @param trainTypeId
     * @return
     */
    List<VendorTree> vendorTree(String trainTypeId);
    List<VendorTree> selectVendorTreeChild(String id);

    IPage<BuBaseSupplier> selectPageByCondition(Page<BuBaseSupplier> page, BuBaseSupplierQueryVO queryVO);
}
