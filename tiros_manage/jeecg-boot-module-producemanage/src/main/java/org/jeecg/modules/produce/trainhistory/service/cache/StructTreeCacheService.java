package org.jeecg.modules.produce.trainhistory.service.cache;

import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainStructureDetailTreeVO;

import java.util.List;

/**
 * <p>
 * 车辆结构信息缓存 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-13
 */
public interface StructTreeCacheService {

        /**
     * 根据车辆结构id查询车辆结构明细树，做缓存
     *
     * @param structId 车辆结构id
     * @return 车辆结构明细树
     * @throws Exception 异常信息
     */
    List<BuTrainStructureDetailTreeVO> treeAllStructureByStructId(String structId) throws Exception;

}
