package org.jeecg.modules.tiros.importer.utils.other;


import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据处理类，处理成树结构
 */
public class DataBuildUtil {
    /**
     * 将数据列表处理成树形结构
     * @param list 数据列表
     * @param deep 树结构最大构造深度(0,1,....) , 只算到有子节点的层数
     * @return
     */
    public List<DataRecord> buildTreeData(List<Map<String, Object>> list, int deep) {
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        List<DataRecord> records = new ArrayList<>();
        for (Map<String, Object> map : list) {
            DataRecord parent = searchParent(records, map, deep);
            DataRecord record = new DataRecord();
            int curLevel = deep;
            if (parent == null) {
                curLevel = 0;  // 如果没有找到父级，那么默认他为顶级
                record.setLevel(curLevel);
                record.setText(map.get((curLevel) + "").toString());
                record.setData(map);
                if(curLevel <= deep) {
                    this.genChildren(record, deep);
                }
                records.add(record);
            } else {
                curLevel = parent.getLevel() + 1;
                record.setLevel(curLevel);
                record.setText(map.get((curLevel) + "").toString());
                record.setData(map);
                if(curLevel <= deep) {
                    this.genChildren(record, deep);
                }
                parent.getChildrens().add(record);
            }
        }
        clearEmpthyChild(records);
        return records;
    }

    /**
     * 生成这条记录下面子节点（还咩有到达最大层的时候）
     * @param node
     * @param deep
     */
    private void genChildren(DataRecord node, int deep) {
        DataRecord child = node.clone();
        int level = node.getLevel() + 1;
        child.setLevel(level);
        child.setText(node.getData().get(level + "").toString());
        child.setData(node.getData());
        node.getChildrens().add(child);

        if (level <= deep) {
            this.genChildren(child, deep);
        }
    }

    private DataRecord searchParent(List<DataRecord> records, Map<String, Object> map, int deep) {
        for (DataRecord record : records) {
            // 查找行对应列的名称 与该记录名称一样，说明查找行是该记录下的一个数据
            if (StringUtils.equals(record.getText(), map.get((record.getLevel()) + "").toString())) {
                // 当找到的一个父节点，且父节点子节点集合为空，说明第一次作为父节点，则要把自己也复制一份降级
                /*if (record.getChildrens().size() < 1) {
                    // 复制一份（显示下级标题）
                    DataRecord self = record.clone();
                    int selfLevel = self.getLevel() + 1;
                    // 基本降一级
                    self.setLevel(selfLevel);

                    if (self.getData() != null && self.getData().get(selfLevel + "") != null) {
                        self.setText(self.getData().get(selfLevel + "").toString());
                    }

                    record.getChildrens().add(self);
                }*/

                // 如果还没有达到最大深度，则在去子节点找, 因为有可能这条数据属于这个节点的子节点的子节点
                if (record.getLevel() < deep) {
                    DataRecord parent = searchParent(record.getChildrens(), map, deep);
                    if (parent == null) {
                        // 子集中没有相同的存在，返回本级节点
                        return record;
                    } else {
                        // 有则返回找到的父节点
                        return parent;
                    }
                } else {
                    return record;
                }
            }
        }
        return null;
    }

    private void clearEmpthyChild(List<DataRecord> records) {
        for (DataRecord record : records) {
            if (record.getChildrens() == null || record.getChildrens().size() == 0) {
                // 没有子节点的全部清空
                record.setChildrens(null);
            } else {
                clearEmpthyChild(record.getChildrens());
            }
        }
    }

    /**
     * 统计集合中的叶子节点
     * @param records
     * @return
     */
    public int countLeafs(List<DataRecord> records) {
        int sum = 0;
        for (DataRecord record : records) {
            if (record.getChildrens() == null || record.getChildrens().size() == 0) {
                sum++;
            } else {
                sum += countLeafs(record.getChildrens());
            }
        }
        return sum;
    }
}
