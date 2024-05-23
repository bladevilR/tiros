package org.jeecg.modules.dispatch.workorder.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yyg
 */
@Data
public class DelayReasonVO implements Serializable {
    @NotBlank
    private String id;
    private String delayReason;
}
