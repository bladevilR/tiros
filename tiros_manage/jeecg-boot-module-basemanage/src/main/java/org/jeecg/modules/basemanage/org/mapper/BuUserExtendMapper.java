package org.jeecg.modules.basemanage.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.org.bean.BuUserExtend;

import java.util.List;

/**
 * <p>
 * 用户扩展信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021 -01-19
 */
public interface BuUserExtendMapper extends BaseMapper<BuUserExtend> {

    /**
     * 批量插入
     *
     * @param list 用户扩展信息列表
     */
    void insertList(@Param("list") List<BuUserExtend> list);

}
