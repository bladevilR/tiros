package org.jeecg.modules.material.alert.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.material.alert.bean.BuAlertRead;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertQueryVO;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertVO;
import org.jeecg.modules.material.alert.service.BuMaterialAlertService;
import org.jeecg.modules.material.stock.mapper.BuMaterialStockMapper;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 物资预警查看 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/9/18
 */
@Slf4j
@Service
public class BuMaterialAlertServiceImpl implements BuMaterialAlertService {

    @Resource
    private BuMaterialStockMapper buMaterialStockMapper;
    @Resource
    private SysConfigService sysConfigService;
//    @Resource
//    private BuMtrWarehouseMapper buMtrWarehouseMapper;

    /**
     * @see BuMaterialAlertService#pageDefault(BuMaterialAlertQueryVO, Integer, Integer)
     */
    @Override
    public IPage<BuMaterialAlertVO> pageDefault(BuMaterialAlertQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
//        // 根据仓库id获取所有子仓库
//        List<BuMtrWarehouse> children = buMtrWarehouseMapper.selectAllChildrenTreeByParentId(queryVO.getWarehouseId());
//        List<String> childrenIdList = getChildrenIdList(children);
//        childrenIdList.add(queryVO.getWarehouseId());
//        queryVO.setWarehouseIdList(childrenIdList);

//        fillAlertVOList(page.getRecords(), queryVO.getAlertType());


        if(queryVO.getAlertType()!=null&&queryVO.getAlertType()==2){
            Date materialExpireDateLine;
            String configDay= sysConfigService.getConfigValueByCode(TirosConstant.MATERIAL_EXPIRE_WARNING_CODE);
            if(StringUtils.isNotBlank(configDay)){
                materialExpireDateLine = new Date(System.currentTimeMillis() + (Long.parseLong(configDay) * 24 * 60 * 60 * 1000));
            }else {
                materialExpireDateLine = new Date();
            }
            queryVO.setMaterialExpireDateLine(materialExpireDateLine);
        }
        IPage<BuMaterialAlertVO> page = buMaterialStockMapper.selectBuMaterialAlertVOPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        return page;
    }

    @Override
    public IPage<BuMaterialAlertVO> appPageDefault(BuMaterialAlertQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        queryVO.setUserId(sysUser.getId());
      return  buMaterialStockMapper.selectBuMaterialAlertVOPageByConditionApp(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialAlertService#
     */
    @Override
    public HSSFWorkbook exportWeekMaterialAlert() throws Exception {
        List<BuMaterialAlertVO> alertVOList = buMaterialStockMapper.selectWeekBuMaterialAlertVOList();
//        fillAlertVOList(alertVOList, null);
        return getExcel(alertVOList);

    }

    /**
     * @see BuMaterialAlertService#exportMonthMaterialAlert()
     */
    @Override
    public HSSFWorkbook exportMonthMaterialAlert() throws Exception {
        List<BuMaterialAlertVO> alertVOList = buMaterialStockMapper.selectMonthBuMaterialAlertVOList();
//        fillAlertVOList(alertVOList, null);
        return getExcel(alertVOList);
    }

    @Override
    public HSSFWorkbook exportThesholdMaterialAlert() {
        List<BuMaterialAlertVO> alertVOList = buMaterialStockMapper.selectThresholdBuMaterialAlertVOList();
        return getExcel(alertVOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean settingRead(String id) throws Exception {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return new BuAlertRead()
                .setAlertId(id)
                .setUserId(sysUser.getId())
                .insert();
    }


//    private void fillAlertVOList(List<BuMaterialAlertVO> alertVOList, Integer alertType) {
//        if (CollectionUtils.isEmpty(alertVOList)) {
//            return;
//        }
//
//        for (BuMaterialAlertVO alertVO : alertVOList) {
//            if (alertType != null) {
//                alertVO.setAlertType(alertType);
//            } else {
//                if (alertVO.getCurrentStock() < alertVO.getAlertStock()) {
//                    alertVO.setAlertType(1);
//                } else {
//                    alertVO.setAlertType(2);
//                }
//            }
//            if (alertVO.getAlertType() == 1) {
//                alertVO.setDiffStock(alertVO.getAlertStock() - alertVO.getCurrentStock());
//            }
//        }
//    }

    private HSSFWorkbook getExcel(List<BuMaterialAlertVO> alertVOList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
 /*"出厂日期",
                "有效日期",
                "有效天数",*/
        String[] excelHeader = {
                "物资编码",
                "物资名称",
                "预警类别",
                "物资类别",
                "警戒库存",
                "当前库存",
                "差量",
                "物料描述"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
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

        for (BuMaterialAlertVO alertVO : alertVOList) {
            if (null != alertVO) {
                HSSFRow row = sheet.createRow(alertVOList.indexOf(alertVO) + 1);
                row.createCell(0).setCellValue(alertVO.getMaterialCode());
                row.createCell(1).setCellValue(alertVO.getMaterialName());
                row.createCell(2).setCellValue(getAlertTypeName(alertVO.getAlertType()));
                row.createCell(3).setCellValue(getMaterialTypeName(alertVO.getMaterialType()));
                row.createCell(4).setCellValue(null == alertVO.getAlertStock() ? "" : alertVO.getAlertStock().toString());
                row.createCell(5).setCellValue(null == alertVO.getCurrentStock() ? "" : alertVO.getCurrentStock().toString());
                row.createCell(6).setCellValue(null == alertVO.getDiffStock() ? "" : alertVO.getDiffStock().toString());
/*                row.createCell(7).setCellValue(null == alertVO.getLeaveFactory() ? "" : dateFormat.format(alertVO.getLeaveFactory()));
                row.createCell(8).setCellValue(null == alertVO.getExpirDate() ? "" : dateFormat.format(alertVO.getExpirDate()));
                row.createCell(9).setCellValue(null == alertVO.getExpirDay() ? "" : alertVO.getExpirDay().toString());*/
                row.createCell(7).setCellValue(alertVO.getMaterialSpec());
            }
        }

        // 设置自动列宽
        //  ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());

        return workbook;
    }

    private String getAlertTypeName(Integer alertType) {
        String alertTypeName = "";

        if (null != alertType) {
            if (alertType == 1) {
                alertTypeName = "库存预警";
            }
            if (alertType == 2) {
                alertTypeName = "到期预警";
            }
        }

        return alertTypeName;
    }

    private String getMaterialTypeName(Integer materialType) {
        String materialTypeName = "";

        if (null != materialType) {
            if (materialType == 1) {
                materialTypeName = "必换件";
            }
            if (materialType == 2) {
                materialTypeName = "偶换件";
            }
            if (materialType == 3) {
                materialTypeName = "耗材";
            }
            if (materialType == -1) {
                materialTypeName = "其他";
            }
        }

        return materialTypeName;
    }

    private List<String> getChildrenIdList(List<BuMtrWarehouse> children) {
        Set<String> idSet = new HashSet<>();
        recursionAddChildId(idSet, children);

        return new ArrayList<>(idSet);
    }

    private void recursionAddChildId(Set<String> childrenIdSet, List<BuMtrWarehouse> warehouseList) {
        if (CollectionUtils.isNotEmpty(warehouseList)) {
            for (BuMtrWarehouse warehouse : warehouseList) {
                childrenIdSet.add(warehouse.getId());
                recursionAddChildId(childrenIdSet, warehouse.getChildren());
            }
        }
    }

}
