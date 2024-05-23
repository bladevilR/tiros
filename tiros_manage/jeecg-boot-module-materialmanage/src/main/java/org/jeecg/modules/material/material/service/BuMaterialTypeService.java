package org.jeecg.modules.material.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeTool;
import org.jeecg.modules.material.material.bean.vo.BuMaterialTypeQueryVO;

/**
 * <p>
 * 物资类型 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
public interface BuMaterialTypeService extends IService<BuMaterialType> {

    /**
     * 根据条件分页查询物资类型
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 物质类型列表
     * @throws Exception 异常
     */
    Page<BuMaterialType> pageMaterialType(BuMaterialTypeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件分页查询工器具类型
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 工器具类型列表
     * @throws Exception 异常
     */
    Page<BuMaterialTypeTool> pageMaterialTypeTool(BuMaterialTypeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据条件分页查询必换件
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 必换件物质类型列表
     * @throws Exception 异常
     */
    Page<BuMaterialType> selectMustMaterialTypePage(BuMaterialTypeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询物质类型
     *
     * @param id 物质类型id
     * @return 物质类型信息
     * @throws Exception 异常
     */
    BuMaterialType getMaterialTypeById(String id) throws Exception;

    /**
     * 根据id查询工器具类型
     *
     * @param id 物质类型id
     * @return 工器具类型信息
     * @throws Exception 异常
     */
    BuMaterialTypeTool getMaterialTypeToolById(String id) throws Exception;

    /**
     * 新增物资类型
     *
     * @param materialType 物资类型
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean addMaterialType(BuMaterialType materialType) throws Exception;

    /**
     * 修改物资类型
     *
     * @param materialType 物资类型
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateMaterialType(BuMaterialType materialType) throws Exception;

    /**
     * 批量删除物资类型
     *
     * @param ids 物资类型ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

}
