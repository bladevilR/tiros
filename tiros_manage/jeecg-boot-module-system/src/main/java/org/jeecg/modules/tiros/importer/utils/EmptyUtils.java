package org.jeecg.modules.tiros.importer.utils;

import java.util.List;

/**
 * @author yyg
 */
public class EmptyUtils {
    public  static  boolean listNotEmpty(List list){
        if(list!=null&&list.size()>0){
            return true;
        }

        return false;
    }
}
