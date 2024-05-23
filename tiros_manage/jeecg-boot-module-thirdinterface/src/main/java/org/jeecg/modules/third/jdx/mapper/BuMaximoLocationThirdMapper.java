package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuFaultCodeNew;
import org.jeecg.modules.third.jdx.bean.BuMaximoLocation;

import java.util.List;

/**
 * <p>
 * maximo资产位置 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
public interface BuMaximoLocationThirdMapper extends BaseMapper<BuMaximoLocation> {

    /**
     * 批量插入
     *
     * @param list maximo资产位置列表
     */
    void insertList(@Param("list") List<BuMaximoLocation> list);

}
