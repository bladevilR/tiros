package org.jeecg.modules.basemanage.workcheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckItem;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 作业检查记录表检查项明细【模板】 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021-05-17
 */
public interface BuWorkCheckItemMapper extends BaseMapper<BuWorkCheckItem> {

    /**
     * 批量插入
     *
     * @param list 作业检查记录表检查项明细【模板】列表
     */
    void insertList(@Param("list") List<BuWorkCheckItem> list);

}
