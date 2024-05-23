
package org.jeecg.common.workflow.bean;

import lombok.Data;

/**
 * <p>
 * 工作流 请求返回结果
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/29
 */
@Data
public class WorkFlowResult {

    private String code;
    private Object data;
    private String message;
    private Boolean success;

}
