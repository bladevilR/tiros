package org.jeecg.modules.material.must.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.must.bean.BuMaterialMustList;
import org.jeecg.modules.material.must.bean.vo.BuMaterialMustListSetGroupVO;
import org.jeecg.modules.material.must.bean.vo.BuMaterialMustQueryVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author youGen
 * @since 2021 -04-30
 */
public interface BuMaterialMustListService extends IService<BuMaterialMustList> {

    /**
     * Page material must list page.
     *
     * @param queryVO the query vo
     * @param page    the page
     * @return the page
     * @throws Exception the exception
     */
    Page<BuMaterialMustList> pageMaterialMustList(BuMaterialMustQueryVO queryVO, Page<BuMaterialMustList> page) throws Exception;

    /**
     * Add material must list boolean.
     *
     * @param materialMustList the material must list
     * @return the boolean
     * @throws Exception the exception
     */
    boolean addMaterialMustList(BuMaterialMustList materialMustList) throws Exception;

    /**
     * Update material must list boolean.
     *
     * @param materialMustList the material must list
     * @return the boolean
     * @throws Exception the exception
     */
    boolean updateMaterialMustList(BuMaterialMustList materialMustList) throws Exception;

    /**
     * Delete batch boolean.
     *
     * @param ids the ids
     * @return the boolean
     * @throws Exception the exception
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 设置必换件清单的班组
     *
     * @param setGroupVO 必换件清单和班组信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean setMustListGroup(BuMaterialMustListSetGroupVO setGroupVO) throws Exception;

    /**
     * 批量设置必换件清单有效
     *
     * @param ids 必换件清单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean validMustList(String ids) throws Exception;

    /**
     * 批量设置必换件清单无效
     *
     * @param ids 必换件清单ids，多个逗号分割
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean invalidMustList(String ids) throws Exception;

}
