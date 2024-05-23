package org.jeecg.modules.produce.trainhistory.mapper;

import org.jeecg.modules.produce.trainhistory.bean.bo.IdNameBO;

import java.util.List;

/**
 * <p>
 * 车辆履历通用查询 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-04
 */
public interface TrainHistoryCommonMapper {

    List<IdNameBO> selectDepartIdName();

    List<IdNameBO> selectDepotIdName();

    List<IdNameBO> selectWorkshopIdName();

    List<IdNameBO> selectWorkstationIdName();

    List<IdNameBO> selectLineIdName();

    List<IdNameBO> selectTrainIdName();

}
