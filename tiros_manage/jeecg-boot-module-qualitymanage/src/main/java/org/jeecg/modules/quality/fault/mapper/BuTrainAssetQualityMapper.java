package org.jeecg.modules.quality.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.fault.bean.BuTrainAsset;

import java.util.List;

/**
 * <p>
 * 车辆设备，按车辆分区存放，如果后期设备太多，可以线路分表，设备更换时，如果这个设备对应的是周转件，从车上换下的设备需要在 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-29
 */
public interface BuTrainAssetQualityMapper extends BaseMapper<BuTrainAsset> {

}
