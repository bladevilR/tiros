package org.jeecg.modules.basemanage.workstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseRefStationForm;
import org.jeecg.modules.basemanage.workstation.entity.vo.UnRelatedFormQueryVO;
import org.jeecg.modules.basemanage.workstation.entity.vo.WorkstationFormInfoVO;

import java.util.List;

/**
 * <p>
 * 工位表单关联 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-14
 */
public interface BuBaseRefStationFormMapper extends BaseMapper<BuBaseRefStationForm> {

    /**
     * 根据工位id查询工位已关联表单
     *
     * @param queryVO 查询条件
     * @return 已关联表单信息
     */
    List<BuBaseRefStationForm> selectListByWorkstationId(@Param("queryVO") UnRelatedFormQueryVO queryVO);

    /**
     * 分页查询工位未关联表单信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<WorkstationFormInfoVO> selectUnrelatedFormPage(IPage<WorkstationFormInfoVO> page, @Param("queryVO") UnRelatedFormQueryVO queryVO);

}
