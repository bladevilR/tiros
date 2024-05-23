package org.jeecg.modules.produce.plan.bean.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 规程明细bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-25
 */
@Data
@Accessors(chain = true)
public class BuRepairReguDetailBO {
    private String id;
    private String no;
    private String title;
    private String parentId;
    private List<BuRepairReguDetailBO> children;
    private List<String> childIdList;
}
