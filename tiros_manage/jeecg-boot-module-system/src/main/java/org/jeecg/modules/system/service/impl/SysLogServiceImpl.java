package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.mapper.SysLogMapper;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecg.modules.system.vo.LoginStatisticVO;
import org.jeecg.modules.system.vo.query.LoginStatisticQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统日志表 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

    @Resource
    private SysLogMapper sysLogMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * @功能：清空所有日志记录
     */
    @Override
    public void removeAll() {
        sysLogMapper.removeAll();
    }

    @Override
    public Long findTotalVisitCount() {
        return sysLogMapper.findTotalVisitCount();
    }

    //update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
    @Override
    public Long findTodayVisitCount(Date dayStart, Date dayEnd) {
        return sysLogMapper.findTodayVisitCount(dayStart, dayEnd);
    }

    @Override
    public Long findTodayIp(Date dayStart, Date dayEnd) {
        return sysLogMapper.findTodayIp(dayStart, dayEnd);
    }
    //update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数

    @Override
    public List<Map<String, Object>> findVisitCount(Date dayStart, Date dayEnd) {
        try {
            String dbType = sysBaseAPI.getDatabaseType();
            return sysLogMapper.findVisitCount(dayStart, dayEnd, dbType);
        } catch (SQLException e) {
        }
        return null;
    }

    /**
     * @see ISysLogService#listLoginStatistic(LoginStatisticQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<LoginStatisticVO> listLoginStatistic(LoginStatisticQueryVO queryVO) throws Exception {
        queryVO.toStartEndDate();
        Date startDate = queryVO.getStartDate();
        Date endDate = queryVO.getEndDate();

        long time1 = System.currentTimeMillis();
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>()
                .eq(SysLog::getLogType, 1)
                .between(SysLog::getCreateTime, startDate, endDate)
                .select(SysLog::getCreateTime, SysLog::getLogType, SysLog::getLogContent);
        List<SysLog> sysLogList = sysLogMapper.selectList(wrapper);
        long time2 = System.currentTimeMillis();
        System.out.println("sysLogMapper.selectList(wrapper) 耗时 ：" + (time2 - time1));

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        List<LoginStatisticVO> statisticVOList = new ArrayList<>();
        while (start.before(end)) {
            Date date = start.getTime();
            String dateStr = DateUtils.date_sdf_wz.get().format(date);
            dateStr = dateStr.split("年")[1];

            List<String> logContentList = sysLogList.stream()
                    .filter(item -> DateUtils.isSameDay(date, item.getCreateTime()))
                    .map(SysLog::getLogContent)
                    .collect(Collectors.toList());

            long userCount = logContentList.stream()
                    .filter(item -> item.contains("登录成功"))
                    .distinct()
                    .count();
            long loginCount = logContentList.stream()
                    .filter(item -> item.contains("登录成功"))
                    .count();
            long tokenCount = logContentList.stream()
                    .filter(item -> item.contains("刷新Token成功"))
                    .distinct()
                    .count();
            long useTimeCount = tokenCount + loginCount;

            LoginStatisticVO statisticVO = getLoginStatisticVO(start, statisticVOList);
            List<String> dateList = statisticVO.getDateList();
            if (!dateList.contains(dateStr)) {
                dateList.add(dateStr);
            }

            statisticVO.setDateList(dateList)
                    .setUserCount(statisticVO.getUserCount() + userCount)
                    .setLoginCount(statisticVO.getLoginCount() + loginCount)
                    .setUseTimeCount(statisticVO.getUseTimeCount() + useTimeCount);

            start.add(Calendar.DATE, 1);
        }

        for (LoginStatisticVO statisticVO : statisticVOList) {
            List<String> dateList = statisticVO.getDateList();
            dateList.sort(Comparator.comparing(String::toString));
            String firstDate = dateList.get(0);
            String lastDate = dateList.get(dateList.size() - 1);

            statisticVO.setDateRange(firstDate + " ~ " + lastDate);
        }

        return statisticVOList;
    }

    /**
     * @see ISysLogService#exportLoginStatisticExcel(LoginStatisticQueryVO)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public HSSFWorkbook exportLoginStatisticExcel(LoginStatisticQueryVO queryVO) throws Exception {
        List<LoginStatisticVO> statisticVOList = listLoginStatistic(queryVO);
        if (CollectionUtils.isEmpty(statisticVOList)) {
            return null;
        }
        return getExcel(statisticVOList);
    }


    private LoginStatisticVO getLoginStatisticVO(Calendar calendar, List<LoginStatisticVO> statisticVOList) {
        Date date = calendar.getTime();
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        int weekOfMonth = Math.min(calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH), 4);
        String dateStr = DateUtils.date_sdf_wz.get().format(date);
        dateStr = dateStr.split("年")[1];

        List<LoginStatisticVO> matchList = statisticVOList.stream()
                .filter(item -> year == item.getYear() && month == item.getMonth() && weekOfMonth == item.getWeekOfMonth())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(matchList)) {
            return matchList.get(0);
        } else {
            LoginStatisticVO statisticVO = new LoginStatisticVO()
                    .setYear(year)
                    .setMonth(month)
                    .setWeekOfMonth(weekOfMonth)
                    .setWeekOfMonthDesc(getWeekDesc(weekOfMonth))
                    .setDateList(new ArrayList<>(Collections.singletonList(dateStr)))
                    .setDateRange(null)
                    .setUserCount(0L)
                    .setLoginCount(0L)
                    .setUseTimeCount(0L);
            statisticVOList.add(statisticVO);
            return statisticVO;
        }
    }

    private String getWeekDesc(int weekOfMonth) {
        if (weekOfMonth == 1) {
            return "第一周";
        } else if (weekOfMonth == 2) {
            return "第二周";
        } else if (weekOfMonth == 3) {
            return "第三周";
        } else if (weekOfMonth == 4) {
            return "第四周";
        }
        return null;
    }

    private HSSFWorkbook getExcel(List<LoginStatisticVO> statisticVOList) {

//        String[] excelHeader = {
//                "年份",
//                "线路",
//                "修程",
//                "物资编码",
//                "物资名称",
//                "物资描述",
//                "类型",
//                "属性",
//                "提报数量",
//                "需求数量",
//                "单位",
//                "提报人员",
//                "提报时间",
//        };
        List<String> excelHeaderList = new ArrayList<>();
        excelHeaderList.add("月");
        for (LoginStatisticVO statisticVO : statisticVOList) {
            excelHeaderList.add(statisticVO.getYear() + "年" + statisticVO.getMonth() + "月");
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("架大修系统访问统计");
        HSSFRow sheetRow = sheet.createRow(0);

        // 单元格样式
        HSSFCellStyle cellStyle = initHssfCellStyle(workbook);
        HSSFCellStyle cellStyle2 = initHssfCellStyle2(workbook);

        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell.setCellValue(header);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        int monthSize = statisticVOList.size() / 4;
        int col = 1;
        for (int i = 0; i < monthSize; i++) {
            // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
            sheet.addMergedRegion(new CellRangeAddress(0, 0, col, col + 3));
            col = col + 4;
        }

        for (int i = 1; i <= 5; i++) {
            HSSFRow row = sheet.createRow(i);
            if (i == 1) {
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue("周");
                cell0.setCellStyle(cellStyle);
                for (LoginStatisticVO statisticVO : statisticVOList) {
                    int index = statisticVOList.indexOf(statisticVO) + 1;
                    HSSFCell cell = row.createCell(index);
                    cell.setCellValue(statisticVO.getWeekOfMonthDesc());
                    cell.setCellStyle(cellStyle);
                }
            } else if (i == 2) {
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue("日期范围");
                cell0.setCellStyle(cellStyle);
                for (LoginStatisticVO statisticVO : statisticVOList) {
                    int index = statisticVOList.indexOf(statisticVO) + 1;
                    HSSFCell cell = row.createCell(index);
                    cell.setCellValue(statisticVO.getDateRange());
                    cell.setCellStyle(cellStyle);
                }
            } else if (i == 3) {
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue("用户数");
                cell0.setCellStyle(cellStyle);
                for (LoginStatisticVO statisticVO : statisticVOList) {
                    int index = statisticVOList.indexOf(statisticVO) + 1;
                    HSSFCell cell = row.createCell(index);
                    cell.setCellValue(statisticVO.getUserCount());
                    cell.setCellStyle(cellStyle2);
                }
            } else if (i == 4) {
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue("登录次数");
                cell0.setCellStyle(cellStyle);
                for (LoginStatisticVO statisticVO : statisticVOList) {
                    int index = statisticVOList.indexOf(statisticVO) + 1;
                    HSSFCell cell = row.createCell(index);
                    cell.setCellValue(statisticVO.getLoginCount());
                    cell.setCellStyle(cellStyle2);
                }
            } else {
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue("使用次数");
                cell0.setCellStyle(cellStyle);
                for (LoginStatisticVO statisticVO : statisticVOList) {
                    int index = statisticVOList.indexOf(statisticVO) + 1;
                    HSSFCell cell = row.createCell(index);
                    cell.setCellValue(statisticVO.getUseTimeCount());
                    cell.setCellStyle(cellStyle2);
                }
            }
        }

        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());

        return workbook;
    }

    private HSSFCellStyle initHssfCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 竖直居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 下边框
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 左边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 上边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 右边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 字体
        Font cellFont = workbook.createFont();
        // 字体是否斜体
        cellFont.setItalic(false);
        // 字体是否加粗
        // cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 字体颜色
        cellFont.setColor(Font.COLOR_NORMAL);
        // 字体大小
        cellFont.setFontHeightInPoints((short) 12);
        // 字体
        cellFont.setFontName("宋体");
        // 字体应用到当前单元格上
        cellStyle.setFont(cellFont);
        return cellStyle;
    }

    private HSSFCellStyle initHssfCellStyle2(HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        // 竖直居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 下边框
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 左边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 上边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 右边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 字体
        Font cellFont = workbook.createFont();
        // 字体是否斜体
        cellFont.setItalic(false);
        // 字体是否加粗
        // cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 字体颜色
        cellFont.setColor(Font.COLOR_NORMAL);
        // 字体大小
        cellFont.setFontHeightInPoints((short) 12);
        // 字体
        cellFont.setFontName("宋体");
        // 字体应用到当前单元格上
        cellStyle.setFont(cellFont);
        return cellStyle;
    }

}
