package org.jeecg.modules.material.borrow.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrowDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowQueryVO;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021 -05-02
 */
public interface BuMaterialBorrowDetailMapper extends BaseMapper<BuMaterialBorrowDetail> {

    /**
     * Select borrow detail list.
     *
     * @param id the id
     * @return the list
     */
    List<BuMaterialBorrowDetail> selectBorrowDetail(@Param("id")String id);

    /**
     * Select borrow detail list.
     *
     * @param queryVO the query vo
     * @return the list
     */
    List<BuMaterialBorrowDetail> selectInBorrowDetail(@Param("queryVO") BuMaterialBorrowQueryVO queryVO);

}
