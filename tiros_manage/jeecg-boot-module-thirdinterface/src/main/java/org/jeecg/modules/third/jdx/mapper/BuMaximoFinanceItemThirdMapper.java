package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialTools;
import org.jeecg.modules.third.jdx.bean.BuMaximoFinanceItem;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 财务项目 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-27
 */
public interface BuMaximoFinanceItemThirdMapper extends BaseMapper<BuMaximoFinanceItem> {

    /**
     * 批量插入
     *
     * @param list 财务项目列表
     */
    void insertList(@Param("list") List<BuMaximoFinanceItem> list);

    /**
     * 批量更新
     *
     * @param list 财务项目列表
     */
    void updateList(@Param("list") List<BuMaximoFinanceItem> list);

    List<BuMaximoFinanceItem> selectListUsed();

    List<Map<String, Object>> selectTpPlanIdProjectIdList();
    List<Map<String, Object>> selectPlanIdProjectIdList();
    List<Map<String, Object>> selectOrderIdProjectIdList();
    List<Map<String, Object>> selectHistoryOrderIdProjectIdList();

    void updateTpPlanProjectIdBatch(@Param("idNewProjectIdMapList") List<Map<String, Object>> idNewProjectIdMapList);
    void updatePlanProjectIdBatch(@Param("idNewProjectIdMapList") List<Map<String, Object>> idNewProjectIdMapList);
    void updateOrderProjectIdBatch(@Param("idNewProjectIdMapList") List<Map<String, Object>> idNewProjectIdMapList);
    void updateHistoryOrderProjectIdBatch(@Param("idNewProjectIdMapList") List<Map<String, Object>> idNewProjectIdMapList);

    /**
     * 查询已存在的财务项目id
     *
     * @return 财务项目id列表
     */
    List<String> selectIdList();

}
