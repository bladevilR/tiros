package org.jeecg.modules.dispatch.workorder.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 规程明细bo--用于获取安全提示和技术要求
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/12/10
 */
@Data
@Accessors(chain = true)
public class BuRepairReguDetailBO {
    private String id;
    private String parentId;
    private String safeNotice;
    private String requirement;
    private List<BuRepairReguDetailBO> parentList;
    private Integer order;
}
