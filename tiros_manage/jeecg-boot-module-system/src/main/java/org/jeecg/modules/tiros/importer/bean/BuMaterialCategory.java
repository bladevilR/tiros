package org.jeecg.modules.tiros.importer.bean;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 物资类型分类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-19
 */
@Data
@Accessors(chain = true)
@ToString
public class BuMaterialCategory {
    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private Integer status;
    private String parentId;
    private String remark;

    private List<BuMaterialCategory> children;
//
//    /**
//     * 需要插入，用于excel导入
//     */
//    private boolean needInsert;

}
