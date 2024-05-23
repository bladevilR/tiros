package org.jeecg.modules.third.constant;

/**
 * <p>
 * 第三方接口 常量
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-09
 */
public interface MaximoThirdConstant {
//    /**
//     * 上级仓库id，为”二级(架大修)“仓库的id
//     */
//    String PARENT_WAREHOUSE_ID = "2";
    /**
     * 同步到maximo的数据类型，用于排序处理
     * 1新增工单 -> 7修改工单 -> 3物料消耗 -> 4物料退库 -> 6人员工时 -> 2关闭工单 -> 5新增故障 -> 8修改故障 -> 9删除故障
     */
    Integer[] TRANS_TO_MAXIMO_TYPES = {1, 7, 3, 4, 6, 2, 5, 8, 9};

//    /**
//     * 测试用的部门人员id
//     */
//    String TEST_WORKSHOP = "301090100";
//    String TEST_SITE = "SITE1";
//    String TEST_LINE = "Line1";
//    String TEST_GROUP = "301090110";
//    String TEST_USER = "200451";
//    String TEST_TRAIN_ID = "01020900000100014";

    /**
     * 默认架大修运营一公司
     */
    String DEFAULT_JDX_COMPANY = "YY1";
    /**
     * 架大修车间
     */
    String JDX_WORKSHOP_1 = "301090900";
    String JDX_WORKSHOP_2 = "301091200";
    /**
     * 默认车辆上级，现在所有车辆资产都在它下面
     */
    String TRAIN_PARENT_LINE123 = "01020";
    String TRAIN_PARENT_LINE4 = "01008";
    /**
     * 线路前缀
     */
    String LINE_PREFIX = "Line";
    /**
     * site前缀
     */
    String SITE_PREFIX = "SITE";

    String IFACENAME_TOOL = "JDX_REALASSET";
    String IFACENAME_STOCK = "JDX_INVBALANCES";
    String IFACENAME_STOCK_PRICE = "JDX_INVCOST_OUT";
    String IFACENAME_ORDER = "JDX_WO";
    String IFACENAME_CHANGE = "JDX_REALASSETTRANS";
    String IFACENAME_FAULT = "JDX_SR";
    String IFACENAME_FAULT_CODE = "JDX_FAILURELIST";
    String IFACENAME_FINANCE = "JDX_PROJECT";

    String SYS_CONFIG_UPDATE_WAREHOUSE_CACHE = "update_warehouse_cache";

}
