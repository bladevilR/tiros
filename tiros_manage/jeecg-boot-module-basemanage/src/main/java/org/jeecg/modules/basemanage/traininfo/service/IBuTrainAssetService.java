package org.jeecg.modules.basemanage.traininfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainAsset;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetListQueryVOForApp;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetQueryVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTreeQueryVO;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainAssetTypeCopyVO;

import java.util.List;

/**
 * The interface Bu train asset service.
 */
public interface IBuTrainAssetService extends IService<BuTrainAsset> {

    /**
     * 根据条件查询车辆设备
     *
     * @param queryVO 查询条件
     * @return 车辆设备
     * @throws Exception 异常信息
     */
    List<BuTrainAsset> listTrainAsset(BuTrainAssetTreeQueryVO queryVO) throws Exception;

    /**
     * 根据车号和车辆结构明细查询下级车辆设备列表
     *
     * @param trainNo        车号
     * @param structDetailId 车辆结构明细id
     * @return 下级车辆设备列表
     * @throws Exception 异常
     */
    List<BuTrainAsset> listChildByTrainNoAndStructDetailId(String trainNo, String structDetailId) throws Exception;

    /**
     * 根据条件查询车辆设备-app使用
     *
     * @param queryVO 查询条件
     * @return 车辆设备列表
     * @throws Exception 异常信息
     */
    List<BuTrainAsset> listTrainAssetForApp(BuTrainAssetListQueryVOForApp queryVO) throws Exception;

    /**
     * 根据条件查询车辆设备子节点
     *
     * @param queryVO 查询条件
     * @return 车辆设备列表
     */
    List<BuTrainAsset> selectTrainAssetChild(BuTrainAssetQueryVO queryVO);

    /**
     * 保存车辆设备
     *
     * @param buTrainAsset 车辆设备
     * @return 是否保存成功
     * @throws Exception 异常信息
     */
    boolean saveTrainAsset(BuTrainAsset buTrainAsset) throws Exception;

    /**
     * 更新车辆设备
     *
     * @param buTrainAsset 车辆设备
     * @return 是否更新成功
     * @throws Exception 异常信息
     */
    boolean updateTrainAsset(BuTrainAsset buTrainAsset) throws Exception;

    /**
     * 根据车辆结构生成车辆设备
     *
     * @param id       车辆id
     * @param structId 车辆结构id
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean produceTrainAsset(String id, String structId) throws Exception;

    /**
     * 导入设备类型生成车辆设备
     *
     * @param importVO 导入信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean importByAssetType(BuTrainAssetTypeCopyVO importVO) throws Exception;

    /**
     * 删除车辆设备及其子节点
     *
     * @param ids the as list
     * @return the boolean
     */
    boolean removeAllByIds(List<String> ids) throws Exception;

}
