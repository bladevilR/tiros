package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 规程工艺文件,作业分类关联的文件会被下面的作业项继承
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuRepairReguTechFile  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String reguDetailId;
    /**
     * 来自文档中心的文件表id
     */
    private String fileId;
    private String fileName;
    private String savePath;
}
