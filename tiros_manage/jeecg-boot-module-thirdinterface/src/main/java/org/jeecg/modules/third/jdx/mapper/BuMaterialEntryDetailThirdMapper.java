package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialEntryDetail;

import java.util.List;

/**
 * <p>
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryDetailThirdMapper extends BaseMapper<BuMaterialEntryDetail> {

    /**
     * 批量插入
     *
     * @param list 入库明细列表
     */
    void insertList(@Param("list") List<BuMaterialEntryDetail> list);

}
