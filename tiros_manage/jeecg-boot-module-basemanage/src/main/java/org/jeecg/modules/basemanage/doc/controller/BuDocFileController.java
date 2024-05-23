package org.jeecg.modules.basemanage.doc.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.basemanage.doc.bean.BuDocDirectory;
import org.jeecg.modules.basemanage.doc.bean.BuDocFile;
import org.jeecg.modules.basemanage.doc.bean.BuDocPrivilege;
import org.jeecg.modules.basemanage.doc.bean.BuFileAndDirectory;
import org.jeecg.modules.basemanage.doc.bean.vo.BuDocFileQueryVO;
import org.jeecg.modules.basemanage.doc.service.BuDocDirectoryService;
import org.jeecg.modules.basemanage.doc.service.BuDocFileService;
import org.jeecg.modules.basemanage.doc.service.BuDocPrivilegeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 文件信息 前端控制器
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
@Slf4j
@Api(tags = "工艺文件管理")
@RestController
@RequestMapping("/doc/file/")
public class BuDocFileController {

    private final BuDocDirectoryService directoryService;
    private final BuDocFileService fileService;
    private final BuDocPrivilegeService privilegeService;

    public BuDocFileController(BuDocDirectoryService directoryService,
                               BuDocFileService fileService,
                               BuDocPrivilegeService privilegeService) {
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.privilegeService = privilegeService;
    }


    @GetMapping("directoryTree")
    @ApiOperation("查询文件目录树")
    @OperationLog()
    public Result<List<BuDocDirectory>> listDirectoryTree(@RequestParam(required = false) @ApiParam(value = "目录id") String id) {
        List<BuDocDirectory> docDirectoryList = directoryService.listDirectoryTree(id);
        return new Result<List<BuDocDirectory>>().successResult(docDirectoryList);
    }

    @GetMapping("directoryTree/techFile")
    @ApiOperation("查询工艺文件目录树")
    @OperationLog()
    public Result<List<BuDocDirectory>> listDirectoryTreeTech() {
        List<BuDocDirectory> docDirectoryList = directoryService.listDirectoryTreeTech();
        return new Result<List<BuDocDirectory>>().successResult(docDirectoryList);
    }

    @GetMapping("fileType")
    @ApiOperation("查询文件所有类型")
    @OperationLog()
    public Result<List<String>> listFileType() {
        List<String> fileTypeList = directoryService.listFileType();
        return new Result<List<String>>().successResult(fileTypeList);
    }

    @GetMapping("page")
    @ApiOperation("查询文件")
    @OperationLog()
    public Result<Page<BuFileAndDirectory>> listFilePage(@Validated BuDocFileQueryVO fileQueryVO,
                                                         @RequestParam(defaultValue = "1") Integer pageNo,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BuFileAndDirectory> buDocFilePage = fileService.listPage(new Page<>(pageNo, pageSize), fileQueryVO);
        return new Result<Page<BuFileAndDirectory>>().successResult(buDocFilePage);
    }

    @PostMapping("add")
    @ApiOperation(value = "新增目录")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> add(@RequestBody @Validated BuDocDirectory directory) throws Exception {
        boolean flag = directoryService.saveDirectory(directory);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改目录或者文件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> edit(@RequestBody @Validated BuFileAndDirectory fileAndDirectory) throws Exception {
        boolean flag = directoryService.editFileAndDirectory(fileAndDirectory);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("addFile")
    @ApiOperation(value = "新增文件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_2)
    public Result<Boolean> addFile(@RequestBody @Validated List<BuDocFile> file) throws Exception {
        boolean flag = fileService.saveBatchFile(file);
        return new Result<Boolean>().successResult(flag);
    }

    @PostMapping("delete")
    @ApiOperation("批量删除文件")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deleteBatch(@RequestParam @ApiParam(value = "文件id，多个用英文逗号分隔") String ids) throws Exception {
        fileService.removeBatch(Arrays.asList(ids.split(",")));
        return new Result<Boolean>().successResult(true);
    }

    @PostMapping("privilege/setting")
    @ApiOperation("设置权限")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<Boolean> settingPrivilege(@Validated @RequestBody BuDocPrivilege privilege) throws Exception {
        boolean flag = privilegeService.savePrivilege(privilege);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("privilege/{id}")
    @ApiOperation("权限列表")
    @OperationLog()
    public Result<List<BuDocPrivilege>> privilege(@PathVariable @ApiParam(value = "文档或者目录id") String id) {
        List<BuDocPrivilege> privilegeList = privilegeService.selectByTargetId(id);
        return new Result<List<BuDocPrivilege>>().successResult(privilegeList);
    }

    @PostMapping("privilege/delete/{id}")
    @ApiOperation("删除权限")
    @OperationLog(operateType = CommonConstant.OPERATE_TYPE_4)
    public Result<Boolean> deletePrivilege(@PathVariable @ApiParam(value = "权限id") String id) {
        boolean flag = privilegeService.removeById(id);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("isPrivilege")
    @ApiOperation("判断是否有权限")
    @OperationLog()
    public Result<Boolean> isPrivilege(@RequestParam @ApiParam(value = "文件或目录id") String id,
                                       @RequestParam @ApiParam(value = "操作") Integer operation) {
        boolean flag = fileService.isPrivilege(id, operation);
        return new Result<Boolean>().successResult(flag);
    }

    @GetMapping("dirParentId/{id}")
    @ApiOperation("根据目录id获取最顶层的目录id")
    @OperationLog()
    public Result<String> dirParentId(@PathVariable @ApiParam(value = "目录id") String id) {
        String dirParentId = directoryService.selectDirParentId(id);
        return new Result<String>().successResult(dirParentId);
    }

    @GetMapping("shared/{id}")
    @ApiOperation("查询共享目录")
    @OperationLog()
    public Result<List<BuDocDirectory>> sharedDirectory(@PathVariable @ApiParam(value = "目录id") String id) throws Exception {
        return new Result<List<BuDocDirectory>>().successResult(directoryService.selectSharedDirectory(id));
    }

}

