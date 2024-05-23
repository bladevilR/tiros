package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.BuOutsourceRateing;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceRateingQueryVO;

import java.util.List;

/**
 * <p>
 * 委外评分 服务类
 * </p>
 *
 * @author youGen
 * @since 2020 -10-16
 */
public interface BuOutsourceRateingService extends IService<BuOutsourceRateing> {
    /**
     * 分页
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return page
     */
    IPage<BuOutsourceRateing> page(BuOutsourceRateingQueryVO queryVO, Integer pageNo, Integer pageSize);


    /**
     * 修改
     *
     * @param outsourceRateing the outsource rateing
     * @return boolean
     */
    Boolean editRateing(BuOutsourceRateing outsourceRateing) throws Exception;

    /**
     * 批量删除
     *
     * @param asList the as list
     * @return boolean
     * @throws Exception the exception
     */
    boolean removeBatch(List<String> asList) throws Exception;

    /**
     * Save rateing boolean.
     *
     * @param outsourceRateing the outsource rateing
     * @return the boolean
     */
    Boolean saveRateing(BuOutsourceRateing outsourceRateing) throws Exception;
}
