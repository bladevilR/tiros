package org.jeecg.modules.tiros.third.maximo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.tiros.third.maximo.bean.BuMaximoTransData;

import java.util.List;

/**
 * <p>
 * 检修系统maximo数据同步中间表 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-06
 */
public interface BuMaximoTransDataMapper extends BaseMapper<BuMaximoTransData> {

    /**
     * 批量插入
     *
     * @param list 数据同步列表
     */
    void insertList(@Param("list") List<BuMaximoTransData> list);

}
