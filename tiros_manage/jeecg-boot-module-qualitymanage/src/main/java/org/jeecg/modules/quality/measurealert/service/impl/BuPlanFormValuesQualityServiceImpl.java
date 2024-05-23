package org.jeecg.modules.quality.measurealert.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.quality.measurealert.bean.BuPlanFormValues;
import org.jeecg.modules.quality.measurealert.bean.vo.BuWorkMeasureAlertQueryVO;
import org.jeecg.modules.quality.measurealert.mapper.BuPlanFormValuesQualityMapper;
import org.jeecg.modules.quality.measurealert.service.BuPlanFormValuesQualityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 测量记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-23
 */
@Slf4j
@Service
public class BuPlanFormValuesQualityServiceImpl extends ServiceImpl<BuPlanFormValuesQualityMapper, BuPlanFormValues> implements BuPlanFormValuesQualityService {

    @Resource
    private BuPlanFormValuesQualityMapper buPlanFormValuesQualityMapper;

    /**
     * @see BuPlanFormValuesQualityService#page(BuWorkMeasureAlertQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuPlanFormValues> page(BuWorkMeasureAlertQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuPlanFormValues> page = buPlanFormValuesQualityMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        setWorkstationName(page.getRecords());

        return page;
    }


    private void setWorkstationName(List<BuPlanFormValues> formValuesList) {
        if (CollectionUtils.isNotEmpty(formValuesList)) {
            for (BuPlanFormValues formValues : formValuesList) {
                List<String> workstationNameList = formValues.getWorkstationNameList();
                if (CollectionUtils.isNotEmpty(workstationNameList)) {
                    formValues.setWorkstationNames(String.join( ",",workstationNameList));
                }
            }
        }
    }

}
