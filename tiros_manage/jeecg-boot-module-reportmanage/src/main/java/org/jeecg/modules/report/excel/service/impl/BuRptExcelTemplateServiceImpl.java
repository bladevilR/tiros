package org.jeecg.modules.report.excel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.report.excel.bean.BuRptExcelTemplate;
import org.jeecg.modules.report.excel.mapper.BuRptExcelTemplateMapper;
import org.jeecg.modules.report.excel.service.BuRptExcelTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 报表中心 表格模板 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-08
 */
@Service
public class BuRptExcelTemplateServiceImpl extends ServiceImpl<BuRptExcelTemplateMapper, BuRptExcelTemplate> implements BuRptExcelTemplateService {

    @Resource
    private BuRptExcelTemplateMapper buRptExcelTemplateMapper;


    /**
     * @see BuRptExcelTemplateService#pageExcelTemplate(String, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRptExcelTemplate> pageExcelTemplate(String searchText, Integer pageNo, Integer pageSize) throws Exception {
        return buRptExcelTemplateMapper.selectPageBySearchText(new Page<>(pageNo, pageSize), searchText);
    }

    /**
     * @see BuRptExcelTemplateService#getExcelTemplateByCode(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRptExcelTemplate getExcelTemplateByCode(String tempCode) throws Exception {
        return buRptExcelTemplateMapper.selectByTempCode(tempCode);
    }

//    /**
//     * @see BuRptExcelTemplateService#saveExcelTemplate(BuRptExcelTemplate)
//     */
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean saveExcelTemplate(BuRptExcelTemplate excelTemplate) throws Exception {
//        if (StringUtils.isBlank(excelTemplate.getId())) {
//            buRptExcelTemplateMapper.insert(excelTemplate);
//        } else {
//            buRptExcelTemplateMapper.updateById(excelTemplate);
//        }
//
//        return true;
//    }

    /**
     * @see BuRptExcelTemplateService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buRptExcelTemplateMapper.deleteBatchIds(idList);

        return true;
    }

}
