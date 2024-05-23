package org.jeecg.modules.produce.workshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.workshop.bean.BuMtrWorkshop;
import org.jeecg.modules.produce.workshop.bean.vo.BuWorkstationQueryVO;
import org.jeecg.modules.produce.workshop.bean.vo.BuWorkstationWorkVO;

import java.util.List;

/**
 * <p>
 * 架修车间 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-11-30
 */
public interface BuWorkshopMonitorService extends IService<BuMtrWorkshop> {

    /**
     * 根据id查询车间平面图存放地址
     *
     * @param workshopId 车间id
     * @return 车间平面图地址
     * @throws Exception 异常信息
     */
    String getGraphAddressByWorkShopId(String workshopId) throws Exception;

    /**
     * 根据条件查询车间工位信息列表
     *
     * @param queryVO 查询条件
     * @return 车间工位信息列表
     * @throws Exception 异常信息
     */
    List<BuWorkstationWorkVO> listWorkstation(BuWorkstationQueryVO queryVO) throws Exception;

    /**
     * 根据工位id查询工位作业信息
     *
     * @param id 工位id
     * @return 工位作业信息
     * @throws Exception 异常信息
     */
    BuWorkstationWorkVO getWorkstationWorkInfo(String id) throws Exception;

}