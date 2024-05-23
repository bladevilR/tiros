package org.jeecg.modules.material.entry.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.entry.bean.BuMaterialEntryDetail;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryConfirmVO;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryLevelFourDetailVO;
import org.jeecg.modules.material.entry.bean.vo.BuMaterialEntryQueryVO;

/**
 * <p>
 * 每次同步新增的物资或数量都都在该表记录，一次同步如果发现有新增，则创建一条入库单及相关物资明细 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-08
 */
public interface BuMaterialEntryDetailService extends IService<BuMaterialEntryDetail> {

    /**
     * 分页查询入库明细记录
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuMaterialEntryDetail> pageEntryDetail(BuMaterialEntryQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据入库明细id查询明细信息
     *
     * @param entryDetailId 入库明细id
     * @return 入库明细信息
     * @throws Exception 异常
     */
    BuMaterialEntryDetail getEntryDetailById(String entryDetailId) throws Exception;

    /**
     * 新增/修改入库明细
     *
     * @param entryDetail 入库明细
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveEntryDetail(BuMaterialEntryDetail entryDetail) throws Exception;

    /**
     * 确认入库
     *
     * @param confirmVO 确认信息
     * @return 操作是否成功
     * @throws Exception 异常
     */
    boolean confirmEntry(BuMaterialEntryConfirmVO confirmVO) throws Exception;

    /**
     * 批量删除入库明细，当入库单下没有明细数据时删除入库单
     *
     * @param ids 入库明细ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;
    /**
     * 确认入库第四级库
     *
     * @param confirmVO 确认信息
     * @return 操作是否成功
     * @throws Exception 异常
     */
    boolean confirmEntryLevelFourWarehouse(BuMaterialEntryLevelFourDetailVO confirmVO) throws Exception;
}
