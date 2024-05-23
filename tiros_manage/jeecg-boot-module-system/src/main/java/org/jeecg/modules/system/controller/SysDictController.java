package org.jeecg.modules.system.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.DictNumberModel;
import org.jeecg.common.system.vo.DictQuery;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysDict;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.model.SysDictTree;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.vo.SysDictPage;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@RestController
@RequestMapping("/sys/dict")
@Slf4j
@Api(tags = "字典接口")
public class SysDictController {

    @Autowired
    private ISysDictService sysDictService;
    @Autowired
    private ISysDictItemService sysDictItemService;
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysDict>> queryPageList(SysDict sysDict, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysDict>> result = new Result<IPage<SysDict>>();
        QueryWrapper<SysDict> queryWrapper = QueryGenerator.initQueryWrapper(sysDict, req.getParameterMap());
        Page<SysDict> page = new Page<SysDict>(pageNo, pageSize);
        IPage<SysDict> pageList = sysDictService.page(page, queryWrapper);
        log.debug("查询当前页：" + pageList.getCurrent());
        log.debug("查询当前页数量：" + pageList.getSize());
        log.debug("查询结果数量：" + pageList.getRecords().size());
        log.debug("数据总数：" + pageList.getTotal());
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @param sysDict
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     * @功能：获取树形字典数据
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/treeList", method = RequestMethod.GET)
    public Result<List<SysDictTree>> treeList(SysDict sysDict, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<List<SysDictTree>> result = new Result<>();
        LambdaQueryWrapper<SysDict> query = new LambdaQueryWrapper<>();
        // 构造查询条件
        String dictName = sysDict.getDictName();
        if (oConvertUtils.isNotEmpty(dictName)) {
            query.like(true, SysDict::getDictName, dictName);
        }
        query.orderByDesc(true, SysDict::getCreateTime);
        List<SysDict> list = sysDictService.list(query);
        List<SysDictTree> treeList = new ArrayList<>();
        for (SysDict node : list) {
            treeList.add(new SysDictTree(node));
        }
        result.setSuccess(true);
        result.setResult(treeList);
        return result;
    }

    /**
     * 获取字典数据
     * --如果修改记得同步修改AppDictController中的方法
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
//            log.info(result.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }

        return result;
    }

    /**
     * 获取全部字典数据
     *
     * @return
     */
    @RequestMapping(value = "/queryAllDictItems", method = RequestMethod.GET)
    public Result<?> queryAllDictItems(HttpServletRequest request) {
        Map<String, List<DictModel>> res = new HashMap<String, List<DictModel>>();
        res = sysDictService.queryAllDictItems();
        return Result.ok(res);
    }

    /**
     * 获取字典数据
     *
     * @param dictCode
     * @return
     */
    @RequestMapping(value = "/getDictText/{dictCode}/{key}", method = RequestMethod.GET)
    public Result<String> getDictText(@PathVariable("dictCode") String dictCode, @PathVariable("key") String key) {
        log.info(" dictCode : " + dictCode);
        Result<String> result = new Result<String>();
        String text = null;
        try {
            text = sysDictService.queryDictTextByKey(dictCode, key);
            result.setSuccess(true);
            result.setResult(text);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }
        return result;
    }

    /**
     * 大数据量的字典表 走异步加载  即前端输入内容过滤数据
     *
     * @param dictCode
     * @return
     */
    @RequestMapping(value = "/loadDict/{dictCode}", method = RequestMethod.GET)
    public Result<List<DictModel>> loadDict(@PathVariable String dictCode, @RequestParam(name = "keyword") String keyword, @RequestParam(value = "sign", required = false) String sign, HttpServletRequest request) {
        log.info(" 加载字典表数据,加载关键字: " + keyword);
        Result<List<DictModel>> result = new Result<List<DictModel>>();
        List<DictModel> ls = null;
        try {
            if (dictCode.contains(",")) {
                String[] params = dictCode.split(",");
                if (params.length != 3) {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
                ls = sysDictService.queryTableDictItems(params[0], params[1], params[2], keyword);
                result.setSuccess(true);
                result.setResult(ls);
//                log.info(result.toString());
            } else {
                result.error500("字典Code格式不正确！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }

        return result;
    }

    /**
     * 根据字典code加载字典text 返回
     */
    @RequestMapping(value = "/loadDictItem/{dictCode}", method = RequestMethod.GET)
    public Result<List<String>> loadDictItem(@PathVariable String dictCode, @RequestParam(name = "key") String key, @RequestParam(value = "sign", required = false) String sign, HttpServletRequest request) {
        Result<List<String>> result = new Result<>();
        try {
            if (dictCode.contains(",")) {
                String[] params = dictCode.split(",");
                if (params.length != 3) {
                    result.error500("字典Code格式不正确！");
                    return result;
                }
                List<String> texts = sysDictService.queryTableDictByKeys(params[0], params[1], params[2], key.split(","));

                result.setSuccess(true);
                result.setResult(texts);
//                log.info(result.toString());
            } else {
                result.error500("字典Code格式不正确！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
            return result;
        }

        return result;
    }

    /**
     * 根据表名——显示字段-存储字段 pid 加载树形数据
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadTreeData(@RequestParam(name = "pid") String pid, @RequestParam(name = "pidField") String pidField,
                                                      @RequestParam(name = "tableName") String tbname,
                                                      @RequestParam(name = "text") String text,
                                                      @RequestParam(name = "code") String code,
                                                      @RequestParam(name = "hasChildField") String hasChildField,
                                                      @RequestParam(name = "condition") String condition,
                                                      @RequestParam(value = "sign", required = false) String sign, HttpServletRequest request) {
        Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
        Map<String, String> query = null;
        if (oConvertUtils.isNotEmpty(condition)) {
            query = JSON.parseObject(condition, Map.class);
        }
        // SQL注入漏洞 sign签名校验(表名,label字段,val字段,条件)
        String dictCode = tbname + "," + text + "," + code + "," + condition;
        List<TreeSelectModel> ls = sysDictService.queryTreeList(query, tbname, text, code, pidField, pid, hasChildField);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 【APP接口】根据字典配置查询表字典数据
     *
     * @param query
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/queryTableData")
    public Result<List<DictModel>> queryTableData(DictQuery query,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(value = "sign", required = false) String sign, HttpServletRequest request) {
        Result<List<DictModel>> res = new Result<List<DictModel>>();
        // SQL注入漏洞 sign签名校验
        String dictCode = query.getTable() + "," + query.getText() + "," + query.getCode();
        List<DictModel> ls = this.sysDictService.queryDictTablePageList(query, pageSize, pageNo);
        res.setResult(ls);
        res.setSuccess(true);
        return res;
    }

    /**
     * @param sysDict
     * @return
     * @功能：新增
     */
    //@RequiresRoles({"admin"})
    @RequiresPermissions(value={"/isystem/dict"})
    @ApiOperation(value = "添加数据字典")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysDict> add(@RequestBody SysDict sysDict) {
        Result<SysDict> result = new Result<>();
        try {
            sysDict.setCreateTime(new Date());
            sysDict.setDelFlag(CommonConstant.DEL_FLAG_0);
            sysDictService.save(sysDict);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param sysDict
     * @return
     * @功能：编辑
     */
    //@RequiresRoles({"admin"})
    @RequiresPermissions(value={"/isystem/dict"})
    @ApiOperation(value = "修改数据字典")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysDict> edit(@RequestBody SysDict sysDict) {
        Result<SysDict> result = new Result<>();
        SysDict sysdict = sysDictService.getById(sysDict.getId());
        if (sysdict == null) {
            result.error500("未找到对应实体");
        } else {
            sysDict.setUpdateTime(new Date());
            boolean ok = sysDictService.updateById(sysDict);
            if (ok) {
                result.success("编辑成功!");
            }
        }
        return result;
    }

    /**
     * @param id
     * @return
     * @功能：删除
     */
    //@RequiresRoles({"admin"})
    @RequiresPermissions(value={"/isystem/dict"})
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<SysDict> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysDict> result = new Result<SysDict>();
        boolean ok = sysDictService.removeById(id);
        if (ok) {
            result.success("删除成功!");
        } else {
            result.error500("删除失败!");
        }
        return result;
    }

    /**
     * @param ids
     * @return
     * @功能：批量删除
     */
    //@RequiresRoles({"admin"})
    @RequiresPermissions(value={"/isystem/dict"})
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    public Result<SysDict> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysDict> result = new Result<SysDict>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            sysDictService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * @return
     * @功能：刷新缓存
     */
    @RequestMapping(value = "/refleshCache")
    public Result<?> refleshCache() {
        Result<?> result = new Result<SysDict>();
        //清空字典缓存
        Set keys = redisTemplate.keys(CacheConstant.SYS_DICT_CACHE + "*");
        Set keys2 = redisTemplate.keys(CacheConstant.SYS_DICT_TABLE_CACHE + "*");
        Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
        Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
        redisTemplate.delete(keys);
        redisTemplate.delete(keys2);
        redisTemplate.delete(keys3);
        redisTemplate.delete(keys4);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysDict sysDict, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysDict> queryWrapper = QueryGenerator.initQueryWrapper(sysDict, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysDictPage> pageList = new ArrayList<SysDictPage>();

        List<SysDict> sysDictList = sysDictService.list(queryWrapper);
        for (SysDict dictMain : sysDictList) {
            SysDictPage vo = new SysDictPage();
            BeanUtils.copyProperties(dictMain, vo);
            // 查询机票
            List<SysDictItem> sysDictItemList = sysDictItemService.selectItemsByMainId(dictMain.getId());
            vo.setSysDictItemList(sysDictItemList);
            pageList.add(vo);
        }

        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "数据字典");
        // 注解对象Class
        mv.addObject(NormalExcelConstants.CLASS, SysDictPage.class);
        // 自定义表格参数
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("数据字典列表", "导出人:" + user.getRealname(), "数据字典"));
        // 导出数据列表
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param
     * @return
     */
    @RequiresPermissions(value={"/isystem/dict"})
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(2);
            params.setNeedSave(true);
            try {
                List<SysDictPage> list = ExcelImportUtil.importExcel(file.getInputStream(), SysDictPage.class, params);
                // 错误信息
                List<String> errorMessage = new ArrayList<>();
                int successLines = 0, errorLines = 0;
                for (int i = 0; i < list.size(); i++) {
                    SysDict po = new SysDict();
                    BeanUtils.copyProperties(list.get(i), po);
                    po.setDelFlag(CommonConstant.DEL_FLAG_0);
                    try {
                        Integer integer = sysDictService.saveMain(po, list.get(i).getSysDictItemList());
                        if (integer > 0) {
                            successLines++;
                        } else {
                            errorLines++;
                            int lineNumber = i + 1;
                            errorMessage.add("第 " + lineNumber + " 行：字典编码已经存在，忽略导入。");
                        }
                    } catch (Exception e) {
                        errorLines++;
                        int lineNumber = i + 1;
                        errorMessage.add("第 " + lineNumber + " 行：字典编码已经存在，忽略导入。");
                    }
                }
                return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorMessage);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }


    /**
     * 查询被删除的列表
     *
     * @return
     */
    @RequestMapping(value = "/deleteList", method = RequestMethod.GET)
    public Result<List<SysDict>> deleteList() {
        Result<List<SysDict>> result = new Result<List<SysDict>>();
        List<SysDict> list = this.sysDictService.queryDeleteList();
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 物理删除
     *
     * @param id
     * @return
     */
    @RequiresPermissions(value={"/isystem/dict"})
    @RequestMapping(value = "/deletePhysic/{id}", method = RequestMethod.DELETE)
    public Result<?> deletePhysic(@PathVariable String id) {
        try {
            sysDictService.deleteOneDictPhysically(id);
            return Result.ok("删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败!");
        }
    }

    /**
     * 取回
     *
     * @param id
     * @return
     */
    @RequiresPermissions(value={"/isystem/dict"})
    @RequestMapping(value = "/back/{id}", method = RequestMethod.PUT)
    public Result<?> back(@PathVariable String id) {
        try {
            sysDictService.updateDictDelFlag(0, id);
            return Result.ok("操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败!");
        }
    }

}
