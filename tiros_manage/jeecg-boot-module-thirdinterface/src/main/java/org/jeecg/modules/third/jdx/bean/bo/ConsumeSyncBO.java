package org.jeecg.modules.third.jdx.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 同步记录bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/11/2
 */
@Data
@Accessors(chain = true)
public class ConsumeSyncBO {

    private String applyId;
    private String returnId;
    private Date syncTime;
    private String syncResult;
    private Date syncResultTime;

}
