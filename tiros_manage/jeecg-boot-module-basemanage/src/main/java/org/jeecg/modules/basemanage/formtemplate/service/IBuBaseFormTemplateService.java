package org.jeecg.modules.basemanage.formtemplate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.formtemplate.entity.BuBaseFormTemplate;
import org.jeecg.modules.basemanage.formtemplate.entity.vo.FormTemplateQueryVO;

/**
 * <p>
 * 数据记录表模版信息 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
public interface IBuBaseFormTemplateService extends IService<BuBaseFormTemplate> {

    /**
     * 分页查询表单模板
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuBaseFormTemplate> selectFormTemplatePage(Page<BuBaseFormTemplate> page, FormTemplateQueryVO queryVO) throws Exception;

    /**
     * 根据表单id获取content
     *
     * @param id 表单id
     * @return 表单content
     * @throws Exception 异常
     */
    String getContentByFormId(String id) throws Exception;

    /**
     * 根据表单id获取datajson
     *
     * @param id 表单id
     * @return 表单datajson
     * @throws Exception 异常
     */
    String getDataJsonByFormId(String id) throws Exception;

    /**
     * 批量设置表单有效
     *
     * @param ids 表单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean validFormTemplate(String ids) throws Exception;

    /**
     * 批量设置表单无效
     *
     * @param ids 表单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean invalidFormTemplate(String ids) throws Exception;

}
