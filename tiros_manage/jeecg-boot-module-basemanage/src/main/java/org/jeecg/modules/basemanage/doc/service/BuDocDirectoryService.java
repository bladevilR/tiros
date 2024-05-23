package org.jeecg.modules.basemanage.doc.service;

import org.jeecg.modules.basemanage.doc.bean.BuDocDirectory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.doc.bean.BuFileAndDirectory;

import java.util.List;

/**
 * <p>
 * 目录信息
 * 1 顶级目录(默认创建)：个人文件、共享文件、班组文件、工艺文件，通过id查找区别，分别对应1、2 服务类
 * </p>
 *
 * @author youGen
 * @since 2020 -08-17
 */
public interface BuDocDirectoryService extends IService<BuDocDirectory> {
    /**
     * 目录结构树形结构
     *
     * @param id the id
     * @return list
     */
    List<BuDocDirectory> listDirectoryTree(String id);

    /**
     * 文件类型
     *
     * @return list
     */
    List<String> listFileType();

    /**
     * 新增目录
     *
     * @param directory the directory
     * @return boolean
     * @throws Exception the exception
     */
    boolean saveDirectory(BuDocDirectory directory) throws Exception;

    /**
     * 修改目录或者文件
     *
     * @param fileAndDirectory the file and directory
     * @return boolean
     * @throws Exception the exception
     */
    boolean editFileAndDirectory(BuFileAndDirectory fileAndDirectory) throws Exception;

    /**
     * List directory tree tech list.
     *
     * @return the list
     */
    List<BuDocDirectory> listDirectoryTreeTech();


    /**
     * Select dir parent id string.
     *
     * @param id the id
     * @return the string
     */
    String selectDirParentId(String id);

    /**
     * Select shared directory list.
     *
     * @return the list
     */
    List<BuDocDirectory> selectSharedDirectory(String id) throws Exception;
}
