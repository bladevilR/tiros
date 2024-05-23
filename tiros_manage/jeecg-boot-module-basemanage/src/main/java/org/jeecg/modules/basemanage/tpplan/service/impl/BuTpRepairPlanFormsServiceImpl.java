package org.jeecg.modules.basemanage.tpplan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.basemanage.formtemplate.entity.BuBaseFormTemplate;
import org.jeecg.modules.basemanage.formtemplate.mapper.BuBaseFormTemplateMapper;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanForms;
import org.jeecg.modules.basemanage.tpplan.bean.BuTpRepairPlanTaskForms;
import org.jeecg.modules.basemanage.tpplan.bean.vo.BuTpRepairPlanFormsQueryVO;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanFormsMapper;
import org.jeecg.modules.basemanage.tpplan.mapper.BuTpRepairPlanTaskFormsMapper;
import org.jeecg.modules.basemanage.tpplan.service.BuTpRepairPlanFormsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 列计划模板表单 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-17
 */
@Slf4j
@Service
public class BuTpRepairPlanFormsServiceImpl extends ServiceImpl<BuTpRepairPlanFormsMapper, BuTpRepairPlanForms> implements BuTpRepairPlanFormsService {

    @Resource
    private BuTpRepairPlanFormsMapper buTpRepairPlanFormsMapper;
    @Resource
    private BuBaseFormTemplateMapper buBaseFormTemplateMapper;
    @Resource
    private BuTpRepairPlanTaskFormsMapper buTpRepairPlanTaskFormsMapper;


    /**
     * @see BuTpRepairPlanFormsService#pageForms(BuTpRepairPlanFormsQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuTpRepairPlanForms> pageForms(BuTpRepairPlanFormsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buTpRepairPlanFormsMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuTpRepairPlanFormsService#listForms(BuTpRepairPlanFormsQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuTpRepairPlanForms> listForms(BuTpRepairPlanFormsQueryVO queryVO) throws Exception {
        return buTpRepairPlanFormsMapper.selectListByCondition(queryVO);
    }

    /**
     * @see BuTpRepairPlanFormsService#getFormById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuTpRepairPlanForms getFormById(String id) throws Exception {
        return buTpRepairPlanFormsMapper.selectFormById(id);
    }

    /**
     * @see BuTpRepairPlanFormsService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buTpRepairPlanFormsMapper.deleteBatchIds(idList);

        // 删除计划模板表单时，删除计划模板任务的表单关联
        LambdaQueryWrapper<BuTpRepairPlanTaskForms> taskFormsWrapper = new LambdaQueryWrapper<BuTpRepairPlanTaskForms>()
                .in(BuTpRepairPlanTaskForms::getPlanFormId, idList);
        buTpRepairPlanTaskFormsMapper.delete(taskFormsWrapper);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFormById(BuTpRepairPlanForms buTpRepairPlanForms) {
        // setWorkRecordType(buTpRepairPlanForms);
        return this.updateById(buTpRepairPlanForms);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveForm(BuTpRepairPlanForms buTpRepairPlanForms) {
        setWorkRecordType(buTpRepairPlanForms);
        return this.save(buTpRepairPlanForms);
    }

    private void setWorkRecordType(BuTpRepairPlanForms buTpRepairPlanForms) {
        Integer formType = buTpRepairPlanForms.getFormType();
        //类型是数据记录表
        if (formType == 1) {
            BuBaseFormTemplate formTemplate = buBaseFormTemplateMapper.selectById(buTpRepairPlanForms.getObjId());
            //分类是其它设置为新的作业记录表
            if ("1328995639249358850".equals(formTemplate.getCategory())) {
                buTpRepairPlanForms.setWorkRecordType(2);
            }
            //类型是作业记录表，作为老的作业记录表
        } else if (formType == 3) {
            buTpRepairPlanForms.setWorkRecordType(1);
        }
    }
}
