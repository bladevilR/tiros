package org.jeecg.modules.basemanage.traininfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainType;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainTypeQueryVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 车型信息 如：A型车，B型车 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-14
 */
@Component("buTrainTypeMapper")
public interface BuTrainTypeMapper extends BaseMapper<BuTrainType> {

    /**
     * 分页
     *
     * @param page
     * @param trainType
     * @return
     */
    List<BuTrainType> selectTrainTypePage(Page<BuTrainType> page, @Param("trainType") BuTrainTypeQueryVO trainType);

}
