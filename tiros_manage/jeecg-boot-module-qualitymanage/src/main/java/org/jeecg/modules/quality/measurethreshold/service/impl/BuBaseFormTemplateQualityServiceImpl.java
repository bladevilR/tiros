package org.jeecg.modules.quality.measurethreshold.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.quality.measurethreshold.bean.BuBaseFormTemplate;
import org.jeecg.modules.quality.measurethreshold.bean.vo.BuBaseFormTemplateQueryVO;
import org.jeecg.modules.quality.measurethreshold.mapper.BuBaseFormTemplateQualityMapper;
import org.jeecg.modules.quality.measurethreshold.service.BuBaseFormTemplateQualityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 数据记录表模版信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Slf4j
@Service
public class BuBaseFormTemplateQualityServiceImpl extends ServiceImpl<BuBaseFormTemplateQualityMapper, BuBaseFormTemplate> implements BuBaseFormTemplateQualityService {

    @Resource
    private BuBaseFormTemplateQualityMapper buBaseFormTemplateQualityMapper;

    private static final String DATA_RECORD_FORM_CATEGORY_CODE = "B03A01";


    /**
     * @see BuBaseFormTemplateQualityService#pageDataRecordForm(BuBaseFormTemplateQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuBaseFormTemplate> pageDataRecordForm(BuBaseFormTemplateQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        queryVO.setDataRecordFormCategoryCode(DATA_RECORD_FORM_CATEGORY_CODE);
        return buBaseFormTemplateQualityMapper.selectDataRecordFormPage(new Page<>(pageNo, pageSize), queryVO);
    }

}
