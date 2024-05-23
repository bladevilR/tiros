package org.jeecg.modules.group.sparepart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.group.sparepart.bean.vo.BuMaterialSparePartQueryVO;

/**
 * <p>
 * 列管备件 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialSparePartGroupService extends IService<BuMaterialSparePart> {

    /**
     * 分页查询列管备件信息
     *
     * @param queryVO  查询列管备件条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialSparePart> page(BuMaterialSparePartQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询列管备件信息
     *
     * @param id 列管备件id
     * @return 分页结果
     * @throws Exception 异常信息
     */
    BuMaterialSparePart getById(String id) throws Exception;

}
