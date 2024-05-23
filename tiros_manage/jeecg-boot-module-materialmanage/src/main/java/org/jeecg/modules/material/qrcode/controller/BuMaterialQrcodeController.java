package org.jeecg.modules.material.qrcode.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.qrcode.bean.BuMaterialQrcode;
import org.jeecg.modules.material.qrcode.service.BuMaterialQrcodeService;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 查询时，自动生成该表中不存在的对应数据以及二维码 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
@Api(tags = "标识码管理")
@Slf4j
@RestController
@RequestMapping("/material/qrcode")
public class BuMaterialQrcodeController {

    private final BuMaterialQrcodeService buMaterialQrcodeService;

    @Autowired
    public BuMaterialQrcodeController(BuMaterialQrcodeService buMaterialQrcodeService) {
        this.buMaterialQrcodeService = buMaterialQrcodeService;
    }


    /* @GetMapping("/pageWarehouse")
     @ApiOperation(value = "查询仓库信息及二维码（分页）")
     public Result<IPage<BuMaterialQrcode>> pageWarehouse(@RequestParam(required = false) @ApiParam(value = "车辆段id 字典dictCode=(bu_mtr_depot,name,id)") String depotId,
                                                          @RequestParam(required = false) @ApiParam(value = "车间id 字典dictCode=(bu_mtr_workshop,name,id)") String workshopId,
                                                          @RequestParam(required = false) @ApiParam(value = "仓库名称或编码") String searchText,
                                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
         IPage<BuMaterialQrcode> page = buMaterialQrcodeService.pageWarehouse(depotId, workshopId, searchText, pageNo, pageSize);
         return new Result<IPage<BuMaterialQrcode>>().successResult(page);
     }*/
    @GetMapping("/pageWarehouse")
    @ApiOperation(value = "查询仓库信息及二维码（分页）")
    @OperationLog()
    public Result<IPage<BuMtrWarehouse>> pageWarehouse(@RequestParam(required = false) @ApiParam(value = "车辆段id 字典dictCode=(bu_mtr_depot,name,id)") String depotId,
                                                       @RequestParam(required = false) @ApiParam(value = "车间id 字典dictCode=(bu_mtr_workshop,name,id)") String workshopId,
                                                       @RequestParam(required = false) @ApiParam(value = "仓库名称或编码") String searchText,
                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMtrWarehouse> page = buMaterialQrcodeService.pageWarehouses(depotId, workshopId, searchText, pageNo, pageSize);
        return new Result<IPage<BuMtrWarehouse>>().successResult(page);
    }


//    @GetMapping("/pageMaterial")
//    @ApiOperation(value = "查询物资信息及二维码（分页）")
//    public Result<IPage<BuMaterialQrcode>> pageMaterial(@RequestParam(required = false) @ApiParam(value = "线路id 字典dictCode=(bu_mtr_line,line_name,line_id)") String lineId,
//                                                        @RequestParam(required = false) @ApiParam(value = "系统id 字典dictCode=(bu_train_structure_detail,name,id,struct_type=1)") String systemId,
//                                                        @RequestParam(required = false) @ApiParam(value = "物资名称或编码") String searchText,
//                                                        @RequestParam(defaultValue = "1") Integer pageNo,
//                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
//        IPage<BuMaterialQrcode> page = buMaterialQrcodeService.pageMaterial(lineId, systemId, searchText, pageNo, pageSize);
//        return new Result<IPage<BuMaterialQrcode>>().successResult(page);
//    }

    /* @GetMapping("/pagePallet")
     @ApiOperation(value = "查询托盘信息及二维码（分页）")
     public Result<IPage<BuMaterialQrcode>> pagePallet(@RequestParam(required = false) @ApiParam(value = "线路id 字典dictCode=(bu_mtr_line,line_name,line_id)") String lineId,
                                                       @RequestParam(required = false) @ApiParam(value = "托盘名称或编码") String searchText,
                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
         IPage<BuMaterialQrcode> page = buMaterialQrcodeService.pagePallet(lineId, searchText, pageNo, pageSize);
         return new Result<IPage<BuMaterialQrcode>>().successResult(page);
     }*/
    @GetMapping("/pagePallet")
    @ApiOperation(value = "查询托盘信息及二维码（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialPallet>> pagePallet(@RequestParam(required = false) @ApiParam(value = "线路id 字典dictCode=(bu_mtr_line,line_name,line_id)") String lineId,
                                                      @RequestParam(required = false) @ApiParam(value = "托盘名称或编码") String searchText,
                                                      @RequestParam(defaultValue = "1") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialPallet> page = buMaterialQrcodeService.pagePallets(lineId, searchText, pageNo, pageSize);
        return new Result<IPage<BuMaterialPallet>>().successResult(page);
    }

    @GetMapping("/pageTurnover")
    @ApiOperation(value = "查询列管备件信息及二维码（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialQrcode>> pageTurnover(@RequestParam(required = false) @ApiParam(value = "线路id 字典dictCode=(bu_mtr_line,line_name,line_id)") String lineId,
                                                        @RequestParam(required = false) @ApiParam(value = "系统id 字典dictCode=(bu_train_structure_detail,name,id,struct_type=1)") String systemId,
                                                        @RequestParam(required = false) @ApiParam(value = "列管备件名称或物资编码或资产编码") String searchText,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialQrcode> page = buMaterialQrcodeService.pageSparePart(lineId, systemId, searchText, pageNo, pageSize);
        return new Result<IPage<BuMaterialQrcode>>().successResult(page);
    }

    @GetMapping("/pageTools")
    @ApiOperation(value = "查询工器具信息及二维码（分页）")
    @OperationLog()
    public Result<IPage<BuMaterialQrcode>> pageTools(@RequestParam(required = false) @ApiParam(value = "线路id 字典dictCode=(bu_mtr_line,line_name,line_id)") String lineId,
                                                     @RequestParam(required = false) @ApiParam(value = "班组id 字典dictCode=(bu_mtr_workshop_group,group_name,id)") String groupId,
                                                     @RequestParam(required = false) @ApiParam(value = "工器具名称或物资编码或资产编码或序列号") String searchText,
                                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMaterialQrcode> page = buMaterialQrcodeService.pageTools(lineId, groupId, searchText, pageNo, pageSize);
        return new Result<IPage<BuMaterialQrcode>>().successResult(page);
    }

    @PostMapping("/confirmPrinted")
    @ApiOperation(value = "更改打印状态为已打印")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> confirmPrinted(@RequestParam @ApiParam(value = "标识码ids，多个逗号分隔", required = true) String ids) throws Exception {
        Boolean flag = buMaterialQrcodeService.confirmPrinted(ids);
        return new Result<Boolean>().successResult(flag);
    }

}

