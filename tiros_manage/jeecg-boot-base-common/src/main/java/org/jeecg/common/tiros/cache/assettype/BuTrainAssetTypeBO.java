package org.jeecg.common.tiros.cache.assettype;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 设备类型bo
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-16
 */
@Data
@Accessors(chain = true)
public class BuTrainAssetTypeBO {

    private String id;
    private String name;
    private String shortName;
    private Integer structType;
    private String parentId;
    private List<String> childrenIdList;

    private String sysId;
    private String sysName;
    private String sysShortName;
    private String subSysId;
    private String subSysName;
    private String subSysShortName;

    private String trainTypeId;

}
