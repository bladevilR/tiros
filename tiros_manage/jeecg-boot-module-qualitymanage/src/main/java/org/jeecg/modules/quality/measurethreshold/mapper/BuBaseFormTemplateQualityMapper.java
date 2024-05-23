package org.jeecg.modules.quality.measurethreshold.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.measurethreshold.bean.BuBaseFormTemplate;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuBaseFormTemplateQueryVO;

/**
 * <p>
 * 数据记录表模版信息 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
public interface BuBaseFormTemplateQualityMapper extends BaseMapper<BuBaseFormTemplate> {

    /**
     * 根据条件分页查询数据记录表单
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 数据记录表单列表
     */
    IPage<BuBaseFormTemplate> selectDataRecordFormPage(IPage<BuBaseFormTemplate> page, @Param("queryVO") BuBaseFormTemplateQueryVO queryVO);

}
