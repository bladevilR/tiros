package org.jeecg.modules.material.pallet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;

import java.util.List;

/**
 * <p>
 * 物资托盘 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
public interface BuMaterialPalletMapper extends BaseMapper<BuMaterialPallet> {

    /**
     * 批量插入
     *
     * @param list 物资托盘列表
     */
    void insertList(@Param("list") List<BuMaterialPallet> list);

    /**
     * 批量更新托盘使用状态
     *
     * @param list 托盘列表
     */
    void updateListUseStatus(@Param("list") List<BuMaterialPallet> list);

    /**
     * 根据条件分页查询物质托盘信息
     *
     * @param page       分页信息
     * @param searchText 搜索条件
     * @return 分页结果
     */
    IPage<BuMaterialPallet> selectPageBySearchText(IPage<BuMaterialPallet> page,
                                                   @Param("searchText") String searchText,
                                                   @Param("typeId") String typeId,
                                                   @Param("status") String status,
                                                   @Param("useStatus") String useStatus);

}
