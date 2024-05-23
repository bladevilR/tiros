package org.jeecg.modules.group.safeassume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.safeassume.bean.BuWorkSafeAssume;
import org.jeecg.modules.group.safeassume.bean.vo.BuWorkSafeAssumeQueryVO;

/**
 * <p>
 * 安全预想 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
public interface BuWorkSafeAssumeMapper extends BaseMapper<BuWorkSafeAssume> {

    /**
     * 根据条件分页查询安全预想
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuWorkSafeAssume> selectPageByCondition(IPage<BuWorkSafeAssume> page, @Param("queryVO") BuWorkSafeAssumeQueryVO queryVO);

    /**
     * 根据根据id查询安全预想详情
     *
     * @param id 安全预想id
     * @return 安全预想详情
     */
    BuWorkSafeAssume selectSafeAssumeById(@Param("id") String id);

}
