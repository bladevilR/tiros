package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.BuBaseSupplier;
import org.jeecg.modules.outsource.bean.vo.BuBaseSupplierQueryVO;
import org.jeecg.modules.outsource.bean.vo.SystemContractVO;

import java.util.List;

/**
 * <p>
 * 厂商信息表 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuBaseSupplierService extends IService<BuBaseSupplier> {

    /**
     * 查询厂商树，系统-合同的树结构数据，第一层=系统，第二层=合同
     *
     * @param trainTypeId 车型id
     * @return 系统-合同的树结构数据
     * @throws Exception 异常
     */
    List<SystemContractVO> vendorTree(String trainTypeId) throws Exception;

    /**
     * 新增
     *
     * @param supplier
     * @return
     */
    boolean saveSupplier(BuBaseSupplier supplier);

    String selectAssetTypeName(String objTypeId);

    IPage<BuBaseSupplier> pageList(Page<BuBaseSupplier> page, BuBaseSupplierQueryVO queryVO);

}
