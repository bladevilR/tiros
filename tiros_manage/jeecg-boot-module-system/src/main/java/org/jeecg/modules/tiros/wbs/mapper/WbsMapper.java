package org.jeecg.modules.tiros.wbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.common.tiros.wbs.entity.WbsConf;
import org.jeecg.modules.tiros.wbs.bean.WbsPO;

import java.util.List;

/**
 * <p>
 * wbs更新mapper
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-29
 */
public interface WbsMapper {

    List<WbsPO> selectWbsPOList(@Param("wbsConf") WbsConf wbsConf);

    List<WbsPO> selectWbsPOTree(@Param("wbsConf") WbsConf wbsConf);

    List<WbsPO> selectByIdList(@Param("idList") List<String> idList, @Param("wbsConf") WbsConf wbsConf);

    void updateWbsList(@Param("list") List<WbsPO> allWbsPOList, @Param("table") String table, @Param("id") String id, @Param("wbs") String wbs);

    void updateWbs(@Param("wbsPO") WbsPO wbsPO, @Param("table") String table, @Param("id") String id, @Param("wbs") String wbs);

}
