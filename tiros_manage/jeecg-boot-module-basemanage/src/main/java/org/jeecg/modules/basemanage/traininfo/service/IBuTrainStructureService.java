package org.jeecg.modules.basemanage.traininfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructure;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainStructureVO;

import java.util.List;

/**
 * <p>
 * 车辆结构 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
public interface IBuTrainStructureService extends IService<BuTrainStructure> {

    /**
     * 根据线路id查找车辆结构
     *
     * @param lineId 线路id
     * @return 车辆结构列表
     * @throws Exception 异常信息
     */
    List<BuTrainStructure> listAll(String lineId) throws Exception;

    /**
     * 新增车辆结构
     *
     * @param trainStructureVO 车辆结构信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveTrainStructure(BuTrainStructureVO trainStructureVO) throws Exception;

    /**
     * 修改车辆结构
     *
     * @param trainStructureVO 车辆结构信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateTrainStructure(BuTrainStructureVO trainStructureVO) throws Exception;

    /**
     * 根据车辆结构ids删除车辆结构和车辆结构明细
     *
     * @param structureIds 车辆结构ids，多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatchByStructureIds(String structureIds) throws Exception;

    /**
     * 根据车辆结构id复制车辆结构信息和详细信息
     *
     * @param structureId 车辆结构id
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean copyTrainStructureByStructureId(String structureId) throws Exception;

    /**
     * 刷新重写该车辆结构的明细的所有编码和wbs
     *
     * @param structId 车辆结构id
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean rewriteCodeAndWbsByStructId(String structId) throws Exception;

}
