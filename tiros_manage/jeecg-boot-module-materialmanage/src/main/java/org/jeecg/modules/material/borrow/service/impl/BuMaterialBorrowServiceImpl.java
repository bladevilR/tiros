package org.jeecg.modules.material.borrow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.material.alert.bean.BuMaterialAlertVO;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrow;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrowDetail;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowQueryVO;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowReturnVO;
import org.jeecg.modules.material.borrow.mapper.BuMaterialBorrowDetailMapper;
import org.jeecg.modules.material.borrow.mapper.BuMaterialBorrowMapper;
import org.jeecg.modules.material.borrow.service.BuMaterialBorrowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 物资借用 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-17
 */
@Slf4j
@Service
public class BuMaterialBorrowServiceImpl extends ServiceImpl<BuMaterialBorrowMapper, BuMaterialBorrow> implements BuMaterialBorrowService {
    @Resource
    private BuMaterialBorrowMapper buMaterialBorrowMapper;
    @Resource
    private BuMaterialBorrowDetailMapper buMaterialBorrowDetailMapper;

    /**
     * @see BuMaterialBorrowService#page(BuMaterialBorrowQueryVO, Integer, Integer)
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public IPage<BuMaterialBorrow> page(BuMaterialBorrowQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialBorrowMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialBorrowService#page(BuMaterialBorrowQueryVO, Integer, Integer)
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public IPage<BuMaterialBorrow> appPage(BuMaterialBorrowQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialBorrowMapper.selectPageByConditionApp(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialBorrowService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        idList.forEach(id -> deleteMaterialBorrowDetail(id));
        buMaterialBorrowMapper.deleteBatchIds(idList);
        return true;
    }

    /**
     * @see BuMaterialBorrowService
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setReturn(List<BuMaterialBorrowReturnVO> borrowReturnVOs) throws Exception {
        borrowReturnVOs.forEach(item -> {
            Integer returnStatus = item.getReturnType() == 1 ? 1 : 2;
            updateBorrowStatus(item.getId(), returnStatus);
            List<BuMaterialBorrowDetail> materialBorrowDetailList = buMaterialBorrowDetailMapper.selectList(Wrappers.<BuMaterialBorrowDetail>lambdaQuery()
                    .select(BuMaterialBorrowDetail::getId).eq(BuMaterialBorrowDetail::getBorrowId, item.getId()));
            if (CollectionUtils.isNotEmpty(materialBorrowDetailList)) {
                materialBorrowDetailList.forEach(detail -> updateBorrowDetailStatus(detail.getId(), returnStatus));
            }
        });
        return true;
    }

    @Override
    public List<BuMaterialBorrowDetail> borrowDetailList(String id) throws Exception {
        return buMaterialBorrowDetailMapper.selectBorrowDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveMaterialBorrow(BuMaterialBorrow buMaterialBorrow) throws Exception {
        String id = UUIDGenerator.generate();
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String billNo = "JY" + nowTimeString;
        buMaterialBorrow.setId(id);
        buMaterialBorrow.setBillCode(billNo);
        buMaterialBorrow.insert();
        saveMaterialBorrowDetail(buMaterialBorrow);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterialBorrow(BuMaterialBorrow buMaterialBorrow) throws Exception {
        buMaterialBorrow.updateById();
        deleteMaterialBorrowDetail(buMaterialBorrow.getId());
        saveMaterialBorrowDetail(buMaterialBorrow);
        return true;
    }

    @Override
    public HSSFWorkbook exportMaterialBorrow(BuMaterialBorrowQueryVO queryVO) throws Exception {

        List<BuMaterialBorrow> buMaterialBorrowList = buMaterialBorrowMapper.selectListByCondition(queryVO);
        return getExcel(buMaterialBorrowList);
    }

    @Override
    public HSSFWorkbook exportDetailList(BuMaterialBorrowQueryVO queryVO) {
        List<BuMaterialBorrowDetail> buMaterialBorrowDetailList = buMaterialBorrowDetailMapper.selectInBorrowDetail(queryVO);
        if (1 == queryVO.getBorrowType()) {
            return getInExcel(buMaterialBorrowDetailList);
        } else if (2 == queryVO.getBorrowType()) {
            return getOutExcel(buMaterialBorrowDetailList);
        }
        return null;
    }

    @Override
    public boolean setDetailReturn(BuMaterialBorrowReturnVO borrowReturnVO) throws Exception {
        Integer returnStatus = borrowReturnVO.getReturnType() == 1 ? 1 : 2;
        updateBorrowDetailStatus(borrowReturnVO.getId(), returnStatus);
        BuMaterialBorrowDetail borrowDetail = buMaterialBorrowDetailMapper.selectById(borrowReturnVO.getId());
        List<BuMaterialBorrowDetail> materialBorrowDetailList = buMaterialBorrowDetailMapper.selectList(Wrappers.<BuMaterialBorrowDetail>lambdaQuery()
                .select(BuMaterialBorrowDetail::getId).eq(BuMaterialBorrowDetail::getBorrowId, borrowDetail.getBorrowId())
                .in(BuMaterialBorrowDetail::getReturnStatus, Arrays.asList(0, 3)));
        if (CollectionUtils.isEmpty(materialBorrowDetailList)) {
            updateBorrowStatus(borrowDetail.getBorrowId(), returnStatus);
        }
        return true;
    }

    /**
     * 更新借用记录状态
     *
     * @param id
     * @param status
     */
    private void updateBorrowStatus(String id, Integer status) {
        buMaterialBorrowMapper.updateById(new BuMaterialBorrow().setId(id).setReturnStatus(status));
    }

    /**
     * 更新借用记录明细状态
     *
     * @param id
     * @param status
     */
    private void updateBorrowDetailStatus(String id, Integer status) {
        buMaterialBorrowDetailMapper.updateById(new BuMaterialBorrowDetail().setId(id).setReturnStatus(status));
    }

    private void saveMaterialBorrowDetail(BuMaterialBorrow buMaterialBorrow) {
        List<BuMaterialBorrowDetail> borrowDetailList = buMaterialBorrow.getBorrowDetailList();
        if (CollectionUtils.isNotEmpty(borrowDetailList)) {
            borrowDetailList.forEach(detail -> {
                detail.setBorrowId(buMaterialBorrow.getId());
                detail.insert();
            });
        }
    }

    private void deleteMaterialBorrowDetail(String id) {
        buMaterialBorrowDetailMapper.delete(Wrappers
                .<BuMaterialBorrowDetail>lambdaUpdate()
                .eq(BuMaterialBorrowDetail::getBorrowId, id));
    }

    private HSSFWorkbook getInExcel(List<BuMaterialBorrowDetail> buMaterialBorrowDetailList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        //excel头部信息
        String[] excelHeader = {
                "序号",
                "物资编码",
                "物料描述",
                "单位",
                "借用数量   ",
                "发料数量",
                "库位",
                "备         注"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFRow row = null;
        HSSFRow row2 = null;
        HSSFCell cell = null;
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(3);

        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font ztFont = workbook.createFont();
        ztFont.setItalic(false);                     // 设置字体为斜体字
        ztFont.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        ztFont.setFontHeightInPoints((short) 16);    // 将字体大小设置为18px
        ztFont.setFontName("宋体");             // 将“宋体”字体应用到当前单元格上
        ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //加粗
        //              ztFont.setUnderline(Font.U_DOUBLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
        //              ztFont.setStrikeout(true);                  // 是否添加删除线
        titleStyle.setFont(ztFont);

        //----------------二级标题格样式----------------------------------
        HSSFCellStyle titleStyle2 = workbook.createCellStyle();        //表格样式
        titleStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font ztFont2 = workbook.createFont();
        ztFont2.setItalic(false);                     // 设置字体为斜体字
        ztFont2.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        ztFont2.setFontHeightInPoints((short) 11);    // 将字体大小设置为18px
        ztFont2.setFontName("宋体");             // 字体应用到当前单元格上
        ztFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //加粗
        //                 ztFont.setUnderline(Font.U_DOUBLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
        //                  ztFont.setStrikeout(true);                  // 是否添加删除线
        titleStyle2.setFont(ztFont2);


        //----------------单元格样式----------------------------------
        HSSFCellStyle cellStyle = workbook.createCellStyle();        //表格样式
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        Font cellFont = workbook.createFont();
        cellFont.setItalic(false);                     // 设置字体为斜体字
        cellFont.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        cellFont.setFontHeightInPoints((short) 10);    // 将字体大小设置为18px
        cellFont.setFontName("宋体");             // 字体应用到当前单元格上
        //            cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(cellFont);
        cellStyle.setWrapText(true);//设置自动换行


        // 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        row = sheet.createRow(0);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        cell = row.createCell(0);
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 7));
        // 设置单元格内容
        cell.setCellValue("物料借用单（" + buMaterialBorrowDetailList.get(0).getDeptName() + "）");
        cell.setCellStyle(titleStyle);

        HSSFRow rowDh = sheet.createRow(2);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        HSSFCell cellDh = rowDh.createCell(0);
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));
        // 设置单元格内容
        BuMaterialBorrow materialBorrow = buMaterialBorrowMapper.selectById(buMaterialBorrowDetailList.get(0).getBorrowId());
        cellDh.setCellValue("借用单号:" + materialBorrow.getBillCode());
        cellDh.setCellStyle(titleStyle2);



        // ------------------创建第二行(单位、填表日期)---------------------
        row = sheet.createRow(buMaterialBorrowDetailList.size() + 13); // 创建第二行
        row2 = sheet.createRow(buMaterialBorrowDetailList.size() + 14); // 创建第二行
        cell = row.createCell(0);
        cell.setCellValue("借用人：");
        cell.setCellStyle(titleStyle2);

        cell = row2.createCell(0);
        cell.setCellValue("借用日期：");
        cell.setCellStyle(titleStyle2);

        //               sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
        cell = row.createCell(2);
        cell.setCellValue("工长签字：");
        cell.setCellStyle(titleStyle2);

        cell = row2.createCell(2);
        cell.setCellValue("日期：");
        cell.setCellStyle(titleStyle2);

        cell = row.createCell(5);
        //                sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 16));
        cell.setCellValue("发料人签字：");
        cell.setCellStyle(titleStyle2);

        cell = row2.createCell(5);
        cell.setCellValue("发料人日期：");
        cell.setCellStyle(titleStyle2);


        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell1 = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell1.setCellValue(header);
            cell1.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        for (int i = 0; i < buMaterialBorrowDetailList.size(); i++) {
            if (null != buMaterialBorrowDetailList.get(i)) {
                List<Object> data = new ArrayList<>();
                BuMaterialBorrowDetail borrowVO = buMaterialBorrowDetailList.get(i);
                data.add(i + 1);
                data.add(null == borrowVO.getMaterialTypeCode() ? "" : borrowVO.getMaterialTypeCode());
                data.add(null == borrowVO.getMaterialTypeSpec() ? "" :borrowVO.getMaterialTypeName()+ "-[规格："+borrowVO.getMaterialTypeSpec()+"]");
                data.add(null == borrowVO.getMaterialTypeUnit() ? "" : borrowVO.getMaterialTypeUnit());
                data.add(null == borrowVO.getAmount() ? "" : borrowVO.getAmount());
                data.add(null == borrowVO.getAmount() ? "" : borrowVO.getAmount());
                data.add(null == borrowVO.getWarehouseName() ? "" : borrowVO.getWarehouseName());
                data.add(null == borrowVO.getRemark() ? "" : borrowVO.getRemark());

                int rowNum = 4 + i;    //从第四行开始
                row = sheet.createRow(rowNum);

                for (int j = 0; j < data.size(); j++) {        //将数据添加到单元格中
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, j, j));
                    cell = row.createCell(j);
                    cell.setCellValue("" + data.get(j) + "");
                    cell.setCellStyle(cellStyle);
                }

            }
        }
        if (buMaterialBorrowDetailList.size() < 5) {
            for (int i = buMaterialBorrowDetailList.size(); i < 5; i++) {
                List<Object> data = new ArrayList<>();
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                int rowNum = 4 + i;    //从第四行开始
                row = sheet.createRow(rowNum);
                for (int j = 0; j < data.size(); j++) {        //将数据添加到单元格中
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, j, j));
                    cell = row.createCell(j);
                    cell.setCellValue("" + data.get(j) + "");
                    cell.setCellStyle(cellStyle);
                }
            }
        }
        // 设置自动列宽
        ExcelUtil.setSizeColumn1(sheet, excelHeaderList.size());
        sheet.setColumnWidth(0, 1500);
        return workbook;
    }


    private HSSFWorkbook getOutExcel(List<BuMaterialBorrowDetail> buMaterialBorrowDetailList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        //  excel头部信息
        String[] excelHeader = {
                "序号",
                "物资编码",
                "物料描述",
                "单位",
                "数量   ",
                "借用理由",
                "预计归还日期",
                "备注"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFRow row = null;
        HSSFRow row2 = null;
        HSSFRow row3 = null;
        HSSFRow row4 = null;
        HSSFRow row5 = null;
        HSSFCell cell = null;
        HSSFCell cell3 = null;
        HSSFCell cell4 = null;
        HSSFCell cell5 = null;
        HSSFCell cell6 = null;
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(4);

        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font ztFont = workbook.createFont();
        ztFont.setItalic(false);                     // 设置字体为斜体字
        ztFont.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        ztFont.setFontHeightInPoints((short) 16);    // 将字体大小设置为18px
        ztFont.setFontName("宋体");             // 将“宋体”字体应用到当前单元格上
        ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //加粗
        //              ztFont.setUnderline(Font.U_DOUBLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
        //              ztFont.setStrikeout(true);                  // 是否添加删除线
        titleStyle.setFont(ztFont);

        //----------------二级标题格样式----------------------------------
        HSSFCellStyle titleStyle2 = workbook.createCellStyle();        //表格样式
        titleStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font ztFont2 = workbook.createFont();
        ztFont2.setItalic(false);                     // 设置字体为斜体字
        ztFont2.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        ztFont2.setFontHeightInPoints((short) 11);    // 将字体大小设置为18px
        ztFont2.setFontName("宋体");             // 字体应用到当前单元格上
        ztFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //加粗
        //                 ztFont.setUnderline(Font.U_DOUBLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
        //                  ztFont.setStrikeout(true);                  // 是否添加删除线
        titleStyle2.setFont(ztFont2);
        HSSFCellStyle titleStyle3 = workbook.createCellStyle();        //表格样式
        titleStyle3.setFont(ztFont2);

        //----------------单元格样式----------------------------------
        HSSFCellStyle cellStyle = workbook.createCellStyle();        //表格样式
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        Font cellFont = workbook.createFont();
        cellFont.setItalic(false);                     // 设置字体为斜体字
        cellFont.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        cellFont.setFontHeightInPoints((short) 10);    // 将字体大小设置为18px
        cellFont.setFontName("宋体");             // 字体应用到当前单元格上
        //            cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(cellFont);
        cellStyle.setWrapText(true);//设置自动换行

      /*  // 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        row = sheet.createRow(0);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        cell = row.createCell(0);
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        // 设置单元格内容
        cell.setCellValue("附录19");
        cell.setCellStyle(titleStyle);

        // 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        row3 = sheet.createRow(1);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        cell3 = row3.createCell(0);
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
        // 设置单元格内容
        cell3.setCellValue("（规范性附录）");
        cell3.setCellStyle(titleStyle);*/

        // 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        row4 = sheet.createRow(0);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        cell4 = row4.createCell(0);
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        // 设置单元格内容
        cell4.setCellValue("厂家物资借用单");
        cell4.setCellStyle(titleStyle);

        HSSFRow rowDh = sheet.createRow(1);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        HSSFCell cellDh = rowDh.createCell(0);
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
        // 设置单元格内容
        BuMaterialBorrow materialBorrow = buMaterialBorrowMapper.selectById(buMaterialBorrowDetailList.get(0).getBorrowId());
        cellDh.setCellValue("借用单号:" + materialBorrow.getBillCode());
        cellDh.setCellStyle(titleStyle2);

        // 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        row5 = sheet.createRow(2);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        cell5 = row5.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 2));
        // 设置单元格内容
        cell5.setCellValue("二级库：架大修车间二级库");
        cell5.setCellStyle(titleStyle3);

        // 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        //        row6 = sheet.createRow(3);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个

        cell6 = row5.createCell(4);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 7));
        cell6.setCellStyle(titleStyle2);
        cell6.setCellValue("厂家名称：" + (StringUtils.isNotBlank(materialBorrow.getBorrowDep()) ? materialBorrow.getBorrowDep() : ""));

        // 设置单元格内容
        //  cell6.setCellValue("厂家名称：" + null == buMaterialBorrowDetailList.get(0).getDeptName() ? "" : buMaterialBorrowDetailList.get(0).getDeptName());
        // cell6.setCellStyle(titleStyle2);


        // ------------------创建第二行(单位、填表日期)---------------------
        row = sheet.createRow(buMaterialBorrowDetailList.size() + 16); // 创建第二行
        row2 = sheet.createRow(buMaterialBorrowDetailList.size() + 17); // 创建第二行
        cell = row.createCell(0);
        cell.setCellValue("中心/车间负责人：");
        sheet.addMergedRegion(new CellRangeAddress(buMaterialBorrowDetailList.size() + 16, buMaterialBorrowDetailList.size() + 16, 0, 1));
        cell.setCellStyle(titleStyle2);

        cell = row2.createCell(0);
        cell.setCellValue("日期：");
        cell.setCellStyle(titleStyle2);

//                     sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
        cell = row.createCell(2);
        cell.setCellValue("专业工程师：");
        cell.setCellStyle(titleStyle2);

        cell = row2.createCell(2);
        cell.setCellValue("日期：");
        cell.setCellStyle(titleStyle2);

        cell = row.createCell(4);
        cell.setCellValue("二级库管理员：");
        cell.setCellStyle(titleStyle2);
        cell = row2.createCell(4);
        cell.setCellValue("日期：");
        cell.setCellStyle(titleStyle2);

        cell = row.createCell(6);
        cell.setCellValue("厂家经办人：");
        cell.setCellStyle(titleStyle2);
        cell = row2.createCell(6);
        cell.setCellValue("日期：");
        cell.setCellStyle(titleStyle2);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell1 = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell1.setCellValue(header);
            cell1.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        for (int i = 0; i < buMaterialBorrowDetailList.size(); i++) {
            if (null != buMaterialBorrowDetailList.get(i)) {
                List<Object> data = new ArrayList<>();
                BuMaterialBorrowDetail borrowVO = buMaterialBorrowDetailList.get(i);
                data.add(i + 1);
                data.add(borrowVO.getMaterialTypeCode());
                data.add(borrowVO.getMaterialTypeName()+"-[规格："+borrowVO.getMaterialTypeSpec()+"]");
                data.add(borrowVO.getMaterialTypeUnit());
                data.add(borrowVO.getAmount());
                data.add(borrowVO.getReason());
                data.add(null == borrowVO.getReturnDate() ? "" : dateFormat.format(borrowVO.getReturnDate()));
                data.add(borrowVO.getRemark());

                int rowNum = 5 + i;    //从第五行开始
                row = sheet.createRow(rowNum);
                for (int j = 0; j < data.size(); j++) {        //将数据添加到单元格中
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, j, j));
                    cell = row.createCell(j);
                    cell.setCellValue("" + (Objects.nonNull(data.get(j)) ? data.get(j) : "") + "");
                    cell.setCellStyle(cellStyle);
                }

            }
        }

        if (buMaterialBorrowDetailList.size() < 5) {
            for (int i = buMaterialBorrowDetailList.size(); i < 5; i++) {
                List<Object> data = new ArrayList<>();
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                int rowNum = 5 + i;    //从第四行开始
                row = sheet.createRow(rowNum);
                for (int j = 0; j < data.size(); j++) {        //将数据添加到单元格中
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, j, j));
                    cell = row.createCell(j);
                    cell.setCellValue("" + (Objects.nonNull(data.get(j)) ? data.get(j) : "") + "");
                    cell.setCellStyle(cellStyle);
                }
            }

        }
        // 设置自动列宽
        ExcelUtil.setSizeColumn1(sheet, excelHeaderList.size());
        sheet.setColumnWidth(0, 1500);
        return workbook;
    }

    private HSSFWorkbook getExcel(List<BuMaterialBorrow> buMaterialBorrowList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
 /*"出厂日期",
                "有效日期",
                "有效天数",*/
        String[] excelHeader = {
                "借用类型",
                "借用单号",
                "借用单位",
                "借用人员",
                "借用日期   ",
                "归还类型",
                "归还状态",
                "所属二级库",
                "备注"
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

        for (BuMaterialBorrow borrowVO : buMaterialBorrowList) {
            if (null != borrowVO) {
                HSSFRow row = sheet.createRow(buMaterialBorrowList.indexOf(borrowVO) + 1);
                row.createCell(0).setCellValue(getBorrowTypeName(borrowVO.getBorrowType()));
                row.createCell(1).setCellValue(borrowVO.getBillCode());
                row.createCell(2).setCellValue(borrowVO.getDeptName());
                row.createCell(3).setCellValue(borrowVO.getBorrowUserName());
                row.createCell(4).setCellValue(null == borrowVO.getBorrowDate() ? "" : dateFormat.format(borrowVO.getBorrowDate()));
                row.createCell(5).setCellValue(1 == borrowVO.getReturnType() ? "物料归还" : "调拨归还");
                row.createCell(6).setCellValue(getReturnStatusName(borrowVO.getReturnStatus()));
                row.createCell(7).setCellValue(borrowVO.getWarehouseName());
                row.createCell(8).setCellValue(borrowVO.getRemark());
            }
        }

        // 设置自动列宽
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());


        return workbook;
    }

    private String getBorrowTypeName(Integer borrowType) {
        String borrowTypeName = "";
        if (null != borrowType) {
            if (borrowType == 1) {
                borrowTypeName = "内单位";
            }
            if (borrowType == 2) {
                borrowTypeName = "外单位";
            }
        }
        return borrowTypeName;
    }

    private String getReturnStatusName(Integer returnStatus) {
        String returnStatusName = "";
        switch (returnStatus) {
            case 0:
                returnStatusName = "未归还";
                break;
            case 1:
                returnStatusName = "已归还";
                break;
            case 2:
                returnStatusName = "已调拨";
                break;
            case 3:
                returnStatusName = "未调拨";
                break;
            default:
                break;
        }
        return returnStatusName;
    }
}
