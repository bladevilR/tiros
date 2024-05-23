package org.jeecg.modules.basemanage.doc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yyg
 */
@Data
public class BuFileAndDirectory implements Serializable {
    private  String  id ;
    @ApiModelProperty(value = "名称")
    private  String  name;
    @ApiModelProperty(value = "是否是文件,0 否，1，是")
    private  Integer isFile;
    @ApiModelProperty(value ="文件类型")
    private  String  type;
    @ApiModelProperty(value ="文件大小")
    private  Long  fileSize;
    @ApiModelProperty(value ="上传日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date  uploadDate;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "存储地址")
    private String  savepath;
    @ApiModelProperty(value = "标签")
    private String  fileTags;

}
