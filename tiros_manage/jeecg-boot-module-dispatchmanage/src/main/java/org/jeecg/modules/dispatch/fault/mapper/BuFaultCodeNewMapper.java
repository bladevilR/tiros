package org.jeecg.modules.dispatch.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeNew;

import java.util.List;

/**
 * <p>
 * 090102003, 09 表示专业编码，01 表示系统编码  02 表示 子系统编码  003 表示部件编码
 * -& Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2021-06-29
 */
public interface BuFaultCodeNewMapper extends BaseMapper<BuFaultCodeNew> {

    List<BuFaultCodeNew> selectAll(@Param("searchText") String searchText);

    List<BuFaultCodeNew> selectByParentCode(@Param("code") String code);

    BuFaultCodeNew selectParentCodeByCode(@Param("code") String code);
}
