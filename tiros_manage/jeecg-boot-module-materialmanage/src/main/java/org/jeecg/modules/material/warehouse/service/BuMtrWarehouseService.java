package org.jeecg.modules.material.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.bean.vo.BuMtrWarehouseQueryVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 仓库信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
public interface BuMtrWarehouseService extends IService<BuMtrWarehouse> {

    /**
     * 获取所有仓库树
     *
     * @return 所有仓库树
     */
    List<BuMtrWarehouse> getAllTrees(boolean needFilterWorkshop) throws Exception;

    /**
     * 根据上级id获取下级仓库列表
     *
     * @param parentId 上级仓库id
     * @return 下级仓库列表
     */
    List<BuMtrWarehouse> listByParentId(String parentId, boolean needFilterWorkshop) throws Exception;

    /**
     * 分页查询仓库信息
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMtrWarehouse> page(BuMtrWarehouseQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id获取仓库信息
     *
     * @param id 仓库id
     * @return 仓库信息
     * @throws Exception 异常信息
     */
    BuMtrWarehouse getWarehouseById(String id) throws Exception;

    /**
     * 新增仓库信息
     *
     * @param buMtrWarehouse 仓库信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveWarehouse(BuMtrWarehouse buMtrWarehouse) throws Exception;

    /**
     * 修改仓库信息
     *
     * @param buMtrWarehouse 仓库信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateWarehouse(BuMtrWarehouse buMtrWarehouse) throws Exception;

    /**
     * 批量删除仓库信息
     *
     * @param ids 仓库ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 仓库关账/取消关账操作
     *
     * @param id 仓库id
     * @return 是否操作成功
     * @throws Exception 异常信息
     */
    boolean changeClose(String id) throws Exception;

    /**
     * excel导入库位信息
     *
     * @param excelFile excel文件
     * @param parentId  上级id
     * @return 是否导入成功
     * @throws Exception 异常信息
     */
    boolean importWarehouseInfoFromExcel(MultipartFile excelFile, String parentId) throws Exception;

}
