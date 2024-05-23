package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysUserDepart;

import java.util.List;

/**
 * <p>
 * 用户部门 Mapper 接口
 * </p>
 */
public interface SysUserDepartMapper extends BaseMapper<SysUserDepart> {

    /**
     * 批量插入
     *
     * @param list 用户部门列表
     */
    void insertList(@Param("list") List<SysUserDepart> list);

    /**
     * 根据用户id和部门id批量删除
     *
     * @param list 用户部门列表
     * @return 删除记录数量
     */
    int deleteListByUserIdAndDepartId(@Param("list") List<SysUserDepart> list);

    /**
     * Gets user depart by uid.
     *
     * @param userId the user id
     * @return the user depart by uid
     */
    List<SysUserDepart> getUserDepartByUid(@Param("userId") String userId);

    /**
     * Select user name list by depart id list list.
     *
     * @param departIdList the depart id list
     * @return the list
     */
    List<String> selectUserNameListByDepartIdList(@Param("departIdList") List<String> departIdList);

}
