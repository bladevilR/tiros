package org.jeecg.modules.material.plan.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.config.bean.SysConfig;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.material.plan.bean.BuMaterialYearPlan;
import org.jeecg.modules.material.plan.bean.vo.BuMaterialYearPlanQueryVO;
import org.jeecg.modules.material.plan.mapper.BuMaterialYearPlanMapper;
import org.jeecg.modules.material.plan.service.BuMaterialYearPlanService;
import org.jeecg.modules.material.stock.service.BuMaterialStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2021-05-01
 */
@Service
public class BuMaterialYearPlanServiceImpl extends ServiceImpl<BuMaterialYearPlanMapper, BuMaterialYearPlan> implements BuMaterialYearPlanService {

    @Resource
    private BuMaterialYearPlanMapper materialYearPlanMapper;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private BuMaterialStockService buMaterialStockService;

    private static final String MATERIAL_YEAR_TRAIN_CONFIG_CODE = "MaterialYearTrain";
    ;


    @Override
    public Page<BuMaterialYearPlan> materialYearPlanPage(BuMaterialYearPlanQueryVO queryVO, Page<BuMaterialYearPlan> page) {
        if (null == queryVO.getSummary()) {
            queryVO.setSummary(false);
        }
        if (null == queryVO.getMaterialYearTrain()) {
            queryVO.setMaterialYearTrain(1);
        }

        Page<BuMaterialYearPlan> planPage = materialYearPlanMapper.materialYearPlanPage(page, queryVO);
        List<BuMaterialYearPlan> records = planPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(item -> item.setSbAmount(item.getSbAmount() * queryVO.getMaterialYearTrain()));
            if (queryVO.getSummary()) {
                List<BuMaterialYearPlan> resultList = new ArrayList<>();
                records.stream().collect(Collectors.groupingBy(BuMaterialYearPlan::getMaterialTypeId)).forEach((k, v) -> {
                            resultList.add(v.get(0).setSbAmount(v.stream().mapToDouble(BuMaterialYearPlan::getSbAmount).sum()));
                        }
                );
                planPage.setRecords(resultList);
            }
        }

        SysConfig materialYearTrainConfig = new SysConfig()
                .setConfigCode(MATERIAL_YEAR_TRAIN_CONFIG_CODE)
                .setConfigValue(queryVO.getMaterialYearTrain().toString())
                .setConfigName("物资计划维修车辆数")
                .setConfigRemark("每次查询时更新");
        SysConfig config = sysConfigService.getById(MATERIAL_YEAR_TRAIN_CONFIG_CODE);
        if (null == config) {
            sysConfigService.save(materialYearTrainConfig);
        } else {
            sysConfigService.updateById(materialYearTrainConfig);
        }

        return planPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMaterialYearPlan(BuMaterialYearPlan materialYearPlan) throws Exception {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        String orgCode = sysUser.getOrgCode();
        String workGroupId = materialYearPlanMapper.selectWorkGroupId(orgCode);
        materialYearPlan.setSbDeptId(workGroupId);
        materialYearPlan.setSbUserId(userId);
        materialYearPlan.setSbDate(new Date());
        return materialYearPlanMapper.insert(materialYearPlan) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterialYearPlan(BuMaterialYearPlan materialYearPlan) throws Exception {
        return materialYearPlanMapper.updateById(materialYearPlan) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        return materialYearPlanMapper.deleteBatchIds(idList) > 0;
    }

    @Override
    public HSSFWorkbook exportMaterialYearPlan(BuMaterialYearPlanQueryVO queryVO) throws Exception {
        List<BuMaterialYearPlan> materialYearPlanList = materialYearPlanMapper.selectBYQueryVO(queryVO);
        if (CollectionUtils.isEmpty(materialYearPlanList)) {
            //throw new JeecgBootException("没有数据，导出失败!");
            return null;
        }
        return getExcel(materialYearPlanList, queryVO.getMaterialYearTrain());
    }

    @Override
    public Integer getMaterialYearTrain() {
        SysConfig materialYearTrain = sysConfigService.getById(MATERIAL_YEAR_TRAIN_CONFIG_CODE);
        if (null != materialYearTrain) {
            return Integer.valueOf(materialYearTrain.getConfigValue());
        } else {
            return 1;
        }
    }

    private HSSFWorkbook getExcel(List<BuMaterialYearPlan> materialYearPlanList, Integer materialYear) throws Exception {

        String[] excelHeader = {
                "年份",
                "线路",
                "修程",
                "物资编码",
                "物资名称",
                "物资描述",
                "类型",
                "属性",
                "提报数量",
                "需求数量",
                "单位",
                "提报人员",
                "提报时间",
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("物资计划");
        HSSFRow sheetRow = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        for (BuMaterialYearPlan item : materialYearPlanList) {
            if (null != item) {
                item.setSbAmount(item.getSbAmount() * materialYear);
                double sumStock = buMaterialStockService.getMaterialSumStockAvailableAmount(item.getMaterialTypeId());
                HSSFRow row = sheet.createRow(materialYearPlanList.indexOf(item) + 1);
                row.createCell(0).setCellValue(item.getSbYear());
                row.createCell(1).setCellValue(item.getLineName());
                row.createCell(2).setCellValue(item.getRepairProgramName());
                row.createCell(3).setCellValue(item.getCode());
                row.createCell(4).setCellValue(item.getName());
                row.createCell(5).setCellValue(item.getSpec());
                row.createCell(6).setCellValue(getSbType(item.getSbType()));
                row.createCell(7).setCellValue(getMaterialTypeCategory(item.getMaterialTypeCategory()));
                row.createCell(8).setCellValue(item.getSbAmount());
                row.createCell(9).setCellValue(getNeedAmount(item.getSbAmount(), sumStock));
                row.createCell(10).setCellValue(item.getUnit());
                row.createCell(11).setCellValue(item.getSbUserName());
                row.createCell(12).setCellValue(item.getSbDate() != null ?
                        new SimpleDateFormat("yyyy-MM-dd").format(item.getSbDate()) : "");
            }
        }
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());

        return workbook;
    }

    private double getNeedAmount(Double sbAmount, Double sumStock) {
        if (sbAmount != null) {
            if (sumStock == null) {
                return sbAmount;
            } else {
                return sbAmount - sumStock > 0 ? sbAmount - sumStock : 0;
            }
        }
        return 0;
    }

    private String getSbType(Integer sbType) {
        String sbTypeStr = "";
        switch (sbType) {
            case 1:
                sbTypeStr = "必换件";
                break;
            case 2:
                sbTypeStr = "耦换件";
                break;
            case 3:
                sbTypeStr = "耗材";
                break;
            default:
                break;

        }
        return sbTypeStr;
    }

    private String getMaterialTypeCategory(Integer category) {
        String categoryStr = "";
        switch (category) {
            case 1:
                categoryStr = "紧固件";
                break;
            case 2:
                categoryStr = "备品备件";
                break;
            case 3:
                categoryStr = "车体";
                break;
            case 4:
                categoryStr = "车上电气";
                break;
            case 5:
                categoryStr = "辅助";
                break;
            case 6:
                categoryStr = "牵引";
                break;
            case 7:
                categoryStr = "制定";
                break;
            case 8:
                categoryStr = "转向架";
                break;
            default:
                break;

        }
        return categoryStr;
    }
}
