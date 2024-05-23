package org.jeecg.modules.dispatch.workorder.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 与工单作业记录表1对1关系
 * </p>
 *
 * @author youGen
 * @since 2020-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BuWorkOrderRecordAsset对象", description = "与工单作业记录表1对1关系")
public class BuWorkOrderRecordAsset extends Model<BuWorkOrderRecordAsset> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "所属作业记录表id")
    private String recordId;

    @ApiModelProperty(value = "设备类型id")
    private String assetTypeId;

    @ApiModelProperty(value = "车辆结构明细id")
    private String structDetailId;

    @ApiModelProperty(value = "设备ID")
    private String trainAssetId;

    @ApiModelProperty(value = "设备名称")
    private String trainAssetName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
