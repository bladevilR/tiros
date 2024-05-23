package org.jeecg.modules.material.tools.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.tiros.constant.TirosConstant;
import org.jeecg.common.tiros.rpt.service.AlertRecordService;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.ExcelUtil;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsQueryVO;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsSettingVO;
import org.jeecg.modules.material.tools.mapper.BuMaterialToolsMapper;
import org.jeecg.modules.material.tools.service.BuMaterialToolsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.jeecg.common.tiros.constant.TirosConstant.TOOLS_CHECK_EARLIER_WARNING_TIME;

/**
 * <p>
 * 工具信息，包括工器具、工装等信息，一种物资类型可能存在多条记录 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Slf4j
@Service
public class BuMaterialToolsServiceImpl extends ServiceImpl<BuMaterialToolsMapper, BuMaterialTools> implements BuMaterialToolsService {

    @Resource
    private BuMaterialToolsMapper buMaterialToolsMapper;
    @Resource
    private AlertRecordService alertRecordService;
    @Resource
    private SysConfigService sysConfigService;


    /**
     * @see BuMaterialToolsService#page(BuMaterialToolsQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialTools> page(BuMaterialToolsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buMaterialToolsMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
    }

    /**
     * @see BuMaterialToolsService#getToolsById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuMaterialTools getToolsById(String id) throws Exception {
        return buMaterialToolsMapper.selectToolById(id);
    }

    /**
     * @see BuMaterialToolsService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        buMaterialToolsMapper.deleteBatchIds(idList);

        return true;
    }

    /**
     * @see BuMaterialToolsService#setNextCheckTime(String, Date)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setNextCheckTime(String ids, Date nextCheckTime) throws Exception {
        Date now = new Date();
        if (StringUtils.isBlank(ids)) {
            throw new JeecgBootException("至少选择一个工器具");
        }
        if (nextCheckTime.before(DateUtils.transToDayEnd(now))) {
            throw new JeecgBootException("下次送检日期需晚于当前日期");
        }

        List<String> idList = Arrays.asList(ids.split(","));
        List<BuMaterialTools> toolList = new ArrayList<>();
        for (String id : idList) {
            BuMaterialTools tool = buMaterialToolsMapper.selectById(id);
            if (null != tool) {
                // 设置旧的下次送检时间为上次送检时间
                tool.setLastCheckTime(tool.getNextCheckTime())
                        .setNextCheckTime(nextCheckTime);
                toolList.add(tool);
            } else {
                throw new JeecgBootException("所选工器具不存在（id=" + id + "）");
            }
        }
        if (CollectionUtils.isNotEmpty(toolList)) {
            this.updateBatchById(toolList);
        }

        // 设置工器具送检预警为已处理
        alertRecordService.setAlertRecordHandled(3, idList);

        return true;
    }

    /**
     * @see BuMaterialToolsService#listNeedCheck(String, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialTools> listNeedCheck(String searchText, Integer pageNo, Integer pageSize) throws Exception {
        // LambdaQueryWrapper<BuMaterialTools> wrapper = getNeedCheckWrapper(searchText);
        Date date;
        String configDay = sysConfigService.getConfigValueByCode(TirosConstant.TOOLS_CHECK_EARLIER_WARNING_CODE);
        if (StringUtils.isNotBlank(configDay)) {
            date = new Date(System.currentTimeMillis() + (Long.parseLong(configDay) * 24 * 60 * 60 * 1000));
        } else {
            date = new Date(System.currentTimeMillis() + TOOLS_CHECK_EARLIER_WARNING_TIME);
        }
        BuMaterialToolsQueryVO queryVO = new BuMaterialToolsQueryVO().setNextCheckTime(date).setSearchText(searchText);
        return buMaterialToolsMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        // return buMaterialToolsMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    /**
     * @see BuMaterialToolsService#countNeedCheck(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Integer countNeedCheck(String searchText) throws Exception {
        LambdaQueryWrapper<BuMaterialTools> wrapper = getNeedCheckWrapper(searchText);
        return buMaterialToolsMapper.selectCount(wrapper);
    }

    /**
     * @see BuMaterialToolsService#flushToolGroupByUser()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean flushToolGroupByUser() throws Exception {
        List<Map<String, Object>> toolUserGroupList = buMaterialToolsMapper.selectToolUserGroupList();
        if (CollectionUtils.isEmpty(toolUserGroupList)) {
            return true;
        }

        List<BuMaterialTools> updateGroupList = new ArrayList<>();
        List<BuMaterialTools> updateWorkshopList = new ArrayList<>();
        for (Map<String, Object> toolUserGroup : toolUserGroupList) {
            String departId = (String) toolUserGroup.get("departId");
            if (StringUtils.isBlank(departId)) {
                continue;
            }

            String toolId = (String) toolUserGroup.get("toolId");
            String departOrgCategory = (String) toolUserGroup.get("departOrgCategory");


            if ("4".equals(departOrgCategory)) {
                // 班组
                BuMaterialTools tool = new BuMaterialTools()
                        .setId(toolId)
                        .setGroupId(departId);
                updateGroupList.add(tool);
            } else {
                BuMaterialTools tool = new BuMaterialTools()
                        .setId(toolId)
                        .setWorkshopId(departId);
                updateWorkshopList.add(tool);
            }
        }

        if (CollectionUtils.isNotEmpty(updateGroupList)) {
            buMaterialToolsMapper.updateListForGroup(updateGroupList);
        }
        if (CollectionUtils.isNotEmpty(updateWorkshopList)) {
            buMaterialToolsMapper.updateListForWorkshop(updateWorkshopList);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setLastCheckTime(String ids, Date lastSelfCheckTime) throws Exception {
        Date now = new Date();
        if (DateUtils.transToDayEnd(now).before(lastSelfCheckTime)) {
            throw new JeecgBootException("上次自检日期需早于当前日期");
        }
        List<String> idsList = Arrays.asList(ids.split(","));
        List<BuMaterialTools> toolsList = idsList.stream()
                .map((id) -> new BuMaterialTools().setId(id).setLastSelfCheckTime(lastSelfCheckTime))
                .collect(Collectors.toList());
        return this.updateBatchById(toolsList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setSelfCheck(String ids, Integer selfCheck) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<BuMaterialTools> toolsList = idList.stream()
                .map((id) -> new BuMaterialTools().setId(id).setIsSelfCheck(selfCheck))
                .collect(Collectors.toList());
        return this.updateBatchById(toolsList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setResponsible(String ids, String userId) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<BuMaterialTools> toolsList = idList.stream()
                .map((id) -> new BuMaterialTools().setId(id).setUserId(userId))
                .collect(Collectors.toList());
        return this.updateBatchById(toolsList);
    }

    @Override
    public HSSFWorkbook exportTools(BuMaterialToolsQueryVO queryVO) {
        IPage<BuMaterialTools> page = buMaterialToolsMapper.selectPageByCondition(new Page<>(-1, -1), queryVO);
        return getExcel(page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setElectric(BuMaterialToolsSettingVO toolsSettingVO) {
        List<String> idList = Arrays.asList(toolsSettingVO.getIds().split(","));
        List<BuMaterialTools> toolsList = idList.stream()
                .map((id) -> {
                    BuMaterialTools tools = new BuMaterialTools().setId(id).setIsElectric(toolsSettingVO.getIsElectric());
                    if (toolsSettingVO.getUpdateTime()) {
                        tools.setExpirDate(toolsSettingVO.getExpirDate());
                    }
                    return tools;
                }).collect(Collectors.toList());
        return this.updateBatchById(toolsList);
    }

    private HSSFWorkbook getExcel(List<BuMaterialTools> toolsList) {
        String[] excelHeader = {
                "序号",
                "物资编码",
                "资产编号",
                "架大修类别",
                "物资描述",
                "数 量",
                "出厂编号",
                "是否送检",
                "下次送检",
                "是否逾期",
                "逾期天数",
                "保管人姓名",
                "领用班组",
                "入库日期",
                "状 态",
                "是否自检",
                "上次自检",
                "电动工具有效日期",
                "固定资产",
                "库位",
        };
        List<String> excelHeaderList = Arrays.asList(excelHeader);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("工器具");
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
        for (BuMaterialTools tools : toolsList) {
            if (null != tools) {
                int rowNum = toolsList.indexOf(tools) + 1;
                HSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(tools.getCode());
                row.createCell(2).setCellValue(tools.getAssetCode());
                row.createCell(3).setCellValue(getToolType(tools.getToolType()));
                row.createCell(4).setCellValue(tools.getSpec());
                row.createCell(5).setCellValue(tools.getAmount());
                row.createCell(6).setCellValue(tools.getSerialNo());
                row.createCell(7).setCellValue(tools.getNextCheckTime() != null ? "是" : "否");
                row.createCell(8).setCellValue(tools.getNextCheckTime() != null ? DateUtils.date_sdf.get().format(tools.getNextCheckTime()) : "");
                row.createCell(9).setCellValue(tools.getOverdue() == 0 ? "否" : "是");
                row.createCell(10).setCellValue(getIsOverTimeDays(tools.getNextCheckTime()));
                row.createCell(11).setCellValue(tools.getUserName());
                row.createCell(12).setCellValue(tools.getGroupName());
                row.createCell(13).setCellValue(tools.getEntryDate() != null ? DateUtils.date_sdf.get().format(tools.getEntryDate()) : "");
                row.createCell(14).setCellValue(getStatusStr(tools.getStatus()));
                row.createCell(15).setCellValue(tools.getIsSelfCheck() == 0 ? "否" : "是");
                row.createCell(16).setCellValue(tools.getLastCheckTime() != null ? DateUtils.date_sdf.get().format(tools.getLastCheckTime()) : "");
                row.createCell(17).setCellValue(tools.getExpirDate() != null ? DateUtils.date_sdf.get().format(tools.getExpirDate()) : "");
                row.createCell(18).setCellValue(tools.getIsFixedAsset() != null && tools.getIsFixedAsset() == 0 ? "否" : "是");
                row.createCell(19).setCellValue(tools.getStorageLocation());
            }
        }
        // 设置自动列宽
        //ExcelUtil.setSizeColumn(sheet, excelHeaderList.size());
        ExcelUtil.setColumnWidth(sheet, excelHeaderList.size());

        return workbook;
    }

    private String getIsOverTimeDays(Date nextCheckTime) {
        if (nextCheckTime != null) {
            Date now = new Date();
            int diffDay = DateUtil.compare(nextCheckTime, now);
            if (diffDay < 0) {
                return Math.abs(diffDay) + "";
            }
        }
        return "";
    }

    private String getToolType(Integer toolType) {
        String type = "";
        switch (toolType) {
            case 4:
                type = "工器具";
                break;
            case 5:
                type = "工装";
                break;
            case 6:
                type = "计量器具";
                break;
            default:
                break;
        }
        return type;
    }

    private String getStatusStr(Integer status) {
        String statusStr = "";

        /**
         * 使用中
         *封存
         *报废
         *待送修
         *待送检
         *已完成
         *报废审批中
         *已拆卸
         *待使用
         *盘亏审批中
         *已盘亏
         *送修中
         *送检中
         *退库审批中
         *遗失已赔偿
         *已报废
         *停用
         *等待批准
         *已退库
         */
        switch (status) {
            case 1:
                statusStr = "使用中";
                break;
            case 2:
                statusStr = "封存";
                break;
            case 3:
                statusStr = "待送修";
                break;
            case 4:
                statusStr = "待送检";
                break;
            case 5:
                statusStr = "已完成";
                break;

            case 6:
                statusStr = "报废审批中";
                break;

            case 7:
                statusStr = "已拆卸";
                break;
            case 8:
                statusStr = "待使用";
                break;

            case 9:
                statusStr = "盘亏审批中";
                break;

            case 10:
                statusStr = "已盘亏";
                break;

            case 11:
                statusStr = "送修中";
                break;
            case 12:
                statusStr = "送检中";
                break;
            case 13:
                statusStr = "退库审批中";
                break;
            case 14:
                statusStr = "遗失已赔偿";
                break;
            case 15:
                statusStr = "已报废";
                break;
            case 16:
                statusStr = "停用";
                break;
            case 17:
                statusStr = "等待批准";
                break;
            case 18:
                statusStr = "已退库";
                break;
            case 19:
                statusStr = "报废";
                break;
            default:
                break;
        }

        return statusStr;
    }

    /**
     * @see BuMaterialToolsService#save(Object)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(BuMaterialTools buMaterialTools) {
        if (isAssetCodeRepeated(buMaterialTools)) {
            throw new JeecgBootException("资产编码重复");
        }

        return super.save(buMaterialTools);
    }

    /**
     * @see BuMaterialToolsService#updateById(Object)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(BuMaterialTools buMaterialTools) {
        if (isAssetCodeRepeated(buMaterialTools)) {
            throw new JeecgBootException("资产编码重复");
        }

        return super.updateById(buMaterialTools);
    }


    private LambdaQueryWrapper<BuMaterialTools> getNeedCheckWrapper(String searchText) {
        Date date;
        String configDay = sysConfigService.getConfigValueByCode(TirosConstant.TOOLS_CHECK_EARLIER_WARNING_CODE);
        if (StringUtils.isNotBlank(configDay)) {
            date = new Date(System.currentTimeMillis() + (Long.parseLong(configDay) * 24 * 60 * 60 * 1000));
        } else {
            date = new Date(System.currentTimeMillis() + TOOLS_CHECK_EARLIER_WARNING_TIME);
        }
        LambdaQueryWrapper<BuMaterialTools> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(BuMaterialTools::getNextCheckTime, date);
//                .in(BuMaterialTools::getStatus, Arrays.asList(1, 2));
        if (StringUtils.isNotBlank(searchText)) {
            wrapper.and(andWrapper -> andWrapper
                    .like(BuMaterialTools::getCode, searchText)
                    .or()
                    .like(BuMaterialTools::getName, searchText));
        }
        return wrapper;
    }

    private boolean isAssetCodeRepeated(BuMaterialTools buMaterialTools) {
        LambdaQueryWrapper<BuMaterialTools> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuMaterialTools::getAssetCode, buMaterialTools.getAssetCode());
        List<BuMaterialTools> buMaterialToolsList = buMaterialToolsMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(buMaterialToolsList)) {
            return false;
        }
        if (StringUtils.isBlank(buMaterialTools.getId())) {
            return true;
        }
        return !buMaterialTools.getId().equals(buMaterialToolsList.get(0).getId());
    }

}
