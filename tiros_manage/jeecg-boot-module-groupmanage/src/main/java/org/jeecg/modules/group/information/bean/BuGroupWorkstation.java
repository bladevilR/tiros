package org.jeecg.modules.group.information.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 班组工位关联表
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "班组工位关联对象", description = "班组工位关联关系")
@TableName("bu_group_workstation")
public class BuGroupWorkstation {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "班组id", required = true)
    @NotBlank
    private String groupId;

    @ApiModelProperty(value = "工位id", required = true)
    @NotBlank
    private String workstationId;

}
