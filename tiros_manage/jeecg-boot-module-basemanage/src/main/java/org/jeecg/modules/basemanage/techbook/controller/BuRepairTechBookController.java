package org.jeecg.modules.basemanage.techbook.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBook;
import org.jeecg.modules.basemanage.techbook.bean.vo.BuRepairTechBookQueryVO;
import org.jeecg.modules.basemanage.techbook.service.BuRepairTechBookService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 作业指导书(工艺) 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Api(tags = "作业指导书")
@Slf4j
@RestController
@RequestMapping("/base/tech-book")
public class BuRepairTechBookController {

    private final BuRepairTechBookService buRepairTechBookService;

    public BuRepairTechBookController(BuRepairTechBookService buRepairTechBookService) {
        this.buRepairTechBookService = buRepairTechBookService;
    }

    @GetMapping("/page")
    @ApiOperation(value = "指导书-查询(分页)")
    @OperationLog()
    public Result<IPage<BuRepairTechBook>> pageTechBook(@Validated BuRepairTechBookQueryVO queryVO,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuRepairTechBook> page = buRepairTechBookService.pageTechBook(queryVO, pageNo, pageSize);
        return new Result<IPage<BuRepairTechBook>>().successResult(page);
    }

    @GetMapping("/get")
    @ApiOperation(value = "指导书-根据id查询")
    @OperationLog()
    public Result<BuRepairTechBook> getTechBook(@RequestParam @ApiParam(value = "作业指导书id", required = true) String id) throws Exception {
        BuRepairTechBook techBook = buRepairTechBookService.getTechBookById(id);
        return new Result<BuRepairTechBook>().successResult(techBook);
    }

    @PostMapping("/save")
    @ApiOperation(value = "指导书-新增/修改", notes = "作业指导书基本信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<String> save(@RequestBody @Validated BuRepairTechBook BuRepairTechBook) throws Exception {
        String id = buRepairTechBookService.saveTechBook(BuRepairTechBook);
        return new Result<String>().successResult(id);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "指导书-删除(批量)", notes = "包括明细、物料及工器具")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "作业指导书ids，多个逗号分隔", required = true) String ids) throws Exception {
        boolean flag = buRepairTechBookService.deleteBatch(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

