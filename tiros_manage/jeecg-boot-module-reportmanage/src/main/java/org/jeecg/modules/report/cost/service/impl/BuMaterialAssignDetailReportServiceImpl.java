package org.jeecg.modules.report.cost.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.report.cost.bean.BuMaterialAssignDetail;
import org.jeecg.modules.report.cost.bean.vo.BuMaterialReceiveQueryVO;
import org.jeecg.modules.report.cost.mapper.BuMaterialAssignDetailReportMapper;
import org.jeecg.modules.report.cost.service.BuMaterialAssignDetailReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 物料分配明细 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Service
public class BuMaterialAssignDetailReportServiceImpl extends ServiceImpl<BuMaterialAssignDetailReportMapper, BuMaterialAssignDetail> implements BuMaterialAssignDetailReportService {

    @Resource
    private BuMaterialAssignDetailReportMapper buMaterialAssignDetailReportMapper;


    /**
     * @see BuMaterialAssignDetailReportService#listAssignDetail(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuMaterialAssignDetail> listAssignDetail(String applyDetailId) throws Exception {
        return buMaterialAssignDetailReportMapper.selectListByApplyDetailId(applyDetailId);
    }

    /**
     * @see BuMaterialAssignDetailReportService#pageMaterialReceive(BuMaterialReceiveQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialAssignDetail> pageMaterialReceive(BuMaterialReceiveQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (null == sysUser) {
            throw new JeecgBootException("无法获取到当前登录人信息，请先登录系统");
        }
        String workshopId = sysUser.getWorkshopId();
        if (StringUtils.isBlank(workshopId) || !Arrays.asList("CJ1", "CJ2").contains(workshopId)) {
            throw new JeecgBootException("当前登录人未明确归属车间，请使用其他账号操作");
        }
        queryVO.setWorkshopId(workshopId);

        if (null != queryVO.getEndDate()) {
            queryVO.setEndDate(DateUtils.transToDayEnd(queryVO.getEndDate()));
        }
        IPage<BuMaterialAssignDetail> page = buMaterialAssignDetailReportMapper.selectPageByReceiveQueryVO(new Page<>(pageNo, pageSize), queryVO);

        // 设置库房名称
        setDetailWarehouseName(page.getRecords());

        return page;
    }

    /**
     * @see BuMaterialAssignDetailReportService#exportMaterialReceive(BuMaterialReceiveQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public HSSFWorkbook exportMaterialReceive(BuMaterialReceiveQueryVO queryVO) throws Exception {
        List<BuMaterialAssignDetail> assignDetailList = buMaterialAssignDetailReportMapper.selectListByReceiveQueryVO(queryVO);

        // 设置库房名称
        setDetailWarehouseName(assignDetailList);

        return getExcel(assignDetailList);
    }


    private void setDetailWarehouseName(List<BuMaterialAssignDetail> assignDetailList) {
        if (CollectionUtils.isEmpty(assignDetailList)) {
            return;
        }

        for (BuMaterialAssignDetail assignDetail : assignDetailList) {
            String locationWbs = assignDetail.getLocationWbs();

            String warehouseCode = transToWarehouseCode(locationWbs);
            assignDetail.setWarehouseName(warehouseCode);
        }
    }

    private String transToWarehouseCode(String locationWbs) {
        if (StringUtils.isBlank(locationWbs)) {
            return null;
        }

        String substring = locationWbs.substring(4);
        int firstIndex = substring.indexOf(".");
        return substring.substring(0, firstIndex);
    }

    private HSSFWorkbook getExcel(List<BuMaterialAssignDetail> assignDetailList) {
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String[] excelHeader = {
                "线路",
                "车辆",
                "修程",
                "工单",
                "班组",
                "物料编码",
                "物资名称",
                "物资描述",
                "申请数量",
                "发放数量",
                "该条分配数量",
                "物资类别",
                "所属组织",
                "库位",
                "批次",
                "存放托盘",
                "发料人",
                "领料人",
                "领料时间"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(0);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 单元格格式，水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setWrapText(true);

        HSSFCellStyle redCellStyle = workbook.createCellStyle();
        redCellStyle.setFillPattern((short) 1);
        redCellStyle.setFillForegroundColor(IndexedColors.RED.index);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        for (BuMaterialAssignDetail detail : assignDetailList) {
            if (null == detail) {
                continue;
            }

            HSSFRow row = sheet.createRow(assignDetailList.indexOf(detail) + 1);
            row.createCell(0).setCellValue(detail.getLineName());
            row.createCell(1).setCellValue(detail.getTrainNo());
            row.createCell(2).setCellValue(detail.getRepairProName());
            row.createCell(3).setCellValue(detail.getOrderCode());
            row.createCell(4).setCellValue(detail.getGroupName());
            row.createCell(5).setCellValue(detail.getMaterialTypeCode());
            row.createCell(6).setCellValue(detail.getMaterialTypeName());
            row.createCell(7).setCellValue(detail.getMaterialTypeSpec());

            HSSFCell cell8 = row.createCell(8);
            cell8.setCellValue(detail.getApplyAmount());
            HSSFCell cell9 = row.createCell(9);
            cell9.setCellValue(detail.getReceiveAmount());
            HSSFCell cell10 = row.createCell(10);
            cell10.setCellValue(detail.getAmount());
            if (!detail.getApplyAmount().equals(detail.getReceiveAmount())){
                cell8.setCellStyle(redCellStyle);
                cell9.setCellStyle(redCellStyle);
                cell10.setCellStyle(redCellStyle);
            }

            row.createCell(11).setCellValue(paresCategory(detail.getUseCategory()));
            row.createCell(12).setCellValue(detail.getWarehouseName());
            row.createCell(13).setCellValue(detail.getLocationName());
            row.createCell(14).setCellValue(detail.getTradeNo());
            row.createCell(15).setCellValue(detail.getPalletName());
            row.createCell(16).setCellValue(detail.getSendUserName());
            row.createCell(17).setCellValue(detail.getConfirmUserName());
            row.createCell(18).setCellValue(null == detail.getConfirmTime() ? "" : DateUtils.datetimeFormat.get().format(detail.getConfirmTime()));
        }

        // 设置自动列宽
        // ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());

        return workbook;
    }

    private String paresCategory(Integer useCategory) {
        String category = "其他";
        switch (useCategory) {
            case 1:
                category = "必换件";
                break;
            case 2:
                category = "耦换件";
                break;
            case 3:
                category = "耗材";
                break;
            default:
                break;

        }
        return category;
    }

}
