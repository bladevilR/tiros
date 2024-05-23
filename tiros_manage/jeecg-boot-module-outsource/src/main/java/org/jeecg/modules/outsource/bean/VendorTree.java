package org.jeecg.modules.outsource.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yyg
 */
@Data
public class VendorTree implements Serializable {

    private String  name;
    private String id;
    private List<VendorTree> children;
}
