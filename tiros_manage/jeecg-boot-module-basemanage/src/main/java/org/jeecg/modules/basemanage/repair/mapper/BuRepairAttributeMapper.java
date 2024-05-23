package org.jeecg.modules.basemanage.repair.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.repair.bean.BuRepairAttribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.basemanage.repair.bean.vo.RepairAttributeQueryVO;

/**
 * <p>
 * 检修属性 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2024-05-21
 */
public interface BuRepairAttributeMapper extends BaseMapper<BuRepairAttribute> {

    /**
     * 根据条件分页查询检修属性
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuRepairAttribute> selectPageByCondition(IPage<BuRepairAttribute> page, @Param("queryVO") RepairAttributeQueryVO queryVO);

}
