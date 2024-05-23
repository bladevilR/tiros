package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.MD5Util;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.group.information.bean.BuMtrWorkshopGroup;
import org.jeecg.modules.shiro.vo.DefContants;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysLoginModel;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 登录 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-02-23
 */
@AppController
@Api(tags = "登录-公共")
@Slf4j
@RestController
@RequestMapping("/app")
public class AppLoginController {

    @Value("${jeecg.captcha.enable:false}")
    private Boolean captchaEnable;

    private final ISysUserService sysUserService;
    private final ISysBaseAPI sysBaseAPI;
    private final RedisUtil redisUtil;
    private final ISysDepartService sysDepartService;
    private final ISysDictService sysDictService;

    public AppLoginController(ISysUserService sysUserService,
                              ISysBaseAPI sysBaseAPI,
                              RedisUtil redisUtil,
                              ISysDepartService sysDepartService,
                              ISysDictService sysDictService) {
        this.sysUserService = sysUserService;
        this.sysBaseAPI = sysBaseAPI;
        this.redisUtil = redisUtil;
        this.sysDepartService = sysDepartService;
        this.sysDictService = sysDictService;
    }


    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = sysLoginModel.getUsername();
        String password = sysLoginModel.getPassword();
        //update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题
        //前端密码加密，后端进行密码解密
        //password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密码解密
        //update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题

        //update-begin-author:taoyan date:20190828 for:校验验证码
        if (captchaEnable) {
            String captcha = sysLoginModel.getCaptcha();
            if (captcha == null) {
                result.error500("验证码无效");
                return result;
            }
            String lowerCaseCaptcha = captcha.toLowerCase();
            String realKey = MD5Util.MD5Encode(lowerCaseCaptcha + sysLoginModel.getCheckKey(), "utf-8");
            Object checkCode = redisUtil.get(realKey);
            if (checkCode == null || !checkCode.equals(lowerCaseCaptcha)) {
                result.error500("验证码错误");
                return result;
            }
        }
        //update-end-author:taoyan date:20190828 for:校验验证码

        //1. 校验用户是否有效
        SysUser sysUser = sysUserService.getUserByName(username);
        result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }

        //2. 校验用户名或密码是否正确
        String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
        String syspassword = sysUser.getPassword();
        if (!syspassword.equals(userpassword)) {
            result.error500("用户名或密码错误");
            return result;
        }

        //用户登录信息
        userInfo(sysUser, result);
        sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

        return result;
    }

    @ApiOperation(value = "登录(无验证码版)")
    @RequestMapping(value = "/login2", method = RequestMethod.POST)
    public Result<JSONObject> login2(@RequestBody SysLoginModel sysLoginModel) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = sysLoginModel.getUsername();
        String password = sysLoginModel.getPassword();
        //update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题
        //前端密码加密，后端进行密码解密
        //password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密码解密
        //update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题

        //update-begin-author:taoyan date:20190828 for:校验验证码

        //update-end-author:taoyan date:20190828 for:校验验证码

        //1. 校验用户是否有效
        SysUser sysUser = sysUserService.getUserByName(username);
        result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }

        //2. 校验用户名或密码是否正确
        String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
        String syspassword = sysUser.getPassword();
        if (!syspassword.equals(userpassword)) {
            result.error500("用户名或密码错误");
            return result;
        }

        //用户登录信息
        userInfo(sysUser, result);
        sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

        return result;
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiOperation(value = "退出登录")
    public Result<Object> logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //用户退出逻辑
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        if (oConvertUtils.isEmpty(token)) {
            log.error("退出登录失败！，token=" + token);
            return Result.error("退出登录失败！");
        }
        String username = JwtUtil.getUsername(token);
        LoginUser sysUser = sysBaseAPI.getUserByName(username);
        if (sysUser != null) {
            sysBaseAPI.addLog("用户名: " + sysUser.getRealname() + ",退出成功！", CommonConstant.LOG_TYPE_1, null);
            log.info(" 用户名:  " + sysUser.getRealname() + ",退出成功！ ");
            //清空用户登录Token缓存
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            //调用shiro的logout
            SecurityUtils.getSubject().logout();
            return Result.ok("退出登录成功！");
        } else {
            log.error("退出登录，Token无效!，token=" + token);
            return Result.error("Token无效!");
        }
    }


    private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
        String syspassword = sysUser.getPassword();
        String username = sysUser.getUsername();
        // 生成token
        String token = JwtUtil.sign(sysUser.getId(), username, syspassword);
        // 设置token缓存有效时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        // 获取用户部门信息
        JSONObject obj = new JSONObject();
        List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
        obj.put("departs", departs);
        if (departs == null || departs.size() == 0) {
            obj.put("multi_depart", 0);
            // 添加部门id到userInfo
            sysUser.setDepartId("");
        } else if (departs.size() == 1) {
            sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
            obj.put("multi_depart", 1);
            // 添加部门id到userInfo
//            sysUser.setDepartId(departs.get(0).getId());
//            setSysUserTrackOrgIds(departs.get(0), sysUser);
            setSysUserDepartIds(departs.get(0), sysUser);
        } else {
            obj.put("multi_depart", 2);
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
        obj.put("token", token);

        obj.put("userInfo", sysUser);
        obj.put("sysAllDictItems", sysDictService.queryAllDictItems());

//        // 加入线路信息
//        List<BuMtrLine> lineList = buMtrLineService.selectListByUserId(sysUser.getId());
//        obj.put("lines", lineList);

        result.setResult(obj);
        result.success("登录成功");
        return result;
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