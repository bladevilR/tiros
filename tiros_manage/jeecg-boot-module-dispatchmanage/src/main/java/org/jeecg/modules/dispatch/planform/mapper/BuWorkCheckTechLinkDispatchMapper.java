package org.jeecg.modules.dispatch.planform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.planform.bean.BuWorkCheckTechLink;

import java.util.List;

/**
 * <p>
 * 参考工艺文件【模板】 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-18
 */
public interface BuWorkCheckTechLinkDispatchMapper extends BaseMapper<BuWorkCheckTechLink> {

    /**
     * 根据检查表单模板id查询关联工艺文件
     *
     * @param workCheckId 检查表单模板id
     * @return 关联工艺文件
     */
    List<BuWorkCheckTechLink> selectListByWorkCheckId(@Param("workCheckId") String workCheckId);

}
