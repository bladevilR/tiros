package org.jeecg.modules.basemanage.traininfo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainTypeQueryVO;

/**
 * <p>
 * 车型信息 如：A型车，B型车 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-14
 */
public interface IBuTrainTypeService extends IService<BuTrainType> {

    /**
     * 分页
     *
     * @param page
     * @param trainType
     * @return
     */
    Page<BuTrainType> selectTrainTypePage(Page<BuTrainType> page, BuTrainTypeQueryVO trainType);
}
