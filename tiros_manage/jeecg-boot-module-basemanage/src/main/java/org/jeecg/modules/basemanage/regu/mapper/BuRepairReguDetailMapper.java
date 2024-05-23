package org.jeecg.modules.basemanage.regu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.regu.bean.vo.RepairReguDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 规程明细 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
public interface BuRepairReguDetailMapper extends BaseMapper<BuRepairReguDetail> {

    /**
     * 批量插入
     *
     * @param list 规程明细列表
     */
    void insertList(@Param("list") List<BuRepairReguDetail> list);

    /**
     * 根据条件查询规程明细列表
     *
     * @param queryVO 查询条件
     * @return 规程明细列表
     */
    List<BuRepairReguDetail> selectListByRepairReguDetailQueryVO(@Param("queryVO") RepairReguDetailQueryVO queryVO);

    /**
     * 根据id查规程明细及关联信息
     *
     * @param id 规程明细id
     * @return 规程明细及关联信息
     */
    BuRepairReguDetail selectReguDetailById(@Param("id") String id);

    /**
     * 根据id列表查询规程明细-树
     *
     * @param idList 规程明细id列表
     * @return 规程明细树型结构
     */
    List<BuRepairReguDetail> selectTreeByIdList(@Param("idList") List<String> idList);

}
