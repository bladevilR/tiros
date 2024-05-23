package org.jeecg.modules.tiros.config.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.TirosController;
import org.jeecg.common.tiros.config.bean.SysConfig;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统配置 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-11
 */
@TirosController
@Api(tags = "系统配置")
@Slf4j
@RestController
@RequestMapping("/sys/config")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    public SysConfigController(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询(分页)")
    public Result<IPage<SysConfig>> pageConfig(@RequestParam(required = false) @ApiParam(value = "编码或名称") String searchText,
                                               @RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<SysConfig> page = sysConfigService.pageConfig(searchText, pageNo, pageSize);
        return new Result<IPage<SysConfig>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据编码code查询")
    public Result<SysConfig> getByCode(@RequestParam @ApiParam(value = "编码", required = true) String code) throws Exception {
        SysConfig config = sysConfigService.getById(code);
        return new Result<SysConfig>().successResult(config);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存(新增或修改)")
    public Result<Boolean> saveConfig(@RequestBody @Validated SysConfig SysConfig) throws Exception {
        boolean flag = sysConfigService.saveOrUpdate(SysConfig);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除(批量)")
    public Result<Boolean> deleteTrainingBatch(@RequestParam @ApiParam(value = "编码，多个逗号分隔", required = true) String codes) throws Exception {
        boolean flag = sysConfigService.deleteBatch(codes);
        return new Result<Boolean>().successResult(flag);
    }

}

