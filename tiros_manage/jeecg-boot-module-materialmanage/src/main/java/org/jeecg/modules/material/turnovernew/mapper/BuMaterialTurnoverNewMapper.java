package org.jeecg.modules.material.turnovernew.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.turnovernew.bean.BuMaterialTurnoverNew;
import org.jeecg.modules.material.turnovernew.bean.vo.BuMaterialTurnoverQueryVONew;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 周转件 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-09-18
 */
public interface BuMaterialTurnoverNewMapper extends BaseMapper<BuMaterialTurnoverNew> {

    /**
     * 批量插入
     *
     * @param list 周转件列表
     */
    void insertList(@Param("list") List<BuMaterialTurnoverNew> list);

    /**
     * 根据条件查询分页
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialTurnoverNew> selectPageByCondition(IPage<BuMaterialTurnoverNew> page, @Param("queryVO") BuMaterialTurnoverQueryVONew queryVO);

    /**
     * 根据id查询周转件
     *
     * @param id 周转件id
     * @return 周转件
     */
    BuMaterialTurnoverNew selectTurnoverById(@Param("id") String id);

    List<Map<String, Object>> selectSystemShortNameIdMapList(@Param("systemShortNameList") List<String> systemShortNameList);

    List<Map<String, Object>> selectPlanNameIdMapList(@Param("planNameList") List<String> planNameList);

}
