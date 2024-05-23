package org.jeecg.modules.basemanage.techbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetailTools;

import java.util.List;

/**
 * <p>
 * 作业指导书(工艺)明细步骤工器具 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuRepairTechBookDetailToolsMapper extends BaseMapper<BuRepairTechBookDetailTools> {

    /**
     * 批量插入
     *
     * @param list 步骤工器具列表
     */
    void insertList(@Param("list") List<BuRepairTechBookDetailTools> list);

    /**
     * 根据明细id查询工器具列表
     *
     * @param detailId 明细id
     * @return 工器具列表
     */
    List<BuRepairTechBookDetailTools> selectListByDetailId(@Param("detailId") String detailId);

}
