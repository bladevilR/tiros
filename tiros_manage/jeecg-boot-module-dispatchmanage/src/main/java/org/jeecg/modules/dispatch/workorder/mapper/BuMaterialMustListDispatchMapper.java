package org.jeecg.modules.dispatch.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.workorder.bean.BuMaterialMustList;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-15
 */
public interface BuMaterialMustListDispatchMapper extends BaseMapper<BuMaterialMustList> {

    /**
     * 查询物资类型规格描述
     *
     * @param canReplace 物资编码
     * @return 描述信息
     */
    String selectMaterialSpec(@Param("canReplace") String canReplace);
}
