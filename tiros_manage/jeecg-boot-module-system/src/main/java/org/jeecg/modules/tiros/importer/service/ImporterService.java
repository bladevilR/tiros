package org.jeecg.modules.tiros.importer.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The interface Importer service.
 *
 * @author yuyougen
 * @title: ImporterService
 * @projectName tiros -manage-parent
 * @description: TODO
 * @date 2021 /4/1514:15
 */
public interface ImporterService {


    /**
     * Import regu.
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importRegu(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * Import plan.
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importPlan(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * Import work record.
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importWorkRecord(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * Import material must.
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importMaterialMust(MultipartFile file, HttpServletRequest request) throws Exception;

//    /**
//     * Import importMaterialEntryOrder
//     *
//     * @param file    the file
//     * @param request the request
//     * @throws Exception the exception
//     */
//    void importMaterialEntryOrder(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * Import importMaterialType
     *
     * @param file    the file
     * @param request the request
     * @return the boolean
     * @throws Exception the exception
     */
    Boolean importMaterialType(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * Import importMaterialSparePart
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importMaterialSparePart(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * Import importRptBoard
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importRptBoard(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * Import materialTools
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importMaterialTools(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * 导入作业检查表
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importWorkCheck(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * 导入班组库存
     *
     * @param file    the file
     * @param request the request
     * @throws Exception the exception
     */
    void importGroupStock(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * 导入领用明细用来同步价格
     *
     * @param file the file
     */
    void importMaterialApplyUsingSyncPrice(MultipartFile file) throws Exception;

    /**
     * 导入物料成本系统维度
     *
     * @param files
     * @throws Exception 异常信息
     */
    void importMaterialCostBySystem(List<MultipartFile> files) throws Exception;

    /**
     * 导入历史故障信息
     *
     * @param file
     */
    String importFaultHistory(MultipartFile file, HttpServletRequest request) throws Exception;

}
