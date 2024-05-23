package org.jeecg.modules.basemanage.regu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguDetail;
import org.jeecg.modules.basemanage.regu.bean.ImportTechBook;
import org.jeecg.modules.basemanage.regu.bean.vo.RepairReguDetailQueryVO;

import java.util.List;

/**
 * <p>
 * 规程明细 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
public interface BuRepairReguDetailService extends IService<BuRepairReguDetail> {

    /**
     * 根据条件查询规程明细列表
     *
     * @param queryVO 查询条件
     * @return 规程明细列表
     * @throws Exception 异常信息
     */
    List<BuRepairReguDetail> listReguDetail(RepairReguDetailQueryVO queryVO) throws Exception;

    /**
     * 根据id查规程明细及关联信息
     *
     * @param id 规程明细id
     * @return 规程明细及关联信息
     * @throws Exception 异常信息
     */
    BuRepairReguDetail getReguDetailById(String id) throws Exception;

    /**
     * 新增或者修改规程明细信息
     *
     * @param reguDetail 规程明细信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveReguDetail(BuRepairReguDetail reguDetail) throws Exception;

    /**
     * 根据规程明细ids批量删除
     *
     * @param ids 规程明细ids，多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteBatchByDetailIds(String ids) throws Exception;

    /**
     * 根据规程id批量删除
     *
     * @param reguId 规程id
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteByReguId(String reguId) throws Exception;

    /**
     * 根据规程明细id关联的作业指导书，导入到选择的规程明细中
     * @param importTechBook
     * @return
     */
    boolean importTechBook(ImportTechBook importTechBook) throws Exception;
}
