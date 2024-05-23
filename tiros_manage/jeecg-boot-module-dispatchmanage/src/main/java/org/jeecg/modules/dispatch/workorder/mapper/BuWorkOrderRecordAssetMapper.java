package org.jeecg.modules.dispatch.workorder.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuWorkOrderRecordAsset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 与工单作业记录表1对1关系 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-12
 */
public interface BuWorkOrderRecordAssetMapper extends BaseMapper<BuWorkOrderRecordAsset> {
    /**
     * 设备表
     * @param id
     * @return
     */
    List<BuWorkOrderRecordAsset> listEquipment(@Param("id") String id);
}
