package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuRptMaterialUseRecord;

import java.util.List;

/**
 * <p>
 * 物料消耗明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-13
 */
public interface BuRptMaterialUseRecordThirdMapper extends BaseMapper<BuRptMaterialUseRecord> {

    /**
     * 批量插入
     *
     * @param list 物料消耗明细列表
     */
    void insertList(@Param("list") List<BuRptMaterialUseRecord> list);

}
