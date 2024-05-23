package org.jeecg.modules.basemanage.traininfo.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.traininfo.entity.BuTrainInfo;
import org.jeecg.modules.basemanage.traininfo.entity.LineTrainTree;
import org.jeecg.modules.basemanage.traininfo.entity.vo.BuTrainInfoQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆结构，平时在界面显示时从车辆结构中查数据，只有在保存业务数据要用到具体的设备时，才用对应的：结构id+车辆ID去查到 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-10
 */
public interface IBuTrainInfoService extends IService<BuTrainInfo> {

    /**
     * 根据条件分页查询车辆信息
     *
     * @param queryVO 查询条件
     * @param page    分页信息
     * @return 分页结果
     * @throws Exception 异常
     */
    Page<BuTrainInfo> pageTrainInfo(BuTrainInfoQueryVO queryVO, Page<BuTrainInfo> page) throws Exception;

    /**
     * 根据车辆号查询车辆信息
     *
     * @param trainNo 车辆号
     * @return 车辆信息
     * @throws Exception 异常
     */
    BuTrainInfo getTrainInfo(String trainNo) throws Exception;

    /**
     * 新增车辆信息
     *
     * @param buTrainInfo 车辆信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveTrainInfo(BuTrainInfo buTrainInfo) throws Exception;

    /**
     * 修改车辆信息
     *
     * @param buTrainInfo 车辆信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateTrainInfo(BuTrainInfo buTrainInfo) throws Exception;

    /**
     * 根据车辆信息ids删除车辆信息
     *
     * @param trainInfoIds 车辆信息ids多个逗号分隔
     * @return 是否删除陈成功
     * @throws Exception 异常
     */
    boolean deleteBatchByTrainInfoIds(String trainInfoIds) throws Exception;

    /**
     * 判断车辆编号是否存在
     *
     * @param code 车辆编号
     * @return 是否存在
     */
    boolean isExistTrainCode(String code);

    /**
     * 获取线路-车辆树形结构数据
     *
     * @return 线路-车辆树形结构数据
     * @throws Exception 异常
     */
    List<LineTrainTree> getLineTrainTree() throws Exception;

    /**
     * 更新车辆里程
     * @param trainNo 车辆号
     * @param mileage 里程
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean updateTrainMileage(String trainNo, Double mileage)throws Exception;

}
