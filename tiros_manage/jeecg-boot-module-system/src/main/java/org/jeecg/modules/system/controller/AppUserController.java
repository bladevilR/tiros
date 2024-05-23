package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseWorkstation;
import org.jeecg.modules.basemanage.workstation.entity.vo.BuBaseWorkstationQueryVO;
import org.jeecg.modules.basemanage.workstation.service.IBuBaseWorkstationService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@AppController
@Api(tags = "用户-公共")
@Slf4j
@RestController
@RequestMapping("/app/user")
public class AppUserController {

    private final ISysUserService sysUserService;
    private final ISysDepartService sysDepartService;
    private final IBuBaseWorkstationService workstationService;

    @Resource
    public RedisTemplate<String, String> redisTemplate;

    public AppUserController(ISysUserService sysUserService,
                             ISysDepartService sysDepartService,
                             IBuBaseWorkstationService workstationService) {
        this.sysUserService = sysUserService;
        this.sysDepartService = sysDepartService;
        this.workstationService = workstationService;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public Result<?> changePassword(@RequestBody SysUser sysUser) {
        /*LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, sysUser.getUsername());*/
        // change by jak, 修改密码只能修改当前用户的
        LoginUser curUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, curUser.getUsername());
        SysUser user = this.sysUserService.getOne(wrapper);

        if (user == null) {
            return Result.error("用户不存在！");
        }
        sysUser.setUsername(user.getUsername());
        sysUser.setId(user.getId());

        return sysUserService.changePassword(sysUser);
    }

    //APP-我的二维码生成
    @RequestMapping(value = "/getMyQRCode", method = RequestMethod.GET)
    @ApiOperation(value = "我的二维码生成")
    public Result<String> getMyQRCode() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return new Result<String>().successResult(sysUserService.getMyQRCode(sysUser.getId(), sysUser.getUsername()));
    }

    //APP-我的二维码生成
    @RequestMapping(value = "/getMyCodeValid", method = RequestMethod.GET)
    @ApiOperation(value = "我的二维码是否有效")
    public Result<Boolean> getMyCodeValid(@RequestParam @ApiParam(value = "任务二维码UUID", required = true) String uuid) {
        String value = redisTemplate.opsForValue().get(CommonConstant.PREFIX_MY_QRCODE + uuid);
        return new Result<Boolean>().successResult(StringUtils.isNotBlank(value));
    }

    @GetMapping("/getUserInfoByToken")
    @ApiOperation(value = "根据token获取用户信息")
    public Result<SysUser> getUserInfoByToken(@RequestParam String userToken) {
        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(userToken);
        if (username == null) {
            throw new AuthenticationException("token非法无效!");
        }
        //查看用户
        SysUser sysUser = sysUserService.getUserByName(username);
        if (sysUser == null) {
            throw new AuthenticationException("token非法无效!");
        }
        sysUser.setPassword("");
        sysUser.setSalt("");
        if (StringUtils.isBlank(sysUser.getPost())) {
            sysUser.setPost("");
        }

        List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());

        if (departs == null || departs.size() == 0) {
            // 添加部门id到userInfo
            sysUser.setDepartId("");
        } else if (departs.size() == 1) {
            // 添加部门id到userInfo
            sysUser.setDepartId(departs.get(0).getId());
        } else {
            // 添加部门id到userInfo
            for (SysDepart depart : departs) {
                if (depart.getOrgCode().equals(sysUser.getOrgCode())) {
//                    sysUser.setDepartId(depart.getId());
//                    setSysUserTrackOrgIds(departs.get(0), sysUser);
                    setSysUserDepartIds(departs.get(0), sysUser);
                    break;
                }
            }
        }
        return new Result<SysUser>().successResult(sysUser);
    }

    @GetMapping("/listByGroupId")
    @ApiOperation(value = "根据工班（班组）id获取人员信息")
    public Result<IPage<SysUser>> listByGroupId(@RequestParam(name = "groupId") @ApiParam(name = "groupId", value = "班组（工班）id") String groupId,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        List<SysUser> userList = sysUserService.listByGroupId(groupId);
        List<SysUser> userListVo = userList.stream()
                .skip(pageSize * (pageNo - 1))
                .limit(pageSize).collect(Collectors.toList());
        IPage<SysUser> page = new Page();
        page.setRecords(userListVo);
        page.setCurrent(pageNo);
        page.setPages(pageSize);
        page.setTotal(userList.size());
        return new Result<IPage<SysUser>>().successResult(page);
    }

    @GetMapping("/queryUser")
    @ApiOperation(value = "查询人员信息")
    public Result<IPage<SysUser>> selectUser(@RequestParam(name = "groupId") @ApiParam(name = "groupId", value = "班组（工班）id") String groupId,
                                             @RequestParam(name = "searchText") @ApiParam(name = "searchText", value = "账号或用户名") String searchText,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        List<SysUser> userList = sysUserService.selectListByGroupIdAndSearchText(groupId, searchText);
        List<SysUser> userListVo = userList.stream()
                .skip(pageSize * (pageNo - 1))
                .limit(pageSize).collect(Collectors.toList());
        IPage<SysUser> page = new Page();
        page.setRecords(userListVo);
        page.setCurrent(pageNo);
        page.setPages(pageSize);
        page.setTotal(userList.size());
        return new Result<IPage<SysUser>>().successResult(page);
    }

    @GetMapping(value = "/queryWorkstation")
    @ApiOperation(value = "查询工位信息")
    public Result<Page<BuBaseWorkstation>> queryWorkstationPage(@Validated BuBaseWorkstationQueryVO buBaseWorkstationQueryVO,
                                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BuBaseWorkstation> page = workstationService.selectWorkstationPage(new Page<>(pageNo, pageSize), buBaseWorkstationQueryVO);
        return new Result<Page<BuBaseWorkstation>>().successResult(page);
    }

    //    private void setSysUserTrackOrgIds(SysDepart depart, SysUser sysUser) {
//        if ("4".equals(depart.getOrgCategory())) {
//            // 部门为班组时，班组id=部门id
//            BuMtrWorkshopGroup workshopGroup = sysDepartService.getWorkshopGroupByGroupId(depart.getId());
//            sysUser.setWorkshopId(workshopGroup.getWorkshopId())
//                    .setDepotId(workshopGroup.getDepotId())
//                    .setLineId(workshopGroup.getLineId());
//        }
//    }
    private void setSysUserDepartIds(SysDepart depart, SysUser sysUser) {
        if (null == depart || null == sysUser) {
            return;
        }

        sysUser.setDepartId(depart.getId())
                .setDepartIsGroup(depart.getDepartIsGroup())
                .setDepotId(depart.getDepotId())
                .setCompanyId(depart.getCompanyId())
                .setWorkshopId(depart.getWorkshopId())
                .setLineId(depart.getLineId());
    }

}
