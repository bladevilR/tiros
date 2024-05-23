package org.jeecg.modules.quality.rectify.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quality.rectify.bean.BuWorkRectify;
import org.jeecg.modules.quality.rectify.bean.vo.BuWorkRectifyQueryVO;

/**
 * <p>
 * 整改信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
public interface BuWorkRectifyService extends IService<BuWorkRectify> {

    /**
     * 根据条件分页查询整改信息
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuWorkRectify> pageRectify(BuWorkRectifyQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询整改详细信息
     *
     * @param id 整改id
     * @return 整改详细信息
     * @throws Exception 异常
     */
    BuWorkRectify getRectifyById(String id) throws Exception;

    /**
     * 新增整改信息
     *
     * @param buWorkRectify 整改信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveRectify(BuWorkRectify buWorkRectify) throws Exception;

    /**
     * 修改整改信息
     *
     * @param buWorkRectify 整改信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateRectify(BuWorkRectify buWorkRectify) throws Exception;

    /**
     * 批量删除整改信息
     *
     * @param ids 整改信息ids 多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 批量关闭整改信息
     *
     * @param ids 整改信息ids 多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean closeBatch(String ids) throws Exception;

}
