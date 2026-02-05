package org.jeecg.modules.basemanage.org.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.org.bean.BuUser;
import org.jeecg.modules.basemanage.org.bean.vo.BuUserQueryVO;
import org.jeecg.modules.basemanage.org.service.BuUserService;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 组织人员信息 前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-03
 */
@Api(tags = "组织人员信息")
@Slf4j
@RestController
@RequestMapping("/org")
public class BuOrgUserController {

    private final BuUserService buUserService;

    public BuOrgUserController(BuUserService buUserService) {
        this.buUserService = buUserService;
    }


    @GetMapping("/tree")
    @ApiOperation("树结构-查询")
    @OperationLog()
    public Result<List<CompanyTree>> selectTreeForOrgUser() throws Exception {
        List<CompanyTree> companyTreeList = buUserService.selectTreeForOrgUser();
        return new Result<List<CompanyTree>>().successResult(companyTreeList);
    }

    @GetMapping("/user/page")
    @ApiOperation(value = "人员-查询(分页)")
    @OperationLog()
    public Result<IPage<BuUser>> pageUser(@Validated BuUserQueryVO queryVO,
                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        IPage<BuUser> page = buUserService.pageUser(queryVO, pageNo, pageSize);
        return new Result<IPage<BuUser>>().successResult(page);
    }

    @GetMapping("/user/get")
    @ApiOperation(value = "人员-根据id查询")
    @OperationLog()
    public Result<BuUser> getUser(@RequestParam @ApiParam(value = "人员id", required = true) String id) throws Exception {
        BuUser user = buUserService.getUser(id);
        return new Result<BuUser>().successResult(user);
    }

    @PostMapping("/user/save")
    @ApiOperation(value = "人员-保存关联信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_7)
    public Result<Boolean> saveUser(@RequestBody @Validated BuUser user) throws Exception {
        Boolean flag = buUserService.saveUser(user);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("/user/delete")
    @ApiOperation(value = "删除人员信息")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Object> deleteUser(@RequestParam @ApiParam(value = "人员id,多个逗号分隔", required = true) String ids) {
        return (Result)Result.ok(buUserService.deleteUser(ids));
    }

    @GetMapping("/user/skill-compare")
    @ApiOperation(value = "人员技能项对比")
    @OperationLog()
    public Result<List<LinkedHashMap<String, Object>>> compareUserSkill(@RequestParam @ApiParam(value = "人员id,多个逗号分隔", required = true) String ids) throws Exception {
        List<LinkedHashMap<String, Object>> skillCompareList = buUserService.compareUserSkill(ids);
        return new Result<List<LinkedHashMap<String, Object>>>().successResult(skillCompareList);
    }

}

