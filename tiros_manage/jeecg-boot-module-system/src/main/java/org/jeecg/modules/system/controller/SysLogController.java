package org.jeecg.modules.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecg.modules.system.vo.LoginStatisticVO;
import org.jeecg.modules.system.vo.query.LoginStatisticQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 系统日志表 前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@RestController
@RequestMapping("/sys/log")
@Slf4j
public class SysLogController {

    @Autowired
    private ISysLogService sysLogService;

    /**
     * @param syslog
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     * @功能：查询日志记录
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysLog>> queryPageList(SysLog syslog, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysLog>> result = new Result<IPage<SysLog>>();
        QueryWrapper<SysLog> queryWrapper = QueryGenerator.initQueryWrapper(syslog, req.getParameterMap());
        Page<SysLog> page = new Page<SysLog>(pageNo, pageSize);
        //日志关键词
        String keyWord = req.getParameter("keyWord");
        if (oConvertUtils.isNotEmpty(keyWord)) {
            queryWrapper.like("log_content", keyWord);
        }
        //TODO 过滤逻辑处理
        //TODO begin、end逻辑处理
        //TODO 一个强大的功能，前端传一个字段字符串，后台只返回这些字符串对应的字段
        //创建时间/创建人的赋值
        IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
        log.info("查询当前页：" + pageList.getCurrent());
        log.info("查询当前页数量：" + pageList.getSize());
        log.info("查询结果数量：" + pageList.getRecords().size());
        log.info("数据总数：" + pageList.getTotal());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @param id
     * @return
     * @功能：删除单个日志记录
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysLog> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysLog> result = new Result<SysLog>();
        SysLog sysLog = sysLogService.getById(id);
        if (sysLog == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysLogService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }
        return result;
    }

    /**
     * @param ids
     * @return
     * @功能：批量，全部清空日志记录
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<SysRole> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysRole> result = new Result<SysRole>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            if ("allclear".equals(ids)) {
                this.sysLogService.removeAll();
                result.success("清除成功!");
            }
            this.sysLogService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    @GetMapping("/loginStatistic")
    @ApiOperation(value = "查询用户系统访问统计")
    @OperationLog()
    public Result<List<LoginStatisticVO>> listLoginStatistic(@Validated LoginStatisticQueryVO queryVO) throws Exception {
        List<LoginStatisticVO> statisticVOList = sysLogService.listLoginStatistic(queryVO);
        return new Result<List<LoginStatisticVO>>().successResult(statisticVOList);
    }

    @GetMapping("/loginStatistic/export")
    @ApiOperation("导出系统访问统计")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_6)
    public Result<Boolean> export(@Validated LoginStatisticQueryVO queryVO, HttpServletResponse httpServletResponse) {
        try {
            HSSFWorkbook workbook = sysLogService.exportLoginStatisticExcel(queryVO);
            String fileName = "架大修系统访问统计";
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1")));
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
