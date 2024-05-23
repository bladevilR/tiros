package org.jeecg.modules.outsource.training.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author youGen
 * @since 2021-05-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BuTrainingAnnex对象", description="")
public class BuTrainingAnnex extends Model<BuTrainingAnnex> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "培训id")
    private String trainingId;

    @ApiModelProperty(value = "文件id或标识 这里为minio中存放文件的id或标识，没有则不需要提供")
    private String fileId;

    @ApiModelProperty(value = "文件名称",required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "存放小写的文件扩展名称,如：.pdf",required = true)
    @NotBlank
    private String type;

    @ApiModelProperty(value = "文件路径 文件存放的路径，这里存放的是相对路径（包括存放在minio上也是相对路径）",required = true)
    @NotBlank
    private String savepath;

    @ApiModelProperty(value = "文件大小 单位为KB",required = true)
    @NotNull
    private Long fileSize;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
