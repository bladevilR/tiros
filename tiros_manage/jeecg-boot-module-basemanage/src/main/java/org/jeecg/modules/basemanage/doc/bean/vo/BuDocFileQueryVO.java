package org.jeecg.modules.basemanage.doc.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class BuDocFileQueryVO implements Serializable {
    @ApiModelProperty(value = "文件名称")
    private String name;
    @ApiModelProperty(value = "文件类型")
    private String type;
    @ApiModelProperty(value = "目录id")
    private String id;
    @ApiModelProperty(hidden = true)
    private  String groupId;
    @ApiModelProperty(hidden = true)
    private  String personId;
    @ApiModelProperty(value = "显示类型,bu_file_show_type")
    private  Integer  showType;
    @ApiModelProperty(hidden = true)
    private  Boolean  isAdmin;
    @ApiModelProperty(hidden = true)
    private Integer directoryType;
    @ApiModelProperty(hidden = true)
    private List<String> roles;
}
