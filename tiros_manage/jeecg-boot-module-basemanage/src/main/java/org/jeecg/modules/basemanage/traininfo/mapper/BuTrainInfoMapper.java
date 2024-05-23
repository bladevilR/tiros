package org.jeecg.modules.basemanage.traininfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainInfoQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆结构，平时在界面显示时从车辆结构中查数据，只有在保存业务数据要用到具体的设备时，才用对应的：结构id+车辆ID去查到 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
public interface BuTrainInfoMapper extends BaseMapper<BuTrainInfo> {

    /**
     * 根据条件分页查询车辆信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    Page<BuTrainInfo> selectPageByCondition(Page<BuTrainInfo> page, @Param("queryVO") BuTrainInfoQueryVO queryVO);

    /**
     * 根据车辆号查询车辆信息
     *
     * @param trainNo 车辆号
     * @return 车辆信息
     */
    BuTrainInfo selectByTrainNo(String trainNo);

    /**
     * 根据车辆号查询车辆id
     *
     * @param trainNo 车辆号
     * @return 车辆id
     */
    String selectIdByTrainNo(String trainNo);

    /**
     * 更新车辆结构id为空（车辆结构删除时，需更新车辆信息中的车辆结构id为空）
     *
     * @param structIdList 车辆结构id列表
     */
    void updateStructIdNull(@Param("structIdList") List<String> structIdList);

}
