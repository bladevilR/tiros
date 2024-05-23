package org.jeecg.modules.basemanage.formtemplate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.formtemplate.entity.BuBaseFormTemplate;
import org.jeecg.modules.basemanage.formtemplate.entity.vo.FormTemplateQueryVO;

/**
 * <p>
 * 数据记录表模版信息 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
public interface BuBaseFormTemplateMapper extends BaseMapper<BuBaseFormTemplate> {

    /**
     * 分页查询表单模板
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuBaseFormTemplate> selectFormTemplatePage(Page<BuBaseFormTemplate> page, @Param("queryVO") FormTemplateQueryVO queryVO);

}
