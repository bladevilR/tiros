package org.jeecg.modules.basemanage.techbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBookDetailMaterial;

import java.util.List;

/**
 * <p>
 * 作业指导书(工艺)明细步骤物资 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuRepairTechBookDetailMaterialMapper extends BaseMapper<BuRepairTechBookDetailMaterial> {

    /**
     * 批量插入
     *
     * @param list 步骤物资列表
     */
    void insertList(@Param("list") List<BuRepairTechBookDetailMaterial> list);

    /**
     * 根据明细id查询物资列表
     *
     * @param detailId 明细id
     * @return 物资列表
     */
    List<BuRepairTechBookDetailMaterial> selectListByDetailId(@Param("detailId") String detailId);

}
