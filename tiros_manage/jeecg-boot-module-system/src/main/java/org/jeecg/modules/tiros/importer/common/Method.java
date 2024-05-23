package org.jeecg.modules.tiros.importer.common;

/**
 * @author yyg
 */
public class Method {
    public static  String getMethodCode(String method){
        String methodCode="";
        switch (method) {
            case "目测":
            methodCode= "1";
            break;
            case "清洁":
                methodCode= "2";
            break;
            case "操作":
                methodCode= "3";
            break;
            case "检测":
                methodCode="4";
            break;
            case "测量":
                methodCode="5";
            break;
            case "耳听":
                methodCode="6";
                break;
            case "无损探伤":
                methodCode="7";
                break;
            default:
                break;

        }
        return methodCode;
    }
}
