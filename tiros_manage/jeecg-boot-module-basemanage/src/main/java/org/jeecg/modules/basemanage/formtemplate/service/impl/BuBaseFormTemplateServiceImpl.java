package org.jeecg.modules.basemanage.formtemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.formtemplate.entity.BuBaseFormTemplate;
import org.jeecg.modules.basemanage.formtemplate.entity.vo.FormTemplateQueryVO;
import org.jeecg.modules.basemanage.formtemplate.mapper.BuBaseFormTemplateMapper;
import org.jeecg.modules.basemanage.formtemplate.service.IBuBaseFormTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 数据记录表模版信息 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Service
public class BuBaseFormTemplateServiceImpl extends ServiceImpl<BuBaseFormTemplateMapper, BuBaseFormTemplate> implements IBuBaseFormTemplateService {

    @Resource
    private BuBaseFormTemplateMapper buBaseFormTemplateMapper;


    /**
     * @see IBuBaseFormTemplateService#selectFormTemplatePage(Page, FormTemplateQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuBaseFormTemplate> selectFormTemplatePage(Page<BuBaseFormTemplate> page, FormTemplateQueryVO queryVO) throws Exception {
        IPage<BuBaseFormTemplate> formTemplatePage = buBaseFormTemplateMapper.selectFormTemplatePage(page, queryVO);
        setFormTypeAndWorkRecordType(formTemplatePage.getRecords());
        return formTemplatePage;
    }

    /**
     * @see IBuBaseFormTemplateService#getContentByFormId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String getContentByFormId(String id) throws Exception {
        BuBaseFormTemplate formTemplate = buBaseFormTemplateMapper.selectById(id);
        if (null == formTemplate) {
            throw new JeecgBootException("表单不存在");
        }
        return formTemplate.getContent();
    }

    /**
     * @see IBuBaseFormTemplateService#getDataJsonByFormId(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String getDataJsonByFormId(String id) throws Exception {
        BuBaseFormTemplate formTemplate = buBaseFormTemplateMapper.selectById(id);
        if (null == formTemplate) {
            throw new JeecgBootException("表单不存在");
        }
        return formTemplate.getDatajson();
    }

    /**
     * @see IBuBaseFormTemplateService#validFormTemplate(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean validFormTemplate(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuBaseFormTemplate> wrapper = new LambdaQueryWrapper<BuBaseFormTemplate>()
                .in(BuBaseFormTemplate::getId, idList);
        BuBaseFormTemplate plan = new BuBaseFormTemplate()
                .setStatus(1);
        buBaseFormTemplateMapper.update(plan, wrapper);

        return true;
    }

    /**
     * @see IBuBaseFormTemplateService#invalidFormTemplate(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invalidFormTemplate(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        LambdaQueryWrapper<BuBaseFormTemplate> wrapper = new LambdaQueryWrapper<BuBaseFormTemplate>()
                .in(BuBaseFormTemplate::getId, idList);
        BuBaseFormTemplate plan = new BuBaseFormTemplate()
                .setStatus(0);
        buBaseFormTemplateMapper.update(plan, wrapper);

        return true;
    }


    private void setFormTypeAndWorkRecordType(List<BuBaseFormTemplate> formTemplateList) {
        if (CollectionUtils.isEmpty(formTemplateList)) {
            return;
        }

        for (BuBaseFormTemplate formTemplate : formTemplateList) {
            String categoryName = formTemplate.getCategoryName();
            if ("数据记录表".equals(categoryName)) {
                formTemplate.setFormType(1)
                        .setWorkRecordType(0);
            } else if ("作业记录表".equals(categoryName)) {
                formTemplate.setFormType(3)
                        .setWorkRecordType(2);
            }
        }
    }

}
