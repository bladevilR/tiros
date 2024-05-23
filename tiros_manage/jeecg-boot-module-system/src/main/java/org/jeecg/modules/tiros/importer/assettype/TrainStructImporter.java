package org.jeecg.modules.tiros.importer.assettype;

import cn.hutool.db.sql.SqlExecutor;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.tiros.importer.utils.JDBCUtilsConfig;
import org.jeecg.modules.tiros.importer.utils.other.*;

import java.util.*;

/**
 * 车型结构导入
 */
public class TrainStructImporter {

    String assetTypeTemplate = "INSERT INTO bu_train_asset_type (id,train_type_id,code,wbs,name,parent_id,struct_type,init_num,asset_category_id,material_id,subjunctive,turnover,bom,sort_num,unit,status,place_id,place_desc,remark) VALUES ('%s','%s','%s','%s','%s',%s,%s,%s,'%s','%s',%s,%s,%s,'%s','%s',%s,'%s','%s','%s')";

    public void run(String filePath, String trainTypeId) {
        // TrainStructImporter trainStructImport = new TrainStructImporter();
        ImportUitl importUitl = new ImportUitl();
        List<Map<String,Object>> list= importUitl.importFile(filePath, 1);

       /* System.out.println();
        System.out.println("===================================================");
        System.out.println("总计导入行数：" + list.size());
        System.out.println("===================================================");*/

        DataBuildUtil buildUtil = new DataBuildUtil();
        List<DataRecord> records = buildUtil.buildTreeData(list, 1);

        /*System.out.println("====================================================");
        System.out.println("总计叶子节点：" + buildUtil.countLeafs(records));
        System.out.println("====================================================");*/

        /*for (DataRecord record : records) {
            record.show();
        }*/

        Map<Integer, String> leveMap = new HashMap<>(5);
        leveMap.put(0, "1:系统");
        leveMap.put(1, "2:子系统");
        leveMap.put(2, "3:部件");
        leveMap.put(3, "4:零件");
        // String trainTypeId = "";

        List<String> sqls = this.gen_AssetTypeSql(records,trainTypeId, leveMap, "NULL","", 3);

  /*      for (String sql : sqls) {
            System.out.println(sql);
        }
*/
        try {
            sqls.add(0, "delete from bu_train_asset_type where train_type_id='" + trainTypeId + "'");
            SqlExecutor.executeBatch(JDBCUtilsConfig.getConnection(), sqls);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 将结构型数据生成pb_assettype表的SQL语句
     * @param records
     * @param levelMap 基本对应的分类id和名称, value值的格式是： id:name, 如： 1:系统
     * @param parent 父级id，顶级节点时输入NULL， 不为NULL时，要用引号包裹，如： '34234234234'
     * @param deep 要生成的SQL深度
     * @return
     */
    public List<String> gen_AssetTypeSql(List<DataRecord> records, String trainTypeId, Map<Integer, String> levelMap, String parent, String parentWbs, int deep) {
        List<String> sqls = new ArrayList<>();
        for (DataRecord record : records) {
            String category = levelMap.get(record.getLevel());
            String tructTypeId = "";
            String tructTypeName = "";
            if (StringUtils.isNotBlank(category)) {
                try {
                    String[] arr = category.split(":");
                    tructTypeId = arr[0];
                    tructTypeName = arr[1];
                } catch (Exception ex) {
                    System.out.println("读取数据级别对应的分类id和名称异常：");
                    ex.printStackTrace();
                }
            }
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            String assetTypeName = record.getText();
            String remark = "";
            if (assetTypeName.length() > 32) {
                assetTypeName = ChineseUtil.returnChineseCharacter(assetTypeName, true);
                remark = record.getText();
            }
            String code = IdUtil.getRamdonString(10);

            String sortNum = "No." + record.getData().get("index");
            String wbs = code;
            if (StringUtils.isNotBlank(parentWbs)) {
                wbs = parentWbs + "." + code;
            }
            String sql = build_assetType_Sql(uuid, trainTypeId, code, wbs, assetTypeName, parent, tructTypeId, sortNum, remark);
            sqls.add(sql);

            if (record.getChildrens() != null && record.getChildrens().size() > 0 && record.getLevel() < deep) {
                sqls.addAll(gen_AssetTypeSql(record.getChildrens(), trainTypeId, levelMap, "'"+uuid+"'", wbs, deep));
            }
        }
        return sqls;
    }

    public String build_assetType_Sql(String uuid,
                                 String trainTypeId,
                                 String code,
                                 String wbs,
                                 String assetTypeName,
                                 String parentId,
                                 String structType,
                                      String sortNum,String remark) {
        return String.format(assetTypeTemplate,
                uuid,
                trainTypeId,
                code,
                wbs,
                assetTypeName,
                parentId,
                structType,
                "1", // init_num
                "", // asset_category_id
                "", // material_id
                "0", // subjunctive
                "0", // turnover
                "0", // bom
                sortNum,
                "", // unit
                "1", // status
                "", // place_id
                "", // place_desc
                remark // remark
        );
    }
}
