package org.jeecg.modules.material.must.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.cost.bean.vo.CompareQueryVO;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.bean.vo.BuMaterialMustQueryVO;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021 -04-30
 */
public interface BuMaterialMustListMapper extends BaseMapper<BuMaterialMustList> {

    /**
     * Select material must list page page.
     *
     * @param page    the page
     * @param queryVO the query vo
     * @return the page
     */
    Page<BuMaterialMustList> selectMaterialMustListPage(Page<BuMaterialMustList> page, @Param("queryVO") BuMaterialMustQueryVO queryVO);

    /**
     * 根据物料消耗对比查询vo查询对应的必换件清单
     *
     * @param queryVO 物料消耗对比查询vo
     * @return 必换件清单
     */
    List<BuMaterialMustList> selectListByCompareQueryVO(@Param("queryVO") CompareQueryVO queryVO);

    /**
     * Insert batch.
     *
     * @param list the list
     */
    void insertBatch(@Param("list") List<BuMaterialMustList> list);

    String selectAssetTypeId(@Param("structureId") String structureId);

    void setGroupId();

}
