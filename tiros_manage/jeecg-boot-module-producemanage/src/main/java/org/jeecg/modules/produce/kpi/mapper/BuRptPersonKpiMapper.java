package org.jeecg.modules.produce.kpi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.produce.kpi.bean.BuRptPersonKpi;
import org.jeecg.modules.produce.kpi.bean.bo.SysUserBO;
import org.jeecg.modules.produce.trainhistory.bean.bo.IdNameBO;

import java.util.List;

/**
 * <p>
 * 个人绩效 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-06
 */
public interface BuRptPersonKpiMapper extends BaseMapper<BuRptPersonKpi> {

    void deleteAll();

    /**
     * 批量插入
     *
     * @param list 个人绩效列表
     */
    void insertList(@Param("list") List<BuRptPersonKpi> list);

    List<SysUserBO> selectUserListByUserIdList(@Param("userIdList") List<String> userIdList);
    List<SysUserBO> selectAllUser();

    List<IdNameBO> selectDepotListByGroupIdList(@Param("depotIdList") List<String> depotIdList);

    List<IdNameBO> selectWorkshopListByGroupIdList(@Param("workshopIdList") List<String> workshopIdList);

    List<IdNameBO> selectGroupListByGroupIdList(@Param("groupIdList") List<String> groupIdList);

    List<IdNameBO> selectLineListByGroupIdList(@Param("lineIdList") List<String> lineIdList);

}
