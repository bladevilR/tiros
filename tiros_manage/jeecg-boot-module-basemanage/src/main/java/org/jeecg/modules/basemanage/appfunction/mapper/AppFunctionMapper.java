package org.jeecg.modules.basemanage.appfunction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.appfunction.bean.AppFunction;

import java.util.List;

/**
 * <p>
 * APP功能模块 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
public interface AppFunctionMapper extends BaseMapper<AppFunction> {

    /**
     * 根据用户id查询用户的app功能菜单列表
     *
     * @param userId 用户id
     * @return 用户的app功能菜单列表
     */
    List<AppFunction> selectListByUserId(@Param("userId") String userId);

}
