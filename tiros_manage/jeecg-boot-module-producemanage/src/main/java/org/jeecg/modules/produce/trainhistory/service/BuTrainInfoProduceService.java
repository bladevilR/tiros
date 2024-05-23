package org.jeecg.modules.produce.trainhistory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.produce.trainhistory.bean.BuTrainInfo;
import org.jeecg.modules.produce.trainhistory.bean.BuWorkLeaveRecord;
import org.jeecg.modules.produce.trainhistory.bean.BuWorkRectify;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainPeriodTimeLine;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuWorkLeaveRecordQueryVO;
import org.jeecg.modules.produce.trainhistory.bean.vo.query.BuWorkRectifyQueryVO;

import java.util.List;

/**
 * <p>
 * 车辆信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -10-28
 */
public interface BuTrainInfoProduceService extends IService<BuTrainInfo> {

    /**
     * 根据车辆号查询车辆信息
     *
     * @param trainNo 车辆号
     * @return 车辆信息
     * @throws Exception 异常
     */
    BuTrainInfo getTrainInfoByTrainNo(String trainNo) throws Exception;

    /**
     * 查询有股道信息的车辆列表
     *
     * @param lineId 线路id
     * @return 有股道信息的车辆列表
     * @throws Exception 异常
     */
    List<BuTrainInfo> listTrainHasTrack(String lineId) throws Exception;

    /**
     * 根据车辆号查询车辆架修周期时间线
     *
     * @param trainNo 车辆号
     * @return 车辆架修周期时间线
     * @throws Exception 异常
     */
    List<BuTrainPeriodTimeLine> listTrainPeriodTimeLine(String trainNo) throws Exception;

    /**
     * Page leave record page.
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return the page
     */
    IPage<BuWorkLeaveRecord> pageLeaveRecord(BuWorkLeaveRecordQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * Page rectify page.
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return the page
     */
    IPage<BuWorkRectify> pageRectify(BuWorkRectifyQueryVO queryVO, Integer pageNo, Integer pageSize);

}
