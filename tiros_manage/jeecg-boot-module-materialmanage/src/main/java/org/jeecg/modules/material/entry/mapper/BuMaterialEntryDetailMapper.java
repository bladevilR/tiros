package org.jeecg.modules.material.entry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryQueryVO;

import java.util.List;

/**
 * <p>
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryDetailMapper extends BaseMapper<BuMaterialEntryDetail> {

    /**
     * 批量插入
     *
     * @param list 入库明细列表
     */
    void insertList(@Param("list") List<BuMaterialEntryDetail> list);

    /**
     * 根据条件分页查询
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 结果集
     */
    IPage<BuMaterialEntryDetail> selectPageByCondition(IPage<BuMaterialEntryDetail> page, @Param("queryVO") BuMaterialEntryQueryVO queryVO);

    /**
     * 根据入库单id查询入库明细列表
     *
     * @param entryOrderId 入库单id
     * @return 入库明细列表
     */
    List<BuMaterialEntryDetail> selectListByEntryOrderId(@Param("entryOrderId") String entryOrderId);

    /**
     * 根据入库明细id查询入库明细
     *
     * @param entryDetailId 入库明细id
     * @return 入库明细
     */
    BuMaterialEntryDetail selectEntryDetailById(@Param("entryDetailId") String entryDetailId);

}
