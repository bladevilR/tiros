package org.jeecg.modules.third.jdx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.jdx.bean.BuFaultInfo;

import java.util.Date;
import java.util.List;


/**
 * <p>
 * 故障信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-29
 */
public interface BuFaultInfoThirdMapper extends BaseMapper<BuFaultInfo> {

    /**
     * 批量插入
     *
     * @param list 故障信息列表
     */
    void insertList(@Param("list") List<BuFaultInfo> list);

    /**
     * 查询时间点后的故障信息
     *
     * @param date 时间点
     * @return 分页结果
     */
    List<BuFaultInfo> selectListByDate(@Param("date") Date date);

    /**
     * 根据故障id查询故障
     *
     * @param faultId 故障id
     * @return 故障
     */
    BuFaultInfo selectFaultById(@Param("faultId") String faultId);

}
