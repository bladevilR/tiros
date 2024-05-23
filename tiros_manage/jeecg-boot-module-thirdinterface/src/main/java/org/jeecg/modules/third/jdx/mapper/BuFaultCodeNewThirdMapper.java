package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuFaultCodeNew;
import org.jeecg.modules.third.jdx.bean.BuMaterialTools;

import java.util.List;

/**
 * <p>
 * 故障编码(新) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-30
 */
public interface BuFaultCodeNewThirdMapper extends BaseMapper<BuFaultCodeNew> {

    /**
     * 批量插入
     *
     * @param list 故障编码(新)列表
     */
    void insertList(@Param("list") List<BuFaultCodeNew> list);

    /**
     * 批量更新
     *
     * @param list 故障编码(新)列表
     */
    void updateList(@Param("list") List<BuFaultCodeNew> list);

}
