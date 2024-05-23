package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMtrWarehouse;

import java.util.List;

/**
 * <p>
 * 仓库信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-07
 */
public interface BuMtrWarehouseThirdMapper extends BaseMapper<BuMtrWarehouse> {


    /**
     * 批量插入
     *
     * @param list 仓库信息列表
     */
    void insertList(@Param("list") List<BuMtrWarehouse> list);

}
