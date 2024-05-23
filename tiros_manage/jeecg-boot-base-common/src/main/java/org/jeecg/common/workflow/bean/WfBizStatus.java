package org.jeecg.common.workflow.bean;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 业务流程实列状态表
 * </p>
 *
 * @author youGen
 * @since 2020-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WfBizStatus对象", description = "业务流程实列状态表")
@TableName("wf_biz_status")
public class WfBizStatus extends Model<WfBizStatus> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "为业务的主键")
    @TableId(value = "business_key", type = IdType.UUID)
    private String businessKey;

    @ApiModelProperty(value = "映射编码，业务与流程的关联编码，硬编码")
    private String solutionCode;

    @ApiModelProperty(value = "流程定义id")
    private String processDefinitionId;

    @ApiModelProperty(value = "流程实列id")
    private String processInstanceId;

    @ApiModelProperty(value = "当前流程节点编码")
    @TableField(value = "cur_node_code")
    private String curNodeCode;

    @ApiModelProperty(value = "当前流程节点名称")
    @TableField(value = "cur_node_name")
    private String curNodeName;

    @ApiModelProperty(value = "当前流程变量")
    @TableField(value = "cur_vars")
    private String curVars;

    @ApiModelProperty(value = "当前流程属性定义")
    @TableField(value = "cur_attrs")
    private String curAttrs;

    @ApiModelProperty(value = "未启动，.... 其他任务节点名称,  结束")
    private String processStatus;

    @ApiModelProperty(value = "当前候选人名称")
    private String processCandidate;

    @Override
    protected Serializable pkVal() {
        return this.businessKey;
    }

    public Map<String, Object> getVars() {
        if (StringUtils.isNotBlank(this.getCurVars())) {
            try {
                String jsonStr = this.getCurVars();
                JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
                Map<String, Object> map = new HashMap<>();
                jsonObject.entrySet().forEach(e->{
                    map.put(e.getKey(), e.getValue());
                });
                return map;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return new HashMap<>();
        } else {
            return new HashMap<>();
        }
    }

    public Map<String, Object> getAttrs() {
        try {
            String jsonStr = this.getCurAttrs();
            List<Map<String,String>> listObjectFir = (List<Map<String,String>>) JSONArray.parse(jsonStr);
            Map<String, Object> map = new HashMap<>();
            listObjectFir.forEach(e->{
                String key = e.get("attrKey");
                String value = e.get("attrValue");
                map.put(key, value);
            });
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }

}
