package org.jeecg.modules.basemanage.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.org.bean.BuPostionInfo;

import java.util.List;

/**
 * <p>
 * 岗位信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021 -01-19
 */
public interface BuPostionInfoMapper extends BaseMapper<BuPostionInfo> {

    /**
     * 批量插入
     *
     * @param list 岗位信息列表
     */
    void insertList(@Param("list") List<BuPostionInfo> list);

}
