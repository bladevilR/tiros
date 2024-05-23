package org.jeecg.modules.outsource.training.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;
import org.jeecg.modules.outsource.training.bean.BuTrainingRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.training.bean.vo.BuTrainingRecordQueryVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author youGen
 * @since 2021 -05-02
 */
public interface BuTrainingRecordService extends IService<BuTrainingRecord> {

    /**
     * Page page.
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return the page
     * @throws Exception the exception
     */
    IPage<BuTrainingRecord> page(BuTrainingRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws  Exception;

    /**
     * Save training record boolean.
     *
     * @param trainingRecord the training record
     * @return the boolean
     * @throws Exception the exception
     */
    boolean saveTrainingRecord(BuTrainingRecord trainingRecord) throws Exception;

    /**
     * Edit training record boolean.
     *
     * @param trainingRecord the training record
     * @return the boolean
     * @throws Exception the exception
     */
    boolean editTrainingRecord(BuTrainingRecord trainingRecord) throws Exception;

    /**
     * Del batch boolean.
     *
     * @param asList the as list
     * @return the boolean
     * @throws Exception the exception
     */
    boolean delBatch(List<String> asList) throws Exception;

    /**
     * User page page.
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return the page
     */
    IPage<BuTrainingRecord> userPage(BuTrainingRecordQueryVO queryVO, Integer pageNo, Integer pageSize);
}
