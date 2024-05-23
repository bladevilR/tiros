package org.jeecg.modules.tiros.importer.entity;

import lombok.Data;

/**
 * @author yyg
 */
@Data
public class BuWorkRecordCategory {


    private String id;
    private String  workRecId;
    private Integer  reguIndex;
    private  String   reguTitle;
    private  String    reguDetailId;
    private  String  parentId;
}
