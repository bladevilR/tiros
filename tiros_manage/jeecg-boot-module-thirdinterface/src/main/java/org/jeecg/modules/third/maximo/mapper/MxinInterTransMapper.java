package org.jeecg.modules.third.maximo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.third.maximo.bean.MxinInterTrans;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-31
 */
public interface MxinInterTransMapper extends BaseMapper<MxinInterTrans> {

    Long selectTransId();

}
