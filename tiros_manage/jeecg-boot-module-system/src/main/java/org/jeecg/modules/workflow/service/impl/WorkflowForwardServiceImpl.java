package org.jeecg.modules.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.threadlocal.ThreadLocalToken;
import org.jeecg.common.util.RestUtil;
import org.jeecg.common.workflow.bean.WorkFlowGroup;
import org.jeecg.common.workflow.bean.vo.*;
import org.jeecg.common.workflow.constant.WfConstant;
import org.jeecg.common.workflow.service.WorkflowForwardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程接口转发 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Slf4j
@Service
public class WorkflowForwardServiceImpl implements WorkflowForwardService {

    @Value("${tiros.wf.address}")
    private String serviceAddress;

    @Value("${tiros.wf.path.startSolution}")
    private String startSolutionPath;

    @Value("${tiros.wf.path.listCurrentTask}")
    private String listCurrentTaskPath;

    @Value("${tiros.wf.path.signal}")
    private String signalPath;

    @Value("${tiros.wf.path.completeTask}")
    private String completeTaskPath;

    @Value("${tiros.wf.path.stopProcess}")
    private String stopProcessPath;

    @Value("${tiros.wf.path.taskData}")
    private String taskDataPath;

    @Value("${tiros.wf.path.suspendInstance}")
    private String suspendInstance;

    @Value("${tiros.wf.path.activateInstance}")
    private String activateInstance;

    @Value("${tiros.wf.path.deleteInstance}")
    private String deleteInstance;

    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see WorkflowForwardService#startSolution(StartVO)
     */
    @Override
    public boolean startSolution(StartVO startVO) throws RuntimeException {
        // 请求路径
        String url = serviceAddress + startSolutionPath;
        JSONObject params = JSON.parseObject(JSON.toJSONString(startVO));

        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.POST, headers, null, params, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if ((null != code && code == 200) && (null != success && success)) {
            return true;
        } else {
            log.error("启动指定业务的流程未成功：参数=" + JSON.toJSONString(startVO) + "，错误信息如下=" + message);
            throw new JeecgBootException(message);
        }
    }

    /**
     * @see WorkflowForwardService#listCurrentUserProcessCanHandleTask(QueryCandidateVO)
     */
    @Override
    public List<TaskInfoVO> listCurrentUserProcessCanHandleTask(QueryCandidateVO queryCandidateVO) throws RuntimeException {
        // 请求路径
        String url = serviceAddress + listCurrentTaskPath;
        JSONObject variables = new JSONObject();
        variables.put("businessKey", queryCandidateVO.getBusinessKey());
        variables.put("solutionCode", queryCandidateVO.getSolutionCode());
        if (StringUtils.isNotBlank(queryCandidateVO.getProcessInstanceId())) {
            variables.put("processInstanceId", queryCandidateVO.getProcessInstanceId());
        }

        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.GET, headers, variables, null, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }

        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if ((null != code && code == 200) && (null != success && success)) {
            JSONArray jsonArray = result.getJSONArray("result");
            return JSONArray.parseArray(jsonArray.toJSONString(), TaskInfoVO.class);
        } else {
            log.error("获取当前用户指定流程的可办理业务未成功：参数=" + JSON.toJSONString(queryCandidateVO) + "，错误信息如下=" + message);
            throw new JeecgBootException(message);
        }
    }

    /**
     * @see WorkflowForwardService#signal(SignalVO)
     */
    @Override
    public boolean signal(SignalVO signalVO) throws RuntimeException {
        // 请求路径
        String url = serviceAddress + signalPath;
        JSONObject params = JSON.parseObject(JSON.toJSONString(signalVO));

        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.POST, headers, null, params, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if ((null != code && code == 200) && (null != success && success)) {
            return true;
        } else {
            log.error("发送信号的流程未成功：参数=" + JSON.toJSONString(signalVO) + "，错误信息如下=" + message);
            throw new JeecgBootException(message);
        }
    }

    /**
     * @see WorkflowForwardService#completeTask(TaskCompleteVO)
     */
    @Override
    public boolean completeTask(TaskCompleteVO taskCompleteVO) throws RuntimeException {
        // 请求路径
        String url = serviceAddress + completeTaskPath;
        JSONObject params = JSON.parseObject(JSON.toJSONString(taskCompleteVO));

        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.PUT, headers, null, params, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if ((null != code && code == 200) && (null != success && success)) {
            return true;
        } else {
            log.error("完成指定任务未成功：参数=" + JSON.toJSONString(taskCompleteVO) + "，错误信息如下=" + message);
            throw new JeecgBootException(message);
        }
    }

    /**
     * @see WorkflowForwardService#completeOrderFirstWorkflowTask(String, String, LoginUser, String, Map)
     */
    @Override
    public boolean completeOrderFirstWorkflowTask(String orderId, String orderCode, LoginUser sysUser, String scene, Map<String, Object> vars) throws RuntimeException {
        // 获取流程任务
        QueryCandidateVO queryCandidateVO = new QueryCandidateVO()
                .setBusinessKey(orderId)
                .setSolutionCode(WfConstant.SOLUTION_CODE_ORDER);
        List<TaskInfoVO> taskInfoVOList = listCurrentUserProcessCanHandleTask(queryCandidateVO);
        if (CollectionUtils.isEmpty(taskInfoVOList)) {
            throw new JeecgBootException("当前人员[" + sysUser.getRealname() + "]没有工单[" + orderCode + "]的可办理流程任务");
        }
        String message = StringUtils.isBlank(scene) ? "办理完毕" : scene;

        // 提交流程任务：获取任务列表第一个，提交
        TaskInfoVO taskInfoVO = taskInfoVOList.get(0);
        TaskCompleteVO taskCompleteVO = new TaskCompleteVO()
                .setTaskId(taskInfoVO.getId())
                .setUserId(sysUser.getId())
                .setMessage(message);
        if (null != vars && !vars.isEmpty()) {
            taskCompleteVO.setVars(vars);
        }
        completeTask(taskCompleteVO);

        return true;
    }

    /**
     * @see WorkflowForwardService#completeFaultFirstWorkflowTask(String, String, LoginUser, String)
     */
    @Override
    public boolean completeFaultFirstWorkflowTask(String faultId, String faultSn, LoginUser sysUser, String scene) throws RuntimeException {
        // 获取流程任务
        QueryCandidateVO queryCandidateVO = new QueryCandidateVO()
                .setBusinessKey(faultId)
                .setSolutionCode(WfConstant.SOLUTION_CODE_FAULT);
        List<TaskInfoVO> taskInfoVOList = listCurrentUserProcessCanHandleTask(queryCandidateVO);
        if (CollectionUtils.isEmpty(taskInfoVOList)) {
            throw new JeecgBootException("当前人员[" + sysUser.getRealname() + "]没有故障[" + faultSn + "]的可办理流程任务");
        }

        // 提交流程任务：获取任务列表第一个，提交
        TaskInfoVO taskInfoVO = taskInfoVOList.get(0);
        TaskCompleteVO taskCompleteVO = new TaskCompleteVO()
                .setTaskId(taskInfoVO.getId())
                .setUserId(sysUser.getId())
                .setMessage(scene + (StringUtils.isBlank(scene) ? "" : "时，") + "自动通过流程");
        completeTask(taskCompleteVO);

        return true;
    }

    /**
     * 停止/取消 流程流转，直接到结束节点
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    @Override
    public boolean stopProcess(String processInstanceId, String taskId) {
        // 请求路径
        String url = serviceAddress + stopProcessPath;

        Map<String, Object> params = new HashMap<>();
        params.put("processInstanceId", processInstanceId);
        params.put("taskId", taskId);
        JSONObject jsonParam = JSON.parseObject(JSON.toJSONString(params));

        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.PUT, headers, null, jsonParam, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if ((null != code && code == 200) && (null != success && success)) {
            return true;
        } else {
            log.error("终止流程未成功：参数=" + JSON.toJSONString(params) + "，错误信息如下=" + message);
            throw new JeecgBootException(message);
        }
    }

    @Override
    public Integer getTaskAttributes(String orderId, Integer orderType) {
        List<TaskInfoVO> taskInfoVOList = getTaskInfoVO(orderId, orderType);
        if (CollectionUtils.isNotEmpty(taskInfoVOList)) {
            String taskId = taskInfoVOList.get(0).getId();
            // 请求路径
            String url = serviceAddress + taskDataPath;
            Map<String, Object> params = new HashMap<>();
            params.put("taskId", taskId);
            JSONObject jsonParam = JSON.parseObject(JSON.toJSONString(params));
            // 发起请求
            HttpHeaders headers = initHeaders();
            ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.GET, headers, jsonParam, null, JSONObject.class);
            JSONObject result = responseEntity.getBody();

            // 处理结果
            if (null == result) {
                throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
            }
            Integer code = result.getInteger("code");
            Boolean success = result.getBoolean("success");
            String message = result.getString("message");
            JSONObject jsonObject = result.getJSONObject("result");
            if (null == jsonObject) {
                throw new JeecgBootException("找到不到该任务的相关信息");
            }
            if ((null != code && code == 200) && (null != success && success)) {
                JSONArray attributes = jsonObject.getJSONArray("attributes");
                Integer operator = null;
                if (!attributes.isEmpty()) {
                    // 核实工单 物料发放 物料确认 提交工单 专业工程师检查 关闭工单 1 2 3 4 6 5
                    List<AttributesVO> attributesVOList = JSONArray.parseArray(attributes.toJSONString(), AttributesVO.class);
                    AttributesVO attributesVO = attributesVOList.get(0);
                    String attrKey = attributesVO.getAttrKey();
                    Integer attrValue = attributesVO.getAttrValue();
                    if (attrValue == 1) {
                        switch (attrKey) {
                            case "isVerify":
                                operator = 1;
                                break;
                            case "isMaterial":
                                operator = 2;
                                break;
                            case "isConfirm":
                                operator = 3;
                                break;
                            case "isSubmit":
                                operator = 4;
                                break;
                            case "isCheck":
                                operator = 6;
                                break;
                            case "isClose":
                                operator = 5;
                                break;
                            default:
                                break;
                        }
                    }
                }
                return operator;
            } else {
                log.error("终止流程未成功：参数=" + JSON.toJSONString(params) + "，错误信息如下=" + message);
                throw new JeecgBootException(message);
            }

        }
        return null;
    }

    /**
     * @see WorkflowForwardService#completeMaterialApplyFirstWorkflowTask(String, String, LoginUser, String, Map)
     */
    @Override
    public boolean completeMaterialApplyFirstWorkflowTask(String id, String orderCode, LoginUser sysUser, String scene, Map<String, Object> vars) {
        // 获取流程任务
        QueryCandidateVO queryCandidateVO = new QueryCandidateVO()
                .setBusinessKey(id)
                .setSolutionCode(WfConstant.SOLUTION_CODE_ORDER_MATERIAL_APPLY);
        List<TaskInfoVO> taskInfoVOList = listCurrentUserProcessCanHandleTask(queryCandidateVO);
        if (CollectionUtils.isEmpty(taskInfoVOList)) {
            throw new JeecgBootException("当前人员[" + sysUser.getRealname() + "]没有工单[" + orderCode + "]的可办理流程任务");
        }
        String message = StringUtils.isBlank(scene) ? "办理完毕" : scene;

        // 提交流程任务：获取任务列表第一个，提交
        TaskInfoVO taskInfoVO = taskInfoVOList.get(0);
        TaskCompleteVO taskCompleteVO = new TaskCompleteVO()
                .setTaskId(taskInfoVO.getId())
                .setUserId(sysUser.getId())
                .setMessage(message);
        if (null != vars && !vars.isEmpty()) {
            taskCompleteVO.setVars(vars);
        }
        completeTask(taskCompleteVO);

        return true;
    }

    /**
     * @see WorkflowForwardService#completeWorkshopConsumeFirstWorkflowTask(String, String, LoginUser, String, Map)
     */
    @Override
    public boolean completeWorkshopConsumeFirstWorkflowTask(String id, String orderCode, LoginUser sysUser, String scene, Map<String, Object> vars) {
        // 获取流程任务
        QueryCandidateVO queryCandidateVO = new QueryCandidateVO()
                .setBusinessKey(id)
                .setSolutionCode(WfConstant.SOLUTION_CODE_ORDER_WORKSHOP_CONSUME);
        List<TaskInfoVO> taskInfoVOList = listCurrentUserProcessCanHandleTask(queryCandidateVO);
        if (CollectionUtils.isEmpty(taskInfoVOList)) {
            throw new JeecgBootException("当前人员[" + sysUser.getRealname() + "]没有工单[" + orderCode + "]的可办理流程任务");
        }
        String message = StringUtils.isBlank(scene) ? "办理完毕" : scene;

        // 提交流程任务：获取任务列表第一个，提交
        TaskInfoVO taskInfoVO = taskInfoVOList.get(0);
        TaskCompleteVO taskCompleteVO = new TaskCompleteVO()
                .setTaskId(taskInfoVO.getId())
                .setUserId(sysUser.getId())
                .setMessage(message);
        if (null != vars && !vars.isEmpty()) {
            taskCompleteVO.setVars(vars);
        }
        completeTask(taskCompleteVO);

        return true;
    }

    /**
     * @see WorkflowForwardService#suspendInstance(String)
     */
    @Override
    public boolean suspendInstance(String processInstanceId) {
        String url = serviceAddress + suspendInstance;
        Map<String, Object> params = new HashMap<>();
        params.put("processInstanceId", processInstanceId);
        JSONObject jsonParam = JSON.parseObject(JSON.toJSONString(params));
        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.PUT, headers, null, jsonParam, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if (code != 200) {
            throw new JeecgBootException("暂停流程服务失败");
        }
        return true;
    }

    /**
     * @see WorkflowForwardService#activateInstance(String)
     */
    @Override
    public boolean activateInstance(String processInstanceId) {
        String url = serviceAddress + activateInstance;
        Map<String, Object> params = new HashMap<>();
        params.put("processInstanceId", processInstanceId);
        JSONObject jsonParam = JSON.parseObject(JSON.toJSONString(params));
        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.PUT, headers, null, jsonParam, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if (code != 200) {
            throw new JeecgBootException("激活流程服务失败");
        }
        return true;
    }

    /**
     * @see WorkflowForwardService#deleteInstance(String)
     */
    @Override
    public boolean deleteInstance(String processInstanceId) {
        String url = serviceAddress + deleteInstance;
        Map<String, Object> params = new HashMap<>();
        params.put("processInstanceId", processInstanceId);
        params.put("cascade", true);
        JSONObject jsonParam = JSON.parseObject(JSON.toJSONString(params));
        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.DELETE, headers, jsonParam, null, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if (code != 200) {
            throw new JeecgBootException("删除流程服务失败");
        }
        return true;
    }

    /**
     * @see WorkflowForwardService#postDataToApi(WorkFlowGroup)
     */
    @Override
    public boolean postDataToApi(WorkFlowGroup workFlowGroup) {
        // 请求路径
        String url = serviceAddress + "/identity/sync";
        JSONObject params = JSON.parseObject(JSON.toJSONString(workFlowGroup));

        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.POST, headers, null, params, JSONObject.class);
        JSONObject result = responseEntity.getBody();

        // 处理结果
        if (null == result) {
            throw new JeecgBootException("请求工作流服务失败，请联系开发人员");
        }
        Integer code = result.getInteger("code");
        Boolean success = result.getBoolean("success");
        String message = result.getString("message");
        if ((null != code && code == 200) && (null != success && success)) {
            return true;
        } else {
            log.error("同步组织角色人员数据到工作流未成功：参数=" + JSON.toJSONString(workFlowGroup) + "，错误信息如下=" + message);
            throw new JeecgBootException(message);
        }
    }


    private List<TaskInfoVO> getTaskInfoVO(String orderId, Integer orderType) {
        // 请求路径
        String url = serviceAddress + listCurrentTaskPath;
        String solutionCode = WfConstant.SOLUTION_CODE_ORDER;
        if (orderType == 4) {
            solutionCode = WfConstant.SOLUTION_CODE_ORDER_MATERIAL_APPLY;
        }
        if (orderType == 5) {
            solutionCode = WfConstant.SOLUTION_CODE_ORDER_WORKSHOP_CONSUME;
        }
        JSONObject variables = new JSONObject();

        variables.put("businessKey", orderId);
        variables.put("solutionCode", solutionCode);

        // 发起请求
        HttpHeaders headers = initHeaders();
        ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.GET, headers, variables, null, JSONObject.class);
        JSONObject result = responseEntity.getBody();
        if (null != result) {
            Integer code = result.getInteger("code");
            Boolean success = result.getBoolean("success");
            String message = result.getString("message");
            if ((null != code && code == 200) && (null != success && success)) {
                JSONArray jsonArray = result.getJSONArray("result");
                return JSONArray.parseArray(jsonArray.toJSONString(), TaskInfoVO.class);
            }
        }
        return null;
    }


    private HttpHeaders initHeaders() {
        String authToken = getAuthToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AuthKey, authToken);
        headers.add("content-type", "application/json;charset=UTF-8");
        return headers;
    }

    private String getAuthToken() {
        String token = ThreadLocalToken.getCurrentToken();
        if (StringUtils.isBlank(token)) {
            LoginUser sysUser = sysBaseAPI.getUserByName("admin");
            token = JwtUtil.sign(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword());
            ThreadLocalToken.set(token);
        }

        return token;
    }

}
