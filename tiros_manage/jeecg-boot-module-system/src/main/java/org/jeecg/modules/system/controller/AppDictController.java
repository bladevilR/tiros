package org.jeecg.modules.system.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AppController;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.DictNumberModel;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 字典表 app前端控制器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-16
 */
@AppController

@Api(tags = "数据字典-公共")
@Slf4j
@RestController
@RequestMapping("/app/dict")
public class AppDictController {

    private final ISysDictService sysDictService;

    public AppDictController(ISysDictService sysDictService) {
        this.sysDictService = sysDictService;
    }


    /**
     * 获取字典数据
     * --如果修改记得同步修改SysDictController中的方法
     *
     * @param dictCode 字典code
     * @param dictCode 表名,文本字段,code字段  | 举例：sys_user,realname,id
     * @return
     */
    @ApiOperation(value = "获取数据字典",
            notes = "\r\n 1.获取字典表中的字典数据。例：dictCode=bu_valid_status" +
                    "\r\n 2.获取业务表中的2个字段的数据作为字典字典数据。dictCode格式为(表名,作为text的字段,作为value的字段)。例：查线路表中的线路id和线路名称，线路名称 line_name 作为text，线路id line_id 作为value，则dictCode=bu_mtr_line,line_name,line_id" +
                    "\r\n 3.带筛选条件获取业务表中的2个字段的数据作为字典字典数据，在字典末尾用,拼接条件。例：bu_mtr_line,line_name,line_id,company_id='1515154'" +
                    "\r\n 4.获取业务表中多于2个的扩展字段返回，在字典末尾用|分割后加扩展字段(多个逗号拼接)。例：bu_mtr_line,line_name,line_id|line_num,line_length")
    @RequestMapping(value = "/getDictItems/{dictCode}", method = RequestMethod.GET)
    public Result<Object> getDictItems(@PathVariable String dictCode, @RequestParam(value = "sign", required = false) String sign, HttpServletRequest request) {
        log.debug(" dictCode : " + dictCode);
        Result<Object> result = new Result<>();
        List<DictModel> ls;
        List<DictNumberModel> dictNumberModelList = new ArrayList<>();
        String extFields = null;
        try {
            // 获取扩展字段
            if (dictCode.contains("|")) {
                String[] split = dictCode.split("\\|");
                dictCode = split[0];
                extFields = split[1];
            }
            if (dictCode.contains(",")) {
                //关联表字典（举例：sys_user,realname,id）
                String[] params = dictCode.split(",");

                if (params.length < 3) {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
                //SQL注入校验（只限制非法串改数据库）
                final String[] sqlInjCheck = {params[0], params[1], params[2]};
                SqlInjectionUtil.filterContent(sqlInjCheck);

                if (params.length == 4) {
                    // SQL注入校验（查询条件SQL 特殊check，此方法仅供此处使用）
                    SqlInjectionUtil.specialFilterContent(params[3]);
                    if (StringUtils.isNotBlank(extFields)) {
                        //SQL注入校验（查询条件SQL 特殊check，此方法仅供此处使用）
                        ls = sysDictService.queryTableDictItemsAndExtendFieldsByCodeAndFilter(params[0], params[1], params[2], extFields, params[3]);
                    } else {
                        ls = sysDictService.queryTableDictItemsByCodeAndFilter(params[0], params[1], params[2], params[3]);
                    }
                } else if (params.length == 3) {
                    if (StringUtils.isNotBlank(extFields)) {
                        ls = sysDictService.queryTableDictItemsAndExtendFieldsByCodeAndFilter(params[0], params[1], params[2], extFields, null);
                    } else {
                        ls = sysDictService.queryTableDictItemsByCode(params[0], params[1], params[2]);
                    }
                } else {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
                ls.sort(Comparator.comparing(DictModel::getText, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(DictModel::getValue, Comparator.nullsLast(Comparator.naturalOrder())));
            } else {
                Integer type = sysDictService.queryDictTypeByCode(dictCode);
                ls = sysDictService.queryDictItemsByCode(dictCode);
                if (null != type && 1 == type) {
                    // 字典为数字类型时
                    for (DictModel dictModel : ls) {
                        DictNumberModel dictNumberModel = new DictNumberModel()
                                .setText(dictModel.getText())
                                .setValue(Integer.parseInt(dictModel.getValue()))
                                .setSortOrder(dictModel.getSortOrder());
                        dictNumberModelList.add(dictNumberModel);
                    }
                    dictNumberModelList.sort(Comparator.comparing(DictNumberModel::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(DictNumberModel::getValue, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(DictNumberModel::getText, Comparator.nullsLast(Comparator.naturalOrder())));
                    result.setResult(dictNumberModelList);
                } else {
                    // 字典表
                    ls.sort(Comparator.comparing(DictModel::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(DictModel::getValue, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(DictModel::getText, Comparator.nullsLast(Comparator.naturalOrder())));
                }
            }

            result.setSuccess(true);
            if (null == result.getResult()) {
                result.setResult(ls);
            }
            log.info(result.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }

        return result;
    }

}
