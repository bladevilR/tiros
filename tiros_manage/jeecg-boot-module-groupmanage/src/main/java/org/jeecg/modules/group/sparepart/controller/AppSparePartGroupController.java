package org.jeecg.modules.group.sparepart.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.modules.group.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.group.sparepart.bean.vo.BuMaterialSparePartQueryVO;
import org.jeecg.modules.group.sparepart.service.BuMaterialSparePartGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 列管备件 app前端控制器
 * </p>
 *
 * @author lidafeng
 * @since 2021-02-25
 */
@Api(tags = "列管备件管理(工班)")
@AppController
@Slf4j
@RestController
@RequestMapping("/app/group/spare-part")
public class AppSparePartGroupController {

    private final BuMaterialSparePartGroupService buMaterialSparePartGroupService;

    public AppSparePartGroupController(BuMaterialSparePartGroupService buMaterialSparePartGroupService) {
        this.buMaterialSparePartGroupService = buMaterialSparePartGroupService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询列管备件记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialSparePart>> page(@Validated BuMaterialSparePartQueryVO queryVO,
                                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialSparePart> page = buMaterialSparePartGroupService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialSparePart>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查看列管备件详情")
    @OperationLog()
    public Result<BuMaterialSparePart> get(@RequestParam @ApiParam(value = "列管备件id", required = true) String id) throws Exception {
        BuMaterialSparePart buMaterialSparePart = buMaterialSparePartGroupService.getById(id);
        return new Result<BuMaterialSparePart>().successResult(buMaterialSparePart);
    }

}

