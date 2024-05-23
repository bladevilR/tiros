package org.jeecg.modules.basemanage.workstation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseRefStationForm;
import org.jeecg.modules.basemanage.workstation.entity.vo.UnRelatedFormQueryVO;
import org.jeecg.modules.basemanage.workstation.entity.vo.WorkstationFormInfoVO;

import java.util.List;

/**
 * <p>
 * 工位表单关联 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-14
 */
public interface BuBaseRefStationFormService extends IService<BuBaseRefStationForm> {

    /**
     * 根据工位id查询工位已关联表单
     *
     * @param queryVO
     * @return 已关联表单信息
     * @throws Exception 异常
     */
    List<BuBaseRefStationForm> listRelatedFormByWorkstationId(UnRelatedFormQueryVO queryVO) throws Exception;

    /**
     * 分页查询工位未关联表单信息
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<WorkstationFormInfoVO> pageUnRelatedForm(UnRelatedFormQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

}
