package org.jeecg.modules.tiros.wbs.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * wbs更新po
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-29
 */
@Data
@Accessors(chain = true)
public class WbsPO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String parentId;
    private String code;
    private String wbs;
    private String parentWbs;
    private List<WbsPO> children;

    private String wbsConfTable;
    private String wbsConfId;
    private String wbsConfParentId;
    private String wbsConfCode;
    private String wbsConfWbs;
    private String wbsConfFilter;
    private String wbsConfOrder;

    private String newWbs;

}
