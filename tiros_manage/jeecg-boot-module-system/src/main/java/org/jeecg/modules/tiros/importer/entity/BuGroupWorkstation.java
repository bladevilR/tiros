package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 工位信息
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuGroupWorkstation implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String groupId;
    private String workstationId;
}
