package org.jeecg.modules.basemanage.org.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 规程明细bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/3
 */
@Data
@Accessors(chain = true)
public class RepairReguDetailBO {

    /**
     * 规程明细id
     */
    private String id;
    /**
     * 规程明细名称
     */
    private String title;
    /**
     * 规程明细类型 1作业分类 2作业项目
     */
    private Integer type;
    /**
     * 上级规程明细id
     */
    private String parentId;

}
