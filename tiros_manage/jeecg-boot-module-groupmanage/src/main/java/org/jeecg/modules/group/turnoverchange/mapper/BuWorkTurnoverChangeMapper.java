package org.jeecg.modules.group.turnoverchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.turnoverchange.bean.BuWorkTurnoverChange;
import org.jeecg.modules.group.turnoverchange.bean.vo.BuWorkTurnoverChangeQueryVO;

/**
 * <p>
 * 周转件更换，如果换上件没有记录，应先建立记录 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
public interface BuWorkTurnoverChangeMapper extends BaseMapper<BuWorkTurnoverChange> {

    /**
     * 根据条件分页查询周转件更换
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuWorkTurnoverChange> selectPageByCondition(IPage<BuWorkTurnoverChange> page, @Param("queryVO") BuWorkTurnoverChangeQueryVO queryVO);

    /**
     * 根据id查询周转件更换记录详情
     *
     * @param id 周转件更换记录id
     * @return 周转件更换记录详情
     */
    BuWorkTurnoverChange selectById(@Param("id") String id);

    /**
     * 根据周转件id获取周转件最后一次换上的车号
     *
     * @param turnoverId 周转件id
     * @return 最后一次换上的车号
     */
    String selectLastTrainNoByTurnoverId(@Param("turnoverId") String turnoverId);

}
