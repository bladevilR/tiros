package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialType;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 物资类型 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-07
 */
public interface BuMaterialTypeThirdMapper extends BaseMapper<BuMaterialType> {

    /**
     * 批量插入
     *
     * @param list 物资类型列表
     */
    void insertList(@Param("list") List<BuMaterialType> list);

    /**
     * 批量更新
     *
     * @param list 物资类型列表
     */
    void updateList(@Param("list") List<BuMaterialType> list);

    /**
     * 批量更新价格
     *
     * @param list 物资类型列表
     */
    void updateListPrice(@Param("list") List<BuMaterialType> list);

    /**
     * 获取物资类型id,code
     *
     * @return 物资类型id, code
     */
    List<Map<String, Object>> selectIdCodeListByCodeList(@Param("codeList") List<String> codeList);

}
