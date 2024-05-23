package org.jeecg.modules.quality.measurethreshold.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.measurethreshold.bean.BuWorkMeasureItem;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuWorkMeasureItemQueryVO;

import java.util.List;

/**
 * <p>
 * 测量项定义  Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
public interface BuWorkMeasureItemMapper extends BaseMapper<BuWorkMeasureItem> {

    /**
     * 根据表单id和行号列号获取测量项
     *
     * @param queryVO 表单id和行号列号
     * @return 测量项
     */
    List<BuWorkMeasureItem> selectByCondition(@Param("queryVO") BuWorkMeasureItemQueryVO queryVO);

    /**
     * 根据表单id获取测量项列表
     *
     * @param formId 表单id
     * @return 测量项列表
     */
    List<BuWorkMeasureItem> listMeasureItemByFormId(@Param("formId") String formId);
}
