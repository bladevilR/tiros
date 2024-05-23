package org.jeecg.modules.group.information.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.group.information.bean.BuGroupWorkstation;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.group.information.bean.BuMtrWorkstation;
import org.jeecg.modules.group.information.bean.vo.BuGroupToolsEditVO;
import org.jeecg.modules.group.information.service.BuGroupWorkstationService;
import org.jeecg.modules.group.information.service.BuMaterialToolsGroupService;
import org.jeecg.modules.group.information.service.BuMtrWorkshopGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 综合信息管理 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
@Api(tags = "综合信息管理")
@Slf4j
@RestController
@RequestMapping("/group/information")
public class BuGroupInformationController {

    private final BuMtrWorkshopGroupService buMtrWorkshopGroupService;
    private final BuMaterialToolsGroupService buMaterialToolsGroupService;
    private final BuGroupWorkstationService buGroupWorkstationService;


    public BuGroupInformationController(BuMtrWorkshopGroupService buMtrWorkshopGroupService,
                                        BuMaterialToolsGroupService buMaterialToolsGroupService,
                                        BuGroupWorkstationService buGroupWorkstationService
    ) {
        this.buMtrWorkshopGroupService = buMtrWorkshopGroupService;
        this.buMaterialToolsGroupService = buMaterialToolsGroupService;
        this.buGroupWorkstationService = buGroupWorkstationService;
    }


    @GetMapping("/basic")
    @ApiOperation(value = "基本信息")
    @OperationLog()
    public Result<BuMtrWorkshopGroup> getBasicInfo(@RequestParam @ApiParam(value = "班组id", required = true) String groupId) throws Exception {
        BuMtrWorkshopGroup group = buMtrWorkshopGroupService.getGroupById(groupId);
        return new Result<BuMtrWorkshopGroup>().successResult(group);
    }

    @PostMapping("/tools/add")
    @ApiOperation(value = "添加班组关联工装/工具")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> addToolsToGroup(@RequestBody @Validated BuGroupToolsEditVO groupToolsEditVO) throws Exception {
        boolean flag = buMaterialToolsGroupService.addToolsToGroup(groupToolsEditVO.getToolsId(), groupToolsEditVO.getGroupId());
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/tools/delete")
    @ApiOperation(value = "解除班组关联工装/工具")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> deleteToolsFromGroup(@RequestParam @ApiParam(value = "工器具id") String toolsId, @RequestParam @ApiParam(value = "工班id") String groupId) throws Exception {
        boolean flag = buMaterialToolsGroupService.deleteToolsFromGroup(toolsId, groupId);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/workstation/page-related")
    @ApiOperation(value = "查询班组已关联工位（分页）")
    @OperationLog()
    public Result<IPage<BuMtrWorkstation>> pageWorkstationByGroupId(@RequestParam @ApiParam(value = "班组id", required = true) String groupId,
                                                                    @RequestParam @ApiParam(value = "班组名称", required = false) String name,
                                                                    @RequestParam @ApiParam(value = "位置", required = false) String position,
                                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMtrWorkstation> page = buGroupWorkstationService.pageWorkstationByGroupId(groupId, name, position, pageNo, pageSize);
        return new Result<IPage<BuMtrWorkstation>>().successResult(page);
    }

    @GetMapping("/workstation/page-unrelated")
    @ApiOperation(value = "查询班组未关联工位（分页）")
    @OperationLog()
    public Result<IPage<BuMtrWorkstation>> pageUnrelatedWorkstationByGroupId(@RequestParam @ApiParam(value = "班组id", required = true) String groupId,
                                                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuMtrWorkstation> page = buGroupWorkstationService.pageUnrelatedWorkstationByGroupId(groupId, pageNo, pageSize);
        return new Result<IPage<BuMtrWorkstation>>().successResult(page);
    }

    @PostMapping("/workstation/add")
    @ApiOperation(value = "添加班组关联工位")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> addWorkstationToGroup(@RequestBody @Validated BuGroupWorkstation buGroupWorkstation) throws Exception {
        boolean flag = buGroupWorkstationService.addWorkstationToGroup(buGroupWorkstation);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/workstation/delete")
    @ApiOperation(value = "解除班组关联工位")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> deleteWorkstationFromGroup(@RequestBody @Validated BuGroupWorkstation buGroupWorkstation) throws Exception {
        boolean flag = buGroupWorkstationService.deleteWorkstationFromGroup(buGroupWorkstation);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("/workShopGroup/save")
    @ApiOperation(value = "设置班长副班长")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> saveWorkGroupUser(@RequestParam @ApiParam(value = "班组id", required = true) String groupId,
                                             @RequestParam @ApiParam(value = "人员id", required = true) String userId,
                                             @RequestParam @ApiParam(value = "类型", required = true) String type) throws Exception {
        boolean flag = buMtrWorkshopGroupService.saveWorkGroupUser(groupId, userId, type);
        return new Result<Boolean>().successResult(flag);
    }

}

