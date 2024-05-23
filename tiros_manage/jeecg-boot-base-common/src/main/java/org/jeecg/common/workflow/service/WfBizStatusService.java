package org.jeecg.common.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.workflow.bean.WfBizStatus;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务流程实列状态表 服务类
 * </p>
 *
 * @author youGen
 * @since 2020-10-27
 */
public interface WfBizStatusService extends IService<WfBizStatus> {

    /**
     * 根据映射编码查询业务流程存在的任务节点名称
     *
     * @param solutionCode 映射编码
     * @return 任务节点名称列表
     * @throws Exception 异常
     */
    List<String> listProcessStatusBySolutionCode(String solutionCode) throws Exception;

    /**
     * 通用流程状态改变接口，已修改业务流程状态表的值
     *
     * @param requestParam 请求参数
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean changeWfBizStatus(Map<String, Object> requestParam) throws Exception;

    /**
     * 根据业务主键列表和映射编码查询业务流程实列状态
     *
     * @param businessKeyList 业务主键列表
     * @param solutionCode    映射编码
     * @return 业务流程实列状态列表
     * @throws Exception 异常
     */
    List<WfBizStatus> listByBusinessKeyListAndSolutionCode(List<String> businessKeyList, String solutionCode) throws Exception;

}
