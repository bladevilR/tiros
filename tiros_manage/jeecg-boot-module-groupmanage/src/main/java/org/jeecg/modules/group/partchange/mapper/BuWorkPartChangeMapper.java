package org.jeecg.modules.group.partchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.partchange.bean.BuWorkPartChange;
import org.jeecg.modules.group.partchange.bean.vo.BuWorkPartChangeQueryVO;
import org.jeecg.modules.group.turnoverchange.bean.BuWorkTurnoverChange;

/**
 * <p>
 * 部件更换 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
public interface BuWorkPartChangeMapper extends BaseMapper<BuWorkPartChange> {

    /**
     * 根据条件分页查询部件更换
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuWorkPartChange> selectPageByCondition(IPage<BuWorkTurnoverChange> page, @Param("queryVO") BuWorkPartChangeQueryVO queryVO);

    /**
     * 根据id查询部件更换记录详情
     *
     * @param id 部件更换记录id
     * @return 部件更换记录详情
     */
    BuWorkPartChange selectById(@Param("id") String id);

}
