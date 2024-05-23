package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainStructureDetail;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainHistoryTreeBaseVO;

import java.util.List;

/**
 * <p>
 * 车辆结构明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-28
 */
public interface BuTrainStructureDetailProduceService extends IService<BuTrainStructureDetail> {

    /**
     * 查询车辆履历树(线路+车辆)
     *
     * @return 车辆履历树
     * @throws Exception 异常信息
     */
    List<BuTrainHistoryTreeBaseVO> treeLineAndTrain() throws Exception;

}
