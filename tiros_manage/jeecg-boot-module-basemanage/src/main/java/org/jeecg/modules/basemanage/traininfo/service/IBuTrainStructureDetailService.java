package org.jeecg.modules.basemanage.traininfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeCopyVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainStructureDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆结构明细，可以从车车型设备结构导入，导入的话应根据初始数量生成对应设备部件的数据 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
public interface IBuTrainStructureDetailService extends IService<BuTrainStructureDetail> {

    /**
     * 根据条件查询子节点
     *
     * @param queryVO 查询条件
     * @return 车辆结构明细列表
     * @throws Exception 异常信息
     */
    List<BuTrainStructureDetail> selectTrainStructureDetailChildren(BuTrainStructureDetailQueryVO queryVO) throws Exception;


//    /**
//     * 复制车辆设备到车辆结构
//     *
//     * @param trainAssetTypeCopy
//     * @return
//     */
//    Boolean trainAssetTypeCopy(BuTrainAssetTypeCopyVO trainAssetTypeCopy);

    /**
     * 导入设备类型创建车辆结构明细
     *
     * @param copyVO （childId：设备类型id，多个逗号分隔；parentId：上级车辆结构明细id；trainStructId：车辆结构id）
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean importAssetTypeToStructure(BuTrainAssetTypeCopyVO copyVO) throws Exception;

    /**
     * 新增车辆结构明细
     *
     * @param trainStructureDetail 车辆结构明细信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveTrainStructureDetail(BuTrainStructureDetail trainStructureDetail) throws Exception;

    /**
     * 修改车辆结构明细
     *
     * @param trainStructureDetail 车辆结构明细信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateTrainStructureDetail(BuTrainStructureDetail trainStructureDetail) throws Exception;

    /**
     * 根据车辆结构明细ids删除车辆结构明细
     *
     * @param detailIds 车辆结构明细ids，多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatchByDetailIds(String detailIds) throws Exception;

    /**
     * 根据车辆结构id删除车辆结构明细
     *
     * @param trainStructureId 车辆结构id
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatchByTrainStructureId(String trainStructureId) throws Exception;

}
