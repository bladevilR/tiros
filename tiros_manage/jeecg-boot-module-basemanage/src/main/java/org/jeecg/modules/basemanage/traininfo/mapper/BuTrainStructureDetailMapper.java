package org.jeecg.modules.basemanage.traininfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainStructureDetail;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainStructureDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆结构明细，可以从车车型设备结构导入，导入的话应根据初始数量生成对应设备部件的数据 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
public interface BuTrainStructureDetailMapper extends BaseMapper<BuTrainStructureDetail> {

    /**
     * 批量插入
     *
     * @param list 车辆结构明细列表
     */
    void insertList(@Param("list") List<BuTrainStructureDetail> list);

    /**
     * 查询所有
     *
     * @param structId 车辆结构id
     * @return
     */
    List<BuTrainStructureDetail> selectListByStructId(String structId);

    /**
     * 根据条件查询车辆结构明细列表
     *
     * @param queryVO 查询条件
     * @return 车辆结构明细列表
     */
    List<BuTrainStructureDetail> selectListByBuTrainStructureDetailQueryVO(BuTrainStructureDetailQueryVO queryVO);

    /**
     * 根据车辆结构id查询车辆结构明细编码列表
     *
     * @param trainStructId 车辆结构id
     * @return 车辆结构明细编码列表
     */
    List<String> selectCodeListByTrainStructId(String trainStructId);

    /**
     * 根据上级id查询兄弟节点的最大code
     *
     * @param parentId 上级id
     * @return 兄弟节点的最大code
     */
    String selectBrotherMaxCodeByParentId(@Param("parentId") String parentId);

}
