package org.jeecg.modules.basemanage.regu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguInfo;

import java.util.List;

/**
 * <p>
 * 规程信息 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-28
 */
public interface BuRepairReguInfoService extends IService<BuRepairReguInfo> {

    /**
     * 查询规程信息列表
     *
     * @param trainTypeId 所属车型id
     * @return 规程信息列表
     * @throws Exception 异常信息
     */
    List<BuRepairReguInfo> listReguInfo(String trainTypeId) throws Exception;

    /**
     * 根据规程id查询规程信息
     *
     * @param reguInfoId 规程id
     * @return 规程信息
     * @throws Exception 异常信息
     */
    BuRepairReguInfo getReguInfoById(String reguInfoId) throws Exception;

    /**
     * 新增或者修改规程
     *
     * @param reguInfo 规程信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveReguInfo(BuRepairReguInfo reguInfo) throws Exception;

    /**
     * 根据规程id复制规程信息
     *
     * @param reguInfoId 规程id
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean copyReguInfoByReguInfoId(String reguInfoId) throws Exception;

    /**
     * 根据规程id删除规程信息和规程明细
     *
     * @param reguId 规程id
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteById(String reguId) throws Exception;

    /**
     * 根据规程id判断是否关联计划模板
     *
     * @param reguId 规程id
     * @return 是否关联计划模板
     * @throws Exception 异常信息
     */
    boolean isRelativeWithTpPlan(String reguId) throws Exception;

}
