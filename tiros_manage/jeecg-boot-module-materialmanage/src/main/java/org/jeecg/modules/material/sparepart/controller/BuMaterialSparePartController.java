package org.jeecg.modules.material.sparepart.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.material.sparepart.bean.vo.BuMaterialSparePartQueryVO;
import org.jeecg.modules.material.sparepart.service.BuMaterialSparePartService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 列管备件 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-11
 */
@Api(tags = "列管备件管理")
@Slf4j
@RestController
@RequestMapping("/material/spare-part")
public class BuMaterialSparePartController {

    private final BuMaterialSparePartService buMaterialSparePartService;

    public BuMaterialSparePartController(BuMaterialSparePartService buMaterialSparePartService) {
        this.buMaterialSparePartService = buMaterialSparePartService;
    }


    @GetMapping("/page")
    @ApiOperation(value = "查询列管备件记录（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialSparePart>> page(@Validated BuMaterialSparePartQueryVO queryVO,
                                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialSparePart> page = buMaterialSparePartService.page(queryVO, pageNo, pageSize);
        return new Result<IPage<BuMaterialSparePart>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "查看列管备件详情")
    @OperationLog()
    public Result<BuMaterialSparePart> get(@RequestParam @ApiParam(value = "列管备件id", required = true) String id) throws Exception {
        BuMaterialSparePart buMaterialSparePart = buMaterialSparePartService.getById(id);
        return new Result<BuMaterialSparePart>().successResult(buMaterialSparePart);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增列管备件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuMaterialSparePart buMaterialSparePart) {
        boolean flag = buMaterialSparePartService.save(buMaterialSparePart);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "修改列管备件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> updateById(@RequestBody @Validated BuMaterialSparePart buMaterialSparePart) {
        boolean flag = buMaterialSparePartService.updateById(buMaterialSparePart);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除列管备件（批量）")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "列管备件ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buMaterialSparePartService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

