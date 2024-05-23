package org.jeecg.common.tiros.wbs.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * wbs配置
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-29
 */
@Data
@Accessors(chain = true)
public class WbsConf implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "生成wbs类型 1:code字段拼接 2:阿拉伯数字排序拼接 默认1")
    private Integer wbsType;

    @ApiModelProperty(value = "表名", required = true)
    @NotBlank
    private String table;

    @ApiModelProperty(value = "连接字段 默认id")
    private String id;

    @ApiModelProperty(value = "连接字段-上级 默认parentId")
    private String parentId;

    @ApiModelProperty(value = "编码-拼接wbs使用 默认code")
    private String code;

    @ApiModelProperty(value = "wbs字段 默认wbs")
    private String wbs;

    @ApiModelProperty(value = "过滤条件 不带where的条件sql(如:status=0)")
    private String filter;

    @ApiModelProperty(value = "排序字段(默认code正序) 不带order的排序sql(如:code desc)")
    private String order;

    public WbsConf(String table) {
        this.table = table;
        this.wbsType = 1;
        this.id = "id";
        this.parentId = "parent_id";
        this.code = "code";
        this.wbs = "wbs";
    }

}
