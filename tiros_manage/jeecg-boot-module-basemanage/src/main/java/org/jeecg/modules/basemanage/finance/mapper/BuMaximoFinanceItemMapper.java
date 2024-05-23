package org.jeecg.modules.basemanage.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.finance.bean.BuMaximoFinanceItem;

import java.util.List;

/**
 * <p>
 * 财务项目 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
public interface BuMaximoFinanceItemMapper extends BaseMapper<BuMaximoFinanceItem> {

    /**
     * 批量插入
     *
     * @param list maximo财务项目列表
     */
    void insertList(@Param("list") List<BuMaximoFinanceItem> list);

}
