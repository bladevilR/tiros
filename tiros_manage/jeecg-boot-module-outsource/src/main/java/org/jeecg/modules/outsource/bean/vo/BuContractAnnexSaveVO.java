package org.jeecg.modules.outsource.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecg.modules.outsource.bean.BuContractAnnex;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 合同附件保存vo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-23
 */
@Data
@Accessors(chain = true)
public class BuContractAnnexSaveVO {

    @ApiModelProperty(value = "合同id", required = true)
    @NotBlank
    private String contractId;

    @ApiModelProperty(value = "合同附件列表")
    private List<BuContractAnnex> annexList;

    @ApiModelProperty(value = "是否删除以前的附件 默认是（删除）")
    private Boolean deleteBefore;

}
