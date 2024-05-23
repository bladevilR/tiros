package org.jeecg.common.constant;

/**
 * @author: huangxutao
 * @date: 2019-06-14
 * @description: 缓存常量
 */
public interface CacheConstant {

    /**
     * 字典类型缓存
     */
    String SYS_DICT_TYPE_CACHE = "sys:cache:dict:type";
    /**
     * 字典信息缓存
     */
    String SYS_DICT_CACHE = "sys:cache:dict";
    /**
     * 表字典信息缓存
     */
    String SYS_DICT_TABLE_CACHE = "sys:cache:dictTable";

    /**
     * 数据权限配置缓存
     */
    String SYS_DATA_PERMISSIONS_CACHE = "sys:cache:permission:datarules";

    /**
     * 缓存用户信息
     */
    String SYS_USERS_CACHE = "sys:cache:user";

    /**
     * 全部部门信息缓存
     */
    String SYS_DEPARTS_CACHE = "sys:cache:depart:alldata";


    /**
     * 全部部门ids缓存
     */
    String SYS_DEPART_IDS_CACHE = "sys:cache:depart:allids";


    /**
     * 测试缓存key
     */
    String TEST_DEMO_CACHE = "test:demo";

    /**
     * 字典信息缓存
     */
    String SYS_DYNAMICDB_CACHE = "sys:cache:dbconnect:dynamic:";
    /**
     * 文档中心，树形目录
     */
    String DOC_DIC_TREE_CACHE = "doc:cache:dic:tree";

    /**
     * 车辆结构明细缓存
     */
    String TRAIN_STRUCT_DETAIL_TREE = "tiros:cache:struct:detail:tree:";

    /**
     * 车辆设备类型缓存
     */
    String TRAIN_ASSET_TYPE_CACHE_ALL = "tiros:cache:assettype:all";

    /**
     * 车辆设备类型缓存
     */
    String TRAIN_ASSET_TYPE_CACHE_SYS = "tiros:cache:assettype:sys";

    /**
     * 仓库信息缓存
     */
    String WAREHOUSE_CACHE_ALL = "tiros:cache:warehouse:all";

    /**
     * 字典缓存
     */
    String DICT_ITEM_CACHE = "tiros:cache:dict";

    /**
     * 工位缓存
     */
    String WORKSTATION_CACHE_ALL = "tiros:cache:workstation";

}
