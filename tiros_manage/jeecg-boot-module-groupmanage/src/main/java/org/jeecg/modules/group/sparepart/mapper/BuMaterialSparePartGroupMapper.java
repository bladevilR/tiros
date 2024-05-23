package org.jeecg.modules.group.sparepart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.group.sparepart.bean.vo.BuMaterialSparePartQueryVO;

/**
 * <p>
 * 列管备件 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
public interface BuMaterialSparePartGroupMapper extends BaseMapper<BuMaterialSparePart> {

    /**
     * 根据条件查询分页
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuMaterialSparePart> selectPageByCondition(IPage<BuMaterialSparePart> page, @Param("queryVO") BuMaterialSparePartQueryVO queryVO);

    /**
     * 根据id查询列管备件信息 关联多表查询name
     *
     * @param id 列管备件id
     * @return 列管备件信息
     */
    BuMaterialSparePart getById(@Param("id") String id);

}
