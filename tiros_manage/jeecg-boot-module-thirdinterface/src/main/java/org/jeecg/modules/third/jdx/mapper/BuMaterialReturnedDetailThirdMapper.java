package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuMaterialReturnedDetail;
import org.jeecg.modules.third.jdx.bean.bo.ConsumeSyncBO;

import java.util.List;

/**
 * <p>
 * 退料明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public interface BuMaterialReturnedDetailThirdMapper extends BaseMapper<BuMaterialReturnedDetail> {

    /**
     * 更新退料单的同步时间
     *
     * @param list 同步返回数据
     */
    void updateReturnSyncTime(@Param("list") List<ConsumeSyncBO> list);

    /**
     * 更新退料单的同步返回状态和时间
     *
     * @param list 同步返回数据
     */
    void updateReturnSyncResult(@Param("list") List<ConsumeSyncBO> list);

    /**
     * 根据退料明细id查询退料单id
     *
     * @param returnDetailIdList 退料明细id列表
     * @return 退料单id列表
     */
    List<String> selectReturnIdListByReturnDetailIdList(@Param("returnDetailIdList") List<String> returnDetailIdList);

    /**
     * 根据退料单id查询退料明细
     *
     * @param returnId 退料单id
     * @return 退料明细
     */
    List<BuMaterialReturnedDetail> selectListByReturnId(@Param("returnId") String returnId);

    /**
     * 根据退料单id列表查询退料明细
     *
     * @param returnIdList 退料单id列表
     * @return 退料明细
     */
    List<BuMaterialReturnedDetail> selectListByReturnIdList(@Param("returnIdList") List<String> returnIdList);

}
