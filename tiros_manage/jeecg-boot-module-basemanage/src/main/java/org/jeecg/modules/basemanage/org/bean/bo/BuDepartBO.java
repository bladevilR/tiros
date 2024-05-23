package org.jeecg.modules.basemanage.org.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-18
 */
@Data
@Accessors(chain = true)
public class BuDepartBO {

    private String id;
    private String departName;
    /**
     * 字典org_category 4=班组
     */
    private String orgCategory;
    private String orgCode;

    /**
     * 查询用户关联部门使用
     */
    private String userId;

}
