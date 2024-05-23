package org.jeecg.modules.third.trans.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.third.trans.bean.BuMaximoTransData;

import java.util.List;

/**
 * <p>
 * 检修系统maximo数据同步中间表 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-06
 */
public interface BuMaximoTransDataMapper extends BaseMapper<BuMaximoTransData> {

    /**
     * 获取所有未同步到maximo的数据同步中间表数据
     *
     * @return 中间表数据列表
     */
    List<BuMaximoTransData> selectUnTransList();

    /**
     * 更新数据的transId
     *
     * @param list 数据列表
     */
    void updateTransId(@Param("list") List<BuMaximoTransData> list);

    /**
     * 更新数据状态和消息和时间
     *
     * @param list 数据列表
     */
    void updateStatusAndMessageAndTime(@Param("list") List<BuMaximoTransData> list);

    /**
     * 更新数据为已处理
     *
     * @param transDataIdList 数据id列表
     */
    void updateHandledByTransDataIdList(@Param("transDataIdList") List<String> transDataIdList);

}
