package org.jeecg.modules.dispatch.serialplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.dispatch.serialplan.bean.BuTrainStructureDetail;

/**
 * <p>
 * 车辆结构明细，可以从车车型设备结构导入，导入的话应根据初始数量生成对应设备部件的数据 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-15
 */
public interface BuTrainStructureDetailDispatchMapper extends BaseMapper<BuTrainStructureDetail> {

}