package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.mapper.SysDictMapper;
import org.jeecg.modules.system.model.DuplicateCheckVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: DuplicateCheckAction
 * @Description: 重复校验工具
 * @Author 张代浩
 * @Date 2019-03-25
 * @Version V1.0
 */
@Api(tags = "重复校验")
@Slf4j
@RestController
@RequestMapping("/sys/duplicate")
public class DuplicateCheckController {

    private final SysDictMapper sysDictMapper;

    public DuplicateCheckController(SysDictMapper sysDictMapper) {
        this.sysDictMapper = sysDictMapper;
    }


    @PostMapping(value = "/check")
    @ApiOperation(value = "重复校验接口", notes = "校验数据是否在系统中是否存在")
    public Result<Boolean> doDuplicateCheck(@RequestBody DuplicateCheckVO checkVO) throws Exception {
        log.info("------重复校验------参数为:" + checkVO.toString());

        Long num = null;
        if (StringUtils.isNotBlank(checkVO.getTableName())
                && StringUtils.isNotBlank(checkVO.getFieldName())
                && StringUtils.isNotBlank(checkVO.getFieldVal())) {
            // sql里根据dataId是否为空判断是添加页面校验还是编辑页面校验
            num = sysDictMapper.duplicateCheckCountSql(checkVO);
        }

        if (num == null) {
            // 没有表和字段数据
            return new Result<Boolean>().error500("请输入");
        } else if (num == 0) {
            // 该值可用
            return new Result<Boolean>().successResult(true).success("该值可用");
        } else {
            // 该值不可用
            log.info("该值不可用，系统中已存在");
            return new Result<Boolean>().error500("该值不可用，系统中已存在");
        }
    }

}
