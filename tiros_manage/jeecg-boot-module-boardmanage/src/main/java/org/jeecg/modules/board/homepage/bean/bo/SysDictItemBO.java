package org.jeecg.modules.board.homepage.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 字典项bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/11/24
 */
@Data
@Accessors(chain = true)
public class SysDictItemBO {

    private String itemText;
    private String itemValue;

}
