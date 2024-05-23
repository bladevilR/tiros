package org.jeecg.modules.basemanage.workcheck.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.workcheck.bean.BuWorkCheck;
import org.jeecg.modules.basemanage.workcheck.bean.vo.BuWorkCheckQueryVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author youGen
 * @since 2021 -05-17
 */
public interface BuWorkCheckService extends IService<BuWorkCheck> {

    /**
     * Select page page.
     *
     * @param workCheckPage the work check page
     * @param queryVO       the query vo
     * @return the page
     */
    Page<BuWorkCheck> selectPage(Page<BuWorkCheck> workCheckPage, BuWorkCheckQueryVO queryVO);

    /**
     * 根据id查询检查表单详情，包含关联信息
     *
     * @param id 检查表单id
     * @return 检查表单详情，包含关联信息
     * @throws Exception 异常
     */
    BuWorkCheck getWorkCheckById(String id) throws Exception;

    /**
     * Save or update work check boolean.
     *
     * @param workCheck the work check
     * @return the boolean
     * @throws Exception the exception
     */
    boolean saveOrUpdateWorkCheck(BuWorkCheck workCheck) throws Exception;

    /**
     * Delete batch work check boolean.
     *
     * @param ids the id list
     * @return the boolean
     * @throws Exception the exception
     */
    boolean deleteBatchWorkCheck(String ids) throws Exception;

}
