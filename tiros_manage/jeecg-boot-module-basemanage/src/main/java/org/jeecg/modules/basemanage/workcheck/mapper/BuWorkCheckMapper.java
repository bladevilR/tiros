package org.jeecg.modules.basemanage.workcheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheck;
import org.jeecg.modules.basemanage.workcheck.bean.vo.BuWorkCheckQueryVO;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021-05-17
 */
public interface BuWorkCheckMapper extends BaseMapper<BuWorkCheck> {

    Page<BuWorkCheck> selectWorkCheckPage(@Param("page") Page<BuWorkCheck> page, @Param("queryVO") BuWorkCheckQueryVO queryVO);

    /**
     * 根据id查询检查表单详情，包含关联信息
     *
     * @param id 检查表单id
     * @return 检查表单详情，包含关联信息
     */
    BuWorkCheck selectWorkCheckById(@Param("id") String id);

}
