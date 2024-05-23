package org.jeecg.modules.produce.trainhistory.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * id和name bo 用于根据id求name
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-04
 */
@Data
@Accessors(chain = true)
public class IdNameBO {
    private String id;
    private String name;
    private String upperId;
}
