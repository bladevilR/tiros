package org.jeecg.modules.report.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.daily.bean.BuOutsourceOutinDetail;

import java.util.List;

/**
 * <p>
 * 委外出入库明细 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-08
 */
public interface BuOutsourceOutinDetailReportMapper extends BaseMapper<BuOutsourceOutinDetail> {

    /**
     * 根据工单id列表查询委外出入库明细
     *
     * @param orderIdList 工单id列表
     * @param trainNo     车号
     * @return 委外出入库明细列表
     */
    List<BuOutsourceOutinDetail> selectListByOrderIdList(@Param("orderIdList") List<String> orderIdList, @Param("trainNo") String trainNo);

}
