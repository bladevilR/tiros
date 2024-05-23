package org.jeecg.modules.dispatch.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.common.util.PercentUtils;
import org.jeecg.common.workflow.bean.WfBizStatus;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangMapper;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYear;
import org.jeecg.modules.dispatch.plan.bean.BuRepairPlanYearDetail;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanFarYearQueryVO;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearDetailVOWithTaskGantt;
import org.jeecg.modules.dispatch.plan.bean.vo.BuRepairPlanYearVOWithGantt;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearDetailMapper;
import org.jeecg.modules.dispatch.plan.mapper.BuRepairPlanYearMapper;
import org.jeecg.modules.dispatch.plan.service.BuRepairPlanYearService;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairPlan;
import org.jeecg.modules.dispatch.serialplan.bean.BuRepairProgram;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairPlanMapper;
import org.jeecg.modules.dispatch.serialplan.mapper.BuRepairProgramMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 年计划 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-22
 */
@Slf4j
@Service
public class BuRepairPlanYearServiceImpl extends ServiceImpl<BuRepairPlanYearMapper, BuRepairPlanYear> implements BuRepairPlanYearService {

    @Resource
    private BuRepairPlanYearMapper buRepairPlanYearMapper;
    @Resource
    private BuRepairPlanYearDetailMapper buRepairPlanYearDetailMapper;
    @Resource
    private BuRepairProgramMapper buRepairProgramMapper;
    @Resource
    private BuRepairExchangMapper buRepairExchangMapper;
    @Resource
    private BuRepairPlanMapper buRepairPlanMapper;


    /**
     * @see BuRepairPlanYearService#pagePlanYear(BuRepairPlanFarYearQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuRepairPlanYear> pagePlanYear(BuRepairPlanFarYearQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuRepairPlanYear> page = buRepairPlanYearMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        List<BuRepairPlanYear> planYearList = page.getRecords();
        if (CollectionUtils.isNotEmpty(planYearList)) {
            // 查询修程信息
            Map<String, BuRepairProgram> idProgramMap = getIdRepairProgramMap();
            // 设置年计划完成数量和进度
            setPlanYearFinishAndProgress(planYearList, idProgramMap);
        }

        return page.setRecords(planYearList);
    }

    /**
     * @see BuRepairPlanYearService#getPlanYearById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanYear getPlanYearById(String id) throws Exception {
        BuRepairPlanYear planYear = buRepairPlanYearMapper.selectPlanYearWithDetailById(id);
        if (null == planYear) {
            throw new JeecgBootException("年计划不存在");
        }

        // 查询修程信息
        Map<String, BuRepairProgram> idProgramMap = getIdRepairProgramMap();
        // 设置年计划完成数量和进度
        setPlanYearFinishAndProgress(Collections.singletonList(planYear), idProgramMap);

        return planYear;
    }

    /**
     * @see BuRepairPlanYearService#getYearGanttWithDetailGantt(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuRepairPlanYearVOWithGantt getYearGanttWithDetailGantt(String id) throws Exception {
        BuRepairPlanYear planYear = buRepairPlanYearMapper.selectPlanYearWithDetailById(id);

        return transformToGantt(planYear);
    }

    /**
     * @see BuRepairPlanYearService#savePlanYear(BuRepairPlanYearVOWithGantt)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean savePlanYear(BuRepairPlanYearVOWithGantt yearVOWithGantt) throws Exception {
        BuRepairPlanYear buRepairPlanYear = transformFromGantt(yearVOWithGantt);
        computeAmount(buRepairPlanYear);

        // 保存年计划
        buRepairPlanYearMapper.insert(buRepairPlanYear);

        // 保存年计划明细
        String planYearId = buRepairPlanYear.getId();
        List<BuRepairPlanYearDetail> detailList = buRepairPlanYear.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (BuRepairPlanYearDetail buRepairPlanYearDetail : detailList) {
                buRepairPlanYearDetail.setYearPlanId(planYearId);
                buRepairPlanYearDetailMapper.insert(buRepairPlanYearDetail);
            }
        }

        return true;
    }

    /**
     * @see BuRepairPlanYearService#updatePlanYear(BuRepairPlanYearVOWithGantt)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updatePlanYear(BuRepairPlanYearVOWithGantt yearVOWithGantt) throws Exception {
        BuRepairPlanYear buRepairPlanYear = transformFromGantt(yearVOWithGantt);
        computeAmount(buRepairPlanYear);

        String planYearId = buRepairPlanYear.getId();
        // 删除以前的年计划明细
        LambdaQueryWrapper<BuRepairPlanYearDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(BuRepairPlanYearDetail::getYearPlanId, planYearId);
        buRepairPlanYearDetailMapper.delete(detailWrapper);

        // 保存年计划明细
        List<BuRepairPlanYearDetail> detailList = buRepairPlanYear.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (BuRepairPlanYearDetail buRepairPlanYearDetail : detailList) {
                buRepairPlanYearDetail.setYearPlanId(planYearId);
                buRepairPlanYearDetailMapper.insert(buRepairPlanYearDetail);
            }
        }

        // 更新年计划
        buRepairPlanYearMapper.updateById(buRepairPlanYear);

        return true;
    }

    /**
     * @see BuRepairPlanYearService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> planYearIdList = Arrays.asList(ids.split(","));

        if (CollectionUtils.isNotEmpty(planYearIdList)) {
            LambdaQueryWrapper<BuRepairPlanYearDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(BuRepairPlanYearDetail::getYearPlanId, planYearIdList);
            buRepairPlanYearDetailMapper.delete(detailWrapper);

            LambdaQueryWrapper<WfBizStatus> wfWrapper = new LambdaQueryWrapper<>();
            wfWrapper.in(WfBizStatus::getBusinessKey, planYearIdList);
            new WfBizStatus().delete(wfWrapper);

            buRepairPlanYearMapper.deleteBatchIds(planYearIdList);
        }

        return true;
    }

    /**
     * @see BuRepairPlanYearService#sumRepairPlanYearDetailAmountByYearAndProgramId(Integer, String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int sumRepairPlanYearDetailAmountByYearAndProgramId(Integer year, String programId) throws Exception {
        Integer amountSum = buRepairPlanYearDetailMapper.selectAmountSumByYearAndProgramId(year, programId);
        return null == amountSum ? 0 : amountSum;
    }

    /**
     * @see BuRepairPlanYearService#exportYearPlanExport(BuRepairPlanFarYearQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public HSSFWorkbook exportYearPlanExport(BuRepairPlanFarYearQueryVO queryVO) throws Exception {
        List<BuRepairPlanYear> repairPlanYearList = buRepairPlanYearMapper.selectListByCondition(queryVO);
        if (CollectionUtils.isEmpty(repairPlanYearList)) {
            throw new JeecgBootException("没有数据,导出失败!");
        }

        return getExcel(repairPlanYearList, queryVO.getTitle());
    }


    private BuRepairPlanYearVOWithGantt transformToGantt(BuRepairPlanYear planYear) {
        BuRepairPlanYearVOWithGantt planYearGantt = new BuRepairPlanYearVOWithGantt();
        BeanUtils.copyProperties(planYear, planYearGantt);

        List<BuRepairPlanYearDetail> detailList = planYear.getDetailList();
        List<BuRepairPlanYearDetailVOWithTaskGantt> planYearDetailVOWithTaskGanttList = transformToGantt(detailList);

        Date startDate = null;
        Date finishDate = null;
        Optional<BuRepairPlanYearDetailVOWithTaskGantt> startOptional = planYearDetailVOWithTaskGanttList.stream()
                .filter(t -> t.getStart() != null)
                .min(Comparator.comparing(BuRepairPlanYearDetailVOWithTaskGantt::getStart));
        if (startOptional.isPresent()) {
            startDate = startOptional.get().getStart();
        }
        Optional<BuRepairPlanYearDetailVOWithTaskGantt> finishOptional = planYearDetailVOWithTaskGanttList.stream()
                .filter(t -> t.getFinish() != null)
                .max(Comparator.comparing(BuRepairPlanYearDetailVOWithTaskGantt::getFinish));
        if (finishOptional.isPresent()) {
            finishDate = finishOptional.get().getFinish();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(finishDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            finishDate = calendar.getTime();
        }

        planYearGantt.setUID(planYear.getId());
        planYearGantt.setName(planYear.getTitle());
        planYearGantt.setStartDate(startDate);
        planYearGantt.setFinishDate(finishDate);
        planYearGantt.setTasks(planYearDetailVOWithTaskGanttList);

        return planYearGantt;
    }

    private List<BuRepairPlanYearDetailVOWithTaskGantt> transformToGantt(List<BuRepairPlanYearDetail> detailList) {
        List<BuRepairPlanYearDetailVOWithTaskGantt> planYearDetailVOWithTaskGanttList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(detailList)) {
            for (BuRepairPlanYearDetail detail : detailList) {
                planYearDetailVOWithTaskGanttList.add(transformToGantt(detail));
            }
        }

        return planYearDetailVOWithTaskGanttList;
    }

    private BuRepairPlanYearDetailVOWithTaskGantt transformToGantt(BuRepairPlanYearDetail detail) {
        if (null == detail) {
            return null;
        }

        if (null != detail.getFinishDate()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(detail.getFinishDate());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            detail.setFinishDate(calendar.getTime());
        }

        BuRepairPlanYearDetailVOWithTaskGantt detailGantt = new BuRepairPlanYearDetailVOWithTaskGantt();
        BeanUtils.copyProperties(detail, detailGantt);

        detailGantt.setDetailId(detail.getId());
        detailGantt.setUID(detail.getId());
        detailGantt.setID(detail.getTrainIndex());
        detailGantt.setName("车辆" + detail.getTrainIndex());
        detailGantt.setStart(detail.getStartDate());
        detailGantt.setFinish(detail.getFinishDate());
        detailGantt.setDuration(getDuration(detail.getStartDate(), detail.getFinishDate()));

        return detailGantt;
    }

    private static int getDuration(Date startDate, Date finishDate) {
        if (null == startDate || null == finishDate) {
            return 0;
        }

        long startDateTime = startDate.getTime();
        long finishDateTime = finishDate.getTime();

        long day = (finishDateTime - startDateTime) / (1000 * 60 * 60 * 24);
        return new Long(day).intValue() + 1;
    }

    private BuRepairPlanYear transformFromGantt(BuRepairPlanYearVOWithGantt yearVOWithGantt) {
        if (null == yearVOWithGantt) {
            return null;
        }

        BuRepairPlanYear planYear = new BuRepairPlanYear();
        BeanUtils.copyProperties(yearVOWithGantt, planYear);
        if (StringUtils.isBlank(planYear.getId())) {
            planYear.setId(yearVOWithGantt.getUID());
        }
        if (StringUtils.isBlank(planYear.getTitle())) {
            planYear.setTitle(yearVOWithGantt.getName());
        }

        // 年计划明细转换
        List<BuRepairPlanYearDetailVOWithTaskGantt> detailVOWithTaskGanttList = yearVOWithGantt.getTasks();
        List<BuRepairPlanYearDetail> detailList = transformFromGantt(detailVOWithTaskGanttList);
        planYear.setDetailList(detailList);

        return planYear;
    }

    private List<BuRepairPlanYearDetail> transformFromGantt(List<BuRepairPlanYearDetailVOWithTaskGantt> ganttList) {
        if (CollectionUtils.isEmpty(ganttList)) {
            return new ArrayList<>();
        }

        List<BuRepairPlanYearDetail> detailList = new ArrayList<>();
        for (BuRepairPlanYearDetailVOWithTaskGantt detailGantt : ganttList) {
            detailList.add(transformFromGantt(detailGantt));
        }

        return detailList;
    }

    private BuRepairPlanYearDetail transformFromGantt(BuRepairPlanYearDetailVOWithTaskGantt detailGantt) {
        if (null == detailGantt) {
            return null;
        }

        BuRepairPlanYearDetail detail = new BuRepairPlanYearDetail();
        BeanUtils.copyProperties(detailGantt, detail);
        // 甘特图字段转业务表字段
        if (StringUtils.isBlank(detail.getId())) {
            detail.setId(detailGantt.getUID());
        }
        if (null == detail.getTrainIndex()) {
            detail.setTrainIndex(detailGantt.getID());
        }
        if (null == detail.getStartDate()) {
            detail.setStartDate(detailGantt.getStart());
        }
        if (null == detail.getFinishDate()) {
            detail.setFinishDate(detailGantt.getFinish());
        }

        return detail;
    }

    private void computeAmount(BuRepairPlanYear planYear) {
        if (null == planYear) {
            return;
        }

        int middleAmount = 0;
        int hightAmount = 0;
        List<BuRepairPlanYearDetail> detailList = planYear.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            // 查询修程信息
            Map<String, BuRepairProgram> idProgramMap = getIdRepairProgramMap();

            int middleAmountCount = 0;
            int hightAmountCount = 0;
            for (BuRepairPlanYearDetail detail : detailList) {
                BuRepairProgram program = idProgramMap.get(detail.getProgramId());
                Integer proType = program.getProType();

                if (1 == proType) {
                    middleAmountCount = middleAmountCount + detail.getAmount();
                }
                if (2 == proType) {
                    hightAmountCount = hightAmountCount + detail.getAmount();
                }
            }

            middleAmount = new Long(middleAmountCount).intValue();
            hightAmount = new Long(hightAmountCount).intValue();
        }

        planYear.setMiddleAmount(middleAmount).setHightAmount(hightAmount);
    }

    private Map<String, BuRepairProgram> getIdRepairProgramMap() {
        List<BuRepairProgram> programList = buRepairProgramMapper.selectList(Wrappers.emptyWrapper());
        Map<String, BuRepairProgram> idProgramMap = new HashMap<>();
        programList.forEach(program -> idProgramMap.put(program.getId(), program));

        return idProgramMap;
    }

    private void setPlanYearFinishAndProgress(List<BuRepairPlanYear> planYearList, Map<String, BuRepairProgram> idProgramMap) {
        for (BuRepairPlanYear planYear : planYearList) {
            int middleRepair = null == planYear.getMiddleAmount() ? 0 : planYear.getMiddleAmount();
            int hightRepair = null == planYear.getHightAmount() ? 0 : planYear.getHightAmount();
            int middleRepairFinish = 0;
            int hightRepairFinish = 0;

            // 年计划完成数量暂时直接从列计划中查询
//                // 查询对应接车的计划完成时间在年份内，已审批的交车记录
//                List<BuRepairExchang> finishDeliverExchangeList = buRepairExchangMapper.selectApprovedDeliverListByYear(planYear.getYear());
//                for (BuRepairExchang finishDeliverExchange : finishDeliverExchangeList) {
//                    BuRepairProgram program = idProgramMap.get(finishDeliverExchange.getProgramId());
//                    Integer proType = program.getProType();
//
//                    if (1 == proType) {
//                        middleRepairFinish++;
//                    }
//                    if (2 == proType) {
//                        hightRepairFinish++;
//                    }
//                }
            List<BuRepairPlan> planList = buRepairPlanMapper.selectFinishedListByYear(planYear.getYear());
            for (BuRepairPlan plan : planList) {
                BuRepairProgram program = idProgramMap.get(plan.getRepairProgramId());
                Integer proType = program.getProType();

                if (1 == proType) {
                    middleRepairFinish++;
                }
                if (2 == proType) {
                    hightRepairFinish++;
                }
            }

            planYear.setMiddleAmount(middleRepair);
            planYear.setMiddleRepairFinish(middleRepairFinish);
            planYear.setHightAmount(hightRepair);
            planYear.setHightRepairFinish(hightRepairFinish);
            if (middleRepair + hightRepair != 0) {
                Double percent = PercentUtils.percent((middleRepairFinish + hightRepairFinish), (middleRepair + hightRepair));
                int percentInt = new BigDecimal(percent).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                planYear.setFinishPercent(percentInt);
            }
        }
    }

    private HSSFWorkbook getExcel(List<BuRepairPlanYear> repairPlanYearList, String title) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        //excel头部信息
        String[] excelHeader = {
                "序号",
                "设备名称",
                "单位",
                "数量",
                "检修修程   ",
                "开始时间",
                "完成时间",
                "备注"
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFRow row = null;
        HSSFRow row2 = null;
        HSSFRow row3 = null;
        HSSFRow row4 = null;
        HSSFCell cell = null;
        HSSFSheet sheet = workbook.createSheet(nowTimeString);
        HSSFRow sheetRow = sheet.createRow(3);

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

        //----------------二级标题格样式----------------------------------
        HSSFCellStyle titleStyle2 = workbook.createCellStyle();        //表格样式
        titleStyle2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        titleStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font ztFont2 = workbook.createFont();
        ztFont2.setItalic(false);                     // 设置字体为斜体字
        ztFont2.setColor(Font.COLOR_NORMAL);            // 将字体设置为“红色”
        ztFont2.setFontHeightInPoints((short) 10);    // 将字体大小设置为18px
        ztFont2.setFontName("宋体");             // 字体应用到当前单元格上
//        ztFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    //加粗
//                         ztFont.setUnderline(Font.U_SINGLE);         // 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
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
//        cellStyle.setWrapText(true);//设置自动换行


        // 在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        row = sheet.createRow(0);
        // 创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        cell = row.createCell(0);
        // 合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 7));
        // 设置单元格内容
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);


        // ------------------创建第二行(单位、填表日期)---------------------
        row = sheet.createRow(2); // 创建第二行
        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));
//
        // 设置单元格内容
        cell.setCellValue("                  中心               专业");
        cell.setCellStyle(titleStyle2);

//        cell = row.createCell(3);
//        cell.setCellValue("专业");
//        cell.setCellStyle(titleStyle2);


        row2 = sheet.createRow(repairPlanYearList.size() + 10); // 创建第二行
        row3 = sheet.createRow(repairPlanYearList.size() + 11); // 创建第二行
        row4 = sheet.createRow(repairPlanYearList.size() + 9); // 创建第二行
        cell = row4.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(repairPlanYearList.size() + 9, repairPlanYearList.size() + 9, 0, 9));
        cell.setCellValue("编制         审核         车间主任（签字）         中心主任（签字）       填报日期（签字）");
        cell.setCellStyle(titleStyle2);


        cell = row2.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(repairPlanYearList.size() + 10, repairPlanYearList.size() + 10, 0, 9));
        // 设置单元格内容
        cell.setCellValue("填写说明: 1、“设备名称”一栏，各中心、车间可根据自身专业特点，选填计划检修的设备名称或计划检修项目；");
        cell.setCellStyle(titleStyle2);

        cell = row3.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(repairPlanYearList.size() + 11, repairPlanYearList.size() + 11, 0, 9));
        // 设置单元格内容
        cell.setCellValue("         2、“检修修程”、“年检修量”栏，各中心、车间可根据自身专业特点，自定义检修周期的分类。");
        cell.setCellStyle(titleStyle2);


        // 设置excel
        for (String header : excelHeaderList) {
            HSSFCell cell1 = sheetRow.createCell(excelHeaderList.indexOf(header));
            cell1.setCellValue(header);
            cell1.setCellStyle(cellStyle);
            sheet.autoSizeColumn(excelHeaderList.indexOf(header));
        }

        for (int i = 0; i < repairPlanYearList.size(); i++) {
            if (null != repairPlanYearList.get(i)) {
                List<Object> data = new ArrayList<>();
                BuRepairPlanYear planYearVO = repairPlanYearList.get(i);
                data.add(i + 1);
                data.add(planYearVO.getLineName() + "第" + planYearVO.getTrainIndex() + "列车");
                data.add("列");
                data.add(planYearVO.getAmount());
                data.add(planYearVO.getProgramName());
                data.add(dateFormat.format(planYearVO.getStartDate()));
                data.add(dateFormat.format(planYearVO.getFinishDate()));
                data.add((int) ((planYearVO.getFinishDate().getTime() - planYearVO.getStartDate().getTime()) / (1000 * 3600 * 24)) + "个工作日");

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
        if (repairPlanYearList.size() < 5) {
            for (int i = repairPlanYearList.size(); i < 5; i++) {
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
        ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        sheet.setColumnWidth(0, 2000);
//        sheet.autoSizeColumn(1, true);
        return workbook;
    }


}
