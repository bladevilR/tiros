package org.jeecg.modules.basemanage.workcheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheckTechLink;

import java.util.List;

/**
 * <p>
 * 参考工艺文件【模板】 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021-05-17
 */
public interface BuWorkCheckTechLinkMapper extends BaseMapper<BuWorkCheckTechLink> {

    /**
     * 批量插入
     *
     * @param list xxxx列表
     */
    void insertList(@Param("list") List<BuWorkCheckTechLink> list);

}
