package org.jeecg.modules.tiros.importer.utils.other;


import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataRecord implements Cloneable {
    private int level;
    private String text;
    private Map<String, Object> data;
    private List<DataRecord> childrens;

    public DataRecord() {
        level = -1;
        childrens = new ArrayList<>();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<DataRecord> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<DataRecord> childrens) {
        this.childrens = childrens;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    protected DataRecord clone() {
        DataRecord record = new DataRecord();
        record.setLevel(this.level);
        record.setText(this.text);
        record.setData(this.data);
        record.setChildrens(new ArrayList<>());
        return record;
    }

    public void show() {
        String paddleft = StringUtils.repeat("-", 1 + (this.level - 1) * 3);
        System.out.println(paddleft + this.getText());
        if (this.getChildrens() !=null && this.getChildrens().size() > 0) {
            for (DataRecord record : this.getChildrens()) {
                record.show();
            }
        }
    }
}
