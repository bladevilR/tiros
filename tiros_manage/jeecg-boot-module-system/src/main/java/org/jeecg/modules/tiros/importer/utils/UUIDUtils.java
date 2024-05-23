package org.jeecg.modules.tiros.importer.utils;


import java.util.UUID;

/**
 * @author yyg
 */
public class UUIDUtils {

    public static  String getUUID(){

        return UUID.randomUUID().toString().replace("-", "");
    }
}
