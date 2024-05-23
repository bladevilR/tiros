package org.jeecg.modules.basemanage.traininfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAssetType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeQueryVO;

import java.util.List;

/**
 * <p>
 * 设备类型结构 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
public interface BuTrainAssetTypeService extends IService<BuTrainAssetType> {

    /**
     * 根据条件查询设备类型子节点
     *
     * @param queryVO 查询条件
     * @return 设备类型列表
     * @throws Exception 异常
     */
    List<BuTrainAssetType> selectTrainAssetTypeChildren(BuTrainAssetTypeQueryVO queryVO) throws Exception;

    /**
     * 根据id查询设备类型信息
     *
     * @param id 设备类型id
     * @return 设备类型信息
     * @throws Exception 异常
     */
    BuTrainAssetType getAssetTypeById(String id) throws Exception;

    /**
     * 新增设备类型
     *
     * @param trainAssetType 设备类型信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveAssetType(BuTrainAssetType trainAssetType) throws Exception;

    /**
     * 更新设备类型
     *
     * @param trainAssetType 设备类型信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateTrainAssetTypeById(BuTrainAssetType trainAssetType) throws Exception;

    /**
     * 批量删除设备类型，包括子节点
     *
     * @param ids 设备类型ids,多个用英文逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 根据车号获取车辆系统（设备类型中属于系统的第一级）
     *
     * @param trainNo 车号
     * @return 设备类型中属于系统的第一级 列表
     * @throws Exception 异常
     */
    List<BuTrainAssetType> listTopSystemAssetType(String trainNo, String parentId) throws Exception;

    /**
     * 刷新重写该车型的所有编码和wbs
     *
     * @param trainTypeId 所属车型id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean rewriteCodeAndWbsByTrainTypeId(String trainTypeId) throws Exception;

}
