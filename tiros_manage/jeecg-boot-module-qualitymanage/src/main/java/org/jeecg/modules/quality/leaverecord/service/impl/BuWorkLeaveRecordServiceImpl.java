package org.jeecg.modules.quality.leaverecord.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.quality.leaverecord.bean.BuWorkLeaveRecord;
import org.jeecg.modules.quality.leaverecord.bean.vo.BuWorkLeaveRecordQueryVO;
import org.jeecg.modules.quality.leaverecord.mapper.BuWorkLeaveRecordMapper;
import org.jeecg.modules.quality.leaverecord.service.BuWorkLeaveRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 开口项 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-21
 */
@Slf4j
@Service
public class BuWorkLeaveRecordServiceImpl extends ServiceImpl<BuWorkLeaveRecordMapper, BuWorkLeaveRecord> implements BuWorkLeaveRecordService {

    @Resource
    private BuWorkLeaveRecordMapper buWorkLeaveRecordMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;

    /**
     * @see BuWorkLeaveRecordService
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkLeaveRecord> page(BuWorkLeaveRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buWorkLeaveRecordMapper.selectWorkLeaveRecordPage(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuWorkLeaveRecordService#saveWorkLeaveRecord(BuWorkLeaveRecord)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveWorkLeaveRecord(BuWorkLeaveRecord buWorkLeaveRecord) {

        String serialNumber = serialNumberGenerate.generateSerialNumberByCode("JDXKKX");
        buWorkLeaveRecord.setRecordCode(serialNumber);

        buWorkLeaveRecordMapper.insert(buWorkLeaveRecord);

        return true;
    }

    /**
     * @see BuWorkLeaveRecordService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        buWorkLeaveRecordMapper.deleteBatchIds(idList);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        idList.forEach(id->{
            buWorkLeaveRecordMapper.updateStatus(id);
        });
        return true;
    }

    @Override
    public HSSFWorkbook workLeaveRecordExport(BuWorkLeaveRecordQueryVO queryVO) throws Exception {
        List<BuWorkLeaveRecord> recordList = buWorkLeaveRecordMapper.selectListByCondition(queryVO);

        if (CollectionUtils.isEmpty(recordList)) {
            throw new JeecgBootException("没有数据,导出失败!");
        }

        return getExcel(recordList);

    }


    private HSSFWorkbook getExcel(List<BuWorkLeaveRecord> recordList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        //excel头部信息
        String[] excelHeader = {
                "序号",
                "编号",
                "所属车辆",
                "所属路线",
                "开口项目名称",
                "内容描述   ",
                "创建日期",
                "所属工单",
                "关联故障/任务",
                "状态"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFRow row = null;
        HSSFCell cell = null;
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(2);

        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font ztFont = workbook.createFont();
        ztFont.setItalic(false);                     // 设置字体为斜体字
        ztFont.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        ztFont.setFontHeightInPoints((short) 18);    // 将字体大小设置为18px
        ztFont.setFontName("宋体");             // 将“宋体”字体应用到当前单元格上
        ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //加粗
        //              ztFont.setUnderline(Font.U_DOUBLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
        //              ztFont.setStrikeout(true);                  // 是否添加删除线
        titleStyle.setFont(ztFont);


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
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 9));
        // 设置单元格内容
        cell.setCellValue("开口项列表");
        cell.setCellStyle(titleStyle);



        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell1 = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell1.setCellValue(header);
            cell1.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header),true);
        }

        for (int i = 0; i < recordList.size(); i++) {
            if (null != recordList.get(i)) {
                List<Object> data = new ArrayList<>();
                BuWorkLeaveRecord leaveRecordVO = recordList.get(i);
                data.add(i + 1);
                data.add(null == leaveRecordVO.getRecordCode() ? "": leaveRecordVO.getRecordCode());
                data.add(null == leaveRecordVO.getTrainNo() ? "" : leaveRecordVO.getTrainNo());
                data.add(null == leaveRecordVO.getLineName() ? "" : leaveRecordVO.getLineName());
                data.add(null == leaveRecordVO.getRecordName() ? "" : leaveRecordVO.getRecordName());
                data.add(null == leaveRecordVO.getRecordDesc() ? "" : leaveRecordVO.getRecordDesc());
                data.add(null == dateFormat.format(leaveRecordVO.getCreateTime()) ? "" : dateFormat.format(leaveRecordVO.getCreateTime()));
                data.add(null == leaveRecordVO.getOrderName() ? "" : leaveRecordVO.getOrderName());
                data.add(null == leaveRecordVO.getOrderTaskName() ? "" : leaveRecordVO.getOrderTaskName());
                data.add( 0 == leaveRecordVO.getStatus() ? "未处理" : "已处理");

                int rowNum = 3 + i;    //从第二行开始
                row = sheet.createRow(rowNum);

                for (int j = 0; j < data.size(); j++) {        //将数据添加到单元格中
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, j, j));
                    cell = row.createCell(j);
                    cell.setCellValue("" + data.get(j) + "");
                    cell.setCellStyle(cellStyle);
                }

            }
        }
        if (recordList.size() < 5) {
            for (int i = recordList.size(); i < 5; i++) {
                List<Object> data = new ArrayList<>();
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                data.add(" ");
                int rowNum = 3 + i;    //从第四行开始
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
       // ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
           ExcelUtil.setColumnWidth(sheet,excelHeaderList.size());

       // sheet.setColumnWidth(0, 2000);
//        sheet.autoSizeColumn(1, true);
        return workbook;
    }





}
