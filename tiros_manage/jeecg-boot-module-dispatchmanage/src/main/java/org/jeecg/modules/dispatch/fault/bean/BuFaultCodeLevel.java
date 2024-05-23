package org.jeecg.modules.dispatch.fault.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yfy
 * @since 2021-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BuFaultCodeLevel对象", description="")
public class BuFaultCodeLevel extends Model<BuFaultCodeLevel> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private String categoryId;

    private String faultCodeId;

    private Integer levelValue;

    private String levelName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
