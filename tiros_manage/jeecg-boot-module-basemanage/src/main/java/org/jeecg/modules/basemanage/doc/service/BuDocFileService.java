package org.jeecg.modules.basemanage.doc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.basemanage.doc.bean.BuDocFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.doc.bean.BuFileAndDirectory;
import org.jeecg.modules.basemanage.doc.bean.vo.BuDocFileQueryVO;

import java.util.List;

/**
 * <p>
 * 文件信息 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
public interface BuDocFileService extends IService<BuDocFile> {
    /**
     * 分页查询
     * @param page
     * @param fileQueryVO
     * @return
     */
    Page<BuFileAndDirectory> listPage(Page<BuFileAndDirectory> page, BuDocFileQueryVO fileQueryVO);

    void removeBatch(List<String> asList) throws Exception;

    /**
     * 批量新增文件
     * @param file
     * @return
     */
    boolean saveBatchFile(List<BuDocFile> file) throws Exception;

    /**
     * 查询是否有权限
     * @param id
     * @param operation
     * @return
     */
    boolean isPrivilege(String id, Integer operation);

}
