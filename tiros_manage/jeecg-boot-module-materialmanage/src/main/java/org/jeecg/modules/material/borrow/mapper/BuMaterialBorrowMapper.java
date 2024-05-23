package org.jeecg.modules.material.borrow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrow;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowQueryVO;

import java.util.List;

/**
 * <p>
 * 物资借用 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -09-17
 */
public interface BuMaterialBorrowMapper extends BaseMapper<BuMaterialBorrow> {
    /**
     * 根据条件查询物资借用记录分页
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果 page
     */
    IPage<BuMaterialBorrow> selectPageByCondition(IPage<BuMaterialBorrow> page, @Param("queryVO") BuMaterialBorrowQueryVO queryVO);

    /**
     * 根据条件查询物资借用记录分页
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果 page
     */
    IPage<BuMaterialBorrow> selectPageByConditionApp(IPage<BuMaterialBorrow> page, @Param("queryVO") BuMaterialBorrowQueryVO queryVO);

    /**
     * Select list by condition list.
     *
     * @param queryVO the query vo
     * @return the list
     */
    List<BuMaterialBorrow> selectListByCondition(@Param("queryVO") BuMaterialBorrowQueryVO queryVO);
}
